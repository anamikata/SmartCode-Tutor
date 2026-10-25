
package com.smartcode.tutor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CodeSnippetRequest {

    @NotBlank(message = "Snippet title is required.")
    @Size(max = 100, message = "Title must not exceed 100 characters.")
    private String title;

    @NotBlank(message = "Programming language is required.")
    @Size(max = 30, message = "Language must not exceed 30 characters.")
    private String language;

    @NotBlank(message = "Code cannot be empty.")
    @Size(max = 20000, message = "Code must not exceed 20000 characters.")
    private String code;

    public CodeSnippetRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
