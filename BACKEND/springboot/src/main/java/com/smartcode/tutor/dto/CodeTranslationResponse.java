
package com.smartcode.tutor.dto;

import java.util.List;

public class CodeTranslationResponse {

    private String sourceLanguage;
    private String targetLanguage;
    private String originalCode;
    private String translatedCode;
    private List<String> explanations;
    private List<String> warnings;
    private boolean success;
    private String message;

    public CodeTranslationResponse() {
    }

    public CodeTranslationResponse(
            String sourceLanguage,
            String targetLanguage,
            String originalCode,
            String translatedCode,
            List<String> explanations,
            List<String> warnings,
            boolean success,
            String message) {

        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.originalCode = originalCode;
        this.translatedCode = translatedCode;
        this.explanations = explanations;
        this.warnings = warnings;
        this.success = success;
        this.message = message;
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

    public String getOriginalCode() {
        return originalCode;
    }

    public void setOriginalCode(String originalCode) {
        this.originalCode = originalCode;
    }

    public String getTranslatedCode() {
        return translatedCode;
    }

    public void setTranslatedCode(String translatedCode) {
        this.translatedCode = translatedCode;
    }

    public List<String> getExplanations() {
        return explanations;
    }

    public void setExplanations(List<String> explanations) {
        this.explanations = explanations;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
