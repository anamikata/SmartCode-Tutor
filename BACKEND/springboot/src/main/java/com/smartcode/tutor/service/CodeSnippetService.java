
package com.smartcode.tutor.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.smartcode.tutor.dto.CodeSnippetRequest;
import com.smartcode.tutor.entity.CodeSnippet;
import com.smartcode.tutor.entity.User;
import com.smartcode.tutor.repository.CodeSnippetRepository;
import com.smartcode.tutor.repository.UserRepository;

@Service
public class CodeSnippetService {

    private final CodeSnippetRepository snippetRepository;
    private final UserRepository userRepository;

    public CodeSnippetService(
            CodeSnippetRepository snippetRepository,
            UserRepository userRepository) {
        this.snippetRepository = snippetRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CodeSnippet create(Long userId, CodeSnippetRequest request) {
        User user = getUser(userId);

        CodeSnippet snippet = new CodeSnippet();
        snippet.setUser(user);
        applyRequest(snippet, request);

        return snippetRepository.save(snippet);
    }

    @Transactional(readOnly = true)
    public List<CodeSnippet> getAll(Long userId) {
        return snippetRepository.findAllByUserIdOrderByUpdatedAtDesc(userId);
    }

    @Transactional
    public CodeSnippet update(
            Long userId, Long snippetId, CodeSnippetRequest request) {
        CodeSnippet snippet = getOwnedSnippet(userId, snippetId);
        applyRequest(snippet, request);
        return snippetRepository.save(snippet);
    }

    @Transactional
    public void delete(Long userId, Long snippetId) {
        CodeSnippet snippet = getOwnedSnippet(userId, snippetId);
        snippetRepository.delete(snippet);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found."));
    }

    private CodeSnippet getOwnedSnippet(Long userId, Long snippetId) {
        return snippetRepository.findByIdAndUserId(snippetId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Snippet not found."));
    }

    private void applyRequest(
            CodeSnippet snippet, CodeSnippetRequest request) {
        snippet.setTitle(request.getTitle().trim());
        snippet.setLanguage(request.getLanguage().trim().toLowerCase());
        snippet.setCode(request.getCode());
    }
}
