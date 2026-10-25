
package com.smartcode.tutor.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcode.tutor.entity.TranslationHistory;
import com.smartcode.tutor.entity.User;
import com.smartcode.tutor.repository.UserRepository;
import com.smartcode.tutor.service.TranslationHistoryService;

@RestController
@RequestMapping("/api/history")
public class TranslationHistoryController {

    private final TranslationHistoryService historyService;
    private final UserRepository userRepository;

    public TranslationHistoryController(
            TranslationHistoryService historyService,
            UserRepository userRepository) {
        this.historyService = historyService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Map<String, Object>> getHistory(Principal principal) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "User not found."));

        return historyService.getHistory(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Map<String, Object> toResponse(TranslationHistory history) {
        return Map.of(
                "id", history.getId(),
                "sourceLanguage", history.getSourceLanguage(),
                "targetLanguage", history.getTargetLanguage(),
                "originalCode", history.getOriginalCode(),
                "translatedCode", history.getTranslatedCode(),
                "createdAt", history.getCreatedAt().toString()
        );
    }
}
