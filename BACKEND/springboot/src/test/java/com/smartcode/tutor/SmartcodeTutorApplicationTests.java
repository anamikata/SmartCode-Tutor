
package com.smartcode.tutor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.smartcode.tutor.dto.CodeTranslationRequest;
import com.smartcode.tutor.dto.CodeTranslationResponse;
import com.smartcode.tutor.service.CodeTranslationService;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:smartcode-tutor-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SmartcodeTutorApplicationTests {

    @Autowired
    private CodeTranslationService translationService;

    @BeforeEach
    void setUp() {
        assertNotNull(translationService);
    }

    @Test
    void contextLoads() {
        // Confirms that the Spring Boot application context starts.
    }

    @Test
    void translatesPythonStringOutputToJava() {
        CodeTranslationResponse response = translate(
                "python", "java", "print(\"Hello World\")");

        assertTrue(response.isSuccess());
        assertTrue(response.getTranslatedCode().contains(
                "System.out.println(\"Hello World\");"));
        assertTrue(response.getTranslatedCode().contains(
                "public class Main"));
        assertTrue(response.getWarnings().isEmpty());
    }

    @Test
    void translatesPythonIntegerInputAndOutputToJava() {
        CodeTranslationResponse response = translate(
                "python", "java",
                "a = int(input())\nprint(a)");

        String javaCode = response.getTranslatedCode();

        assertTrue(response.isSuccess());
        assertTrue(javaCode.contains("import java.util.Scanner;"));
        assertTrue(javaCode.contains("Scanner scanner = new Scanner(System.in);"));
        assertTrue(javaCode.contains("int a = scanner.nextInt();"));
        assertTrue(javaCode.contains("System.out.println(a);"));
        assertTrue(javaCode.contains("scanner.close();"));
        assertTrue(response.getWarnings().isEmpty());
    }

    @Test
    void translatesPythonConditionalAndSingleQuotedStringToJava() {
        CodeTranslationResponse response = translate(
                "python", "java",
                "age = int(input())\n"
                        + "if age >= 18:\n"
                        + "    print('Adult')");

        String javaCode = response.getTranslatedCode();

        assertTrue(response.isSuccess());
        assertTrue(javaCode.contains("if (age >= 18) {"));
        assertTrue(javaCode.contains("System.out.println(\"Adult\");"));
        assertTrue(javaCode.contains("}"));
        assertTrue(response.getWarnings().isEmpty());
    }

    @Test
    void translatesPythonRangeLoopToJava() {
        CodeTranslationResponse response = translate(
                "python", "java",
                "for i in range(5):\n"
                        + "    print(i)");

        String javaCode = response.getTranslatedCode();

        assertTrue(response.isSuccess());
        assertTrue(javaCode.contains(
                "for (int i = 0; i < 5; i++) {"));
        assertTrue(javaCode.contains("System.out.println(i);"));
        assertTrue(javaCode.contains("public class Main"));
        assertTrue(response.getWarnings().isEmpty());
    }

    private CodeTranslationResponse translate(
            String sourceLanguage,
            String targetLanguage,
            String code) {

        CodeTranslationRequest request = new CodeTranslationRequest();
        request.setSourceLanguage(sourceLanguage);
        request.setTargetLanguage(targetLanguage);
        request.setCode(code);

        return translationService.translate(request);
    }
}
