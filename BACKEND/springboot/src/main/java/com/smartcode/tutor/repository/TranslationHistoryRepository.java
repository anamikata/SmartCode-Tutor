
package com.smartcode.tutor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcode.tutor.entity.TranslationHistory;

public interface TranslationHistoryRepository
        extends JpaRepository<TranslationHistory, Long> {

    List<TranslationHistory> findAllByUserIdOrderByCreatedAtDesc(
            Long userId);
}
