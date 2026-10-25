
package com.smartcode.tutor.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcode.tutor.dto.CodeAnalysisRequest;
import com.smartcode.tutor.dto.CodeAnalysisResponse;
import com.smartcode.tutor.service.CodeAnalysisService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/code")
public class CodeAnalysisController {

    private final CodeAnalysisService codeAnalysisService;

    public CodeAnalysisController(
            CodeAnalysisService codeAnalysisService) {
        this.codeAnalysisService = codeAnalysisService;
    }

    @PostMapping("/analyze")
    public CodeAnalysisResponse analyzeCode(
            @Valid @RequestBody CodeAnalysisRequest request) {

        List<String> feedback =
                codeAnalysisService.analyze(request.getCode());

        return new CodeAnalysisResponse(
                "Code analysis completed.",
                request.getLanguage(),
                true,
                feedback
        );
    }
}
