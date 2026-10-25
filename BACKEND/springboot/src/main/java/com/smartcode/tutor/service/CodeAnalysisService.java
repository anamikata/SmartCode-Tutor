
package com.smartcode.tutor.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CodeAnalysisService {

    private final GeminiService geminiService;
    private final JavaCompilerService javaCompilerService;

    public CodeAnalysisService(
            GeminiService geminiService,
            JavaCompilerService javaCompilerService) {
        this.geminiService = geminiService;
        this.javaCompilerService = javaCompilerService;
    }

    public List<String> analyze(String code) {
        List<String> feedback = new ArrayList<>();

        if (code == null || code.isBlank()) {
            feedback.add("No Java code was provided.");
            return feedback;
        }

        // Rule 1: Check curly braces and parentheses.
        // Ignore comments and literals to reduce false positives.
        String sanitizedCode = removeCommentsAndLiterals(code);

        checkBrackets(
            sanitizedCode, '{', '}',
            "An extra closing brace '}' was found.",
            "One or more closing braces '}' may be missing.",
            feedback
        );

        checkBrackets(
            sanitizedCode, '(', ')',
            "An extra closing parenthesis ')' was found.",
            "One or more closing parentheses ')' may be missing.",
            feedback
        );

        // Rule 2: Check likely missing semicolons.
        String[] lines = code.split("\\R", -1);

        for (int i = 0; i < lines.length; i++) {
            String trimmedLine = lines[i].trim();

            // Remove a trailing single-line comment.
            int commentIndex = trimmedLine.indexOf("//");

            if (commentIndex >= 0) {
                trimmedLine =
                    trimmedLine.substring(0, commentIndex).trim();
            }

            boolean looksLikeDeclaration =
                trimmedLine.matches(
                    "(int|double|boolean|String|char|long|float|short|byte)\\s+\\w+\\s*=.*"
                );

            if (looksLikeDeclaration
                    && !trimmedLine.endsWith(";")
                    && !trimmedLine.endsWith("{")) {

                feedback.add(
                    "Line " + (i + 1)
                    + ": Possible missing semicolon ';'."
                );
            }
        }

        // Rule 3: Run Java compiler diagnostics.
        try {
            List<String> compilerFeedback =
                    javaCompilerService.compileAndAnalyze(code);

            feedback.addAll(compilerFeedback);
        } catch (Exception exception) {
            feedback.add(
                "Compiler analysis is temporarily unavailable."
            );
        }

        // Rule 4: Ask Gemini for deeper analysis.
        try {
            String prompt = """
                You are a helpful Java programming tutor.
                Analyze the Java code below.

                Provide concise feedback covering:
                1. Potential syntax errors.
                2. Logical bugs, if any.
                3. Code quality or readability improvements.
                4. A clear explanation suitable for a student.

                Do not claim that code was compiled or executed.
                If the code is incomplete, explain that limitation.
                Distinguish definite errors from possible concerns.
                Compiler diagnostics may be available separately.
                Do not claim that you independently compiled the code.

                Java code:
                ```java
                %s
                ```
                """.formatted(code);

            String aiFeedback =
                    geminiService.generateText(prompt);

            if (aiFeedback != null && !aiFeedback.isBlank()) {
                feedback.add(
                    "Gemini AI Analysis:\n" + aiFeedback
                );
            }
        } catch (Exception exception) {
            feedback.add(
                "AI analysis is temporarily unavailable. "
                + "Basic code checks were still performed."
            );
        }

        // Rule 5: Provide feedback if no issues were reported.
        if (feedback.isEmpty()) {
            feedback.add(
                "No issues detected by the available checks."
            );
        }

        return feedback;
    }

    private void checkBrackets(
            String code,
            char opening,
            char closing,
            String extraClosingMessage,
            String missingClosingMessage,
            List<String> feedback) {

        int count = 0;

        for (char character : code.toCharArray()) {
            if (character == opening) {
                count++;
            } else if (character == closing) {
                count--;

                if (count < 0) {
                    feedback.add(extraClosingMessage);
                    return;
                }
            }
        }

        if (count > 0) {
            feedback.add(missingClosingMessage);
        }
    }

    private String removeCommentsAndLiterals(String code) {
        StringBuilder result = new StringBuilder(code.length());

        boolean inString = false;
        boolean inChar = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;
        boolean escaped = false;

        for (int i = 0; i < code.length(); i++) {
            char current = code.charAt(i);
            char next = i + 1 < code.length()
                    ? code.charAt(i + 1) : '\0';

            if (inLineComment) {
                if (current == '\n' || current == '\r') {
                    inLineComment = false;
                    result.append(current);
                } else {
                    result.append(' ');
                }
                continue;
            }

            if (inBlockComment) {
                if (current == '*' && next == '/') {
                    result.append("  ");
                    i++;
                    inBlockComment = false;
                } else {
                    result.append(
                        current == '\n' || current == '\r'
                            ? current : ' '
                    );
                }
                continue;
            }

            if (inString || inChar) {
                result.append(
                    current == '\n' || current == '\r'
                        ? current : ' '
                );

                if (escaped) {
                    escaped = false;
                } else if (current == '\\') {
                    escaped = true;
                } else if (inString && current == '"') {
                    inString = false;
                } else if (inChar && current == '\'') {
                    inChar = false;
                }
                continue;
            }

            if (current == '/' && next == '/') {
                result.append("  ");
                i++;
                inLineComment = true;
            } else if (current == '/' && next == '*') {
                result.append("  ");
                i++;
                inBlockComment = true;
            } else if (current == '"') {
                result.append(' ');
                inString = true;
            } else if (current == '\'') {
                result.append(' ');
                inChar = true;
            } else {
                result.append(current);
            }
        }

        return result.toString();
    }
}
