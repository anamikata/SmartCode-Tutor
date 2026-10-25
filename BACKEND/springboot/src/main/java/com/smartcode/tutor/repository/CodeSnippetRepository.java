
package com.smartcode.tutor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcode.tutor.entity.CodeSnippet;

public interface CodeSnippetRepository
        extends JpaRepository<CodeSnippet, Long> {

    List<CodeSnippet> findAllByUserIdOrderByUpdatedAtDesc(Long userId);

    Optional<CodeSnippet> findByIdAndUserId(Long id, Long userId);
}
