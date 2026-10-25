
package com.smartcode.tutor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CodeAnalysisRequest {

    @NotBlank(message = "Code cannot be empty.")
    @Size(max = 20000, message = "Code must not exceed 20000 characters.")
    private String code;

    @NotBlank(message = "Programming language is required.")
    @Pattern(
        regexp = "(?i)java",
        message = "Currently, only Java is supported."
    )
    private String language;

    public CodeAnalysisRequest() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
