
package com.smartcode.tutor.service;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.springframework.stereotype.Service;

@Service
public class JavaCompilerService {

    public List<String> compileAndAnalyze(String code) {
        List<String> feedback = new ArrayList<>();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (compiler == null) {
            feedback.add(
                "Java compiler is unavailable. "
                + "Run the application with a full JDK."
            );
            return feedback;
        }

        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<>();

        String source = prepareSource(code);
        String className = findClassName(source);

        JavaFileObject sourceFile =
                new SourceFile(className, source);

        StringWriter output = new StringWriter();

        try {
            List<String> options = List.of(
                "-proc:none",
                "-Xlint:none",
                "--release", "25"
            );

            Boolean compiled = compiler.getTask(
                output,
                null,
                diagnostics,
                options,
                null,
                List.of(sourceFile)
            ).call();

            for (Diagnostic<? extends JavaFileObject> diagnostic
                    : diagnostics.getDiagnostics()) {

                if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                    feedback.add(
                        "Compiler error at line "
                        + diagnostic.getLineNumber()
                        + ": "
                        + diagnostic.getMessage(Locale.ENGLISH)
                    );
                }
            }

            if (Boolean.TRUE.equals(compiled)) {
                feedback.add(
                    "Java compiler check passed for the analyzed source."
                );
            }

        } catch (RuntimeException exception) {
            feedback.add(
                "Compiler analysis could not be completed."
            );
        }

        return feedback;
    }

    private String prepareSource(String code) {
        String trimmed = code.trim();

        // Keep complete Java source files unchanged.
        if (trimmed.matches(
                "(?s).*\\b(class|interface|enum|record)\\s+\\w+.*")) {
            return code;
        }

        // Wrap code snippets in a class and main method.
        return """
            class SmartCodeInput {
                public static void main(String[] args) {
                    %s
                }
            }
            """.formatted(code);
    }

    private String findClassName(String source) {
        Matcher matcher = Pattern.compile(
            "\\bpublic\\s+(?:(?:abstract|final|strictfp)\\s+)*"
            + "class\\s+(\\w+)"
        ).matcher(source);

        if (matcher.find()) {
            return matcher.group(1);
        }

        // Handle package-private classes and other type declarations.
        matcher = Pattern.compile(
            "\\b(?:class|interface|enum|record)\\s+(\\w+)"
        ).matcher(source);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "SmartCodeInput";
    }

    private static class SourceFile extends SimpleJavaFileObject {

        private final String source;

        SourceFile(String className, String source) {
            super(
                URI.create("string:///" + className + ".java"),
                Kind.SOURCE
            );
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(
                boolean ignoreEncodingErrors) {
            return source;
        }
    }
}
