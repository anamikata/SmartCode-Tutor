
package com.smartcode.tutor.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcode.tutor.dto.CodeTranslationRequest;
import com.smartcode.tutor.dto.CodeTranslationResponse;
import com.smartcode.tutor.entity.User;
import com.smartcode.tutor.repository.UserRepository;
import com.smartcode.tutor.service.CodeTranslationService;
import com.smartcode.tutor.service.TranslationHistoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/code")
public class CodeTranslationController {

    private final CodeTranslationService translationService;
    private final TranslationHistoryService historyService;
    private final UserRepository userRepository;

    public CodeTranslationController(
            CodeTranslationService translationService,
            TranslationHistoryService historyService,
            UserRepository userRepository) {
        this.translationService = translationService;
        this.historyService = historyService;
        this.userRepository = userRepository;
    }

    @PostMapping("/translate")
    public CodeTranslationResponse translate(
            @Valid @RequestBody CodeTranslationRequest request,
            Principal principal) {

        CodeTranslationResponse response =
                translationService.translate(request);

        if (response.isSuccess()) {
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Authenticated user was not found."));

            historyService.save(
                    user.getId(),
                    response.getSourceLanguage(),
                    response.getTargetLanguage(),
                    response.getOriginalCode(),
                    response.getTranslatedCode());
        }

        return response;
    }
}
