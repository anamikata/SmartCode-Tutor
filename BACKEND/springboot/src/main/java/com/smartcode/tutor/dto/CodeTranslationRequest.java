
package com.smartcode.tutor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CodeTranslationRequest {

    @NotBlank(message = "Source code cannot be empty.")
    @Size(max = 20000, message = "Source code must not exceed 20000 characters.")
    private String code;

    @NotBlank(message = "Source language is required.")
    @Pattern(
        regexp = "(?i)java|c|c\\+\\+|python",
        message = "Source language must be Java, C, C++, or Python."
    )
    private String sourceLanguage;

    @NotBlank(message = "Target language is required.")
    @Pattern(
        regexp = "(?i)java|c|c\\+\\+|python",
        message = "Target language must be Java, C, C++, or Python."
    )
    private String targetLanguage;

    public CodeTranslationRequest() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
}
