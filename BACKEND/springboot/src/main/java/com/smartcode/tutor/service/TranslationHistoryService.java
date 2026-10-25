
package com.smartcode.tutor.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.smartcode.tutor.entity.TranslationHistory;
import com.smartcode.tutor.entity.User;
import com.smartcode.tutor.repository.TranslationHistoryRepository;
import com.smartcode.tutor.repository.UserRepository;

@Service
public class TranslationHistoryService {

    private final TranslationHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public TranslationHistoryService(
            TranslationHistoryRepository historyRepository,
            UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TranslationHistory save(
            Long userId,
            String sourceLanguage,
            String targetLanguage,
            String originalCode,
            String translatedCode) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found."));

        TranslationHistory history = new TranslationHistory();
        history.setUser(user);
        history.setSourceLanguage(sourceLanguage);
        history.setTargetLanguage(targetLanguage);
        history.setOriginalCode(originalCode);
        history.setTranslatedCode(translatedCode);

        return historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<TranslationHistory> getHistory(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found.");
        }

        return historyRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId);
    }
}
