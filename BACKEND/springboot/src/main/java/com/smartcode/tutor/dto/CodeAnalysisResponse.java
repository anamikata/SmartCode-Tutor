
package com.smartcode.tutor.dto;

import java.util.List;

public class CodeAnalysisResponse {

    private String message;
    private String language;
    private boolean success;
    private List<String> feedback;

    public CodeAnalysisResponse() {
    }

    public CodeAnalysisResponse(
            String message,
            String language,
            boolean success,
            List<String> feedback) {
        this.message = message;
        this.language = language;
        this.success = success;
        this.feedback = feedback;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<String> feedback) {
        this.feedback = feedback;
    }
}
