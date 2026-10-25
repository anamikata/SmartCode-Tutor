
package com.smartcode.tutor.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcode.tutor.dto.CodeSnippetRequest;
import com.smartcode.tutor.entity.CodeSnippet;
import com.smartcode.tutor.entity.User;
import com.smartcode.tutor.repository.UserRepository;
import com.smartcode.tutor.service.CodeSnippetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/snippets")
public class CodeSnippetController {

    private final CodeSnippetService snippetService;
    private final UserRepository userRepository;

    public CodeSnippetController(
            CodeSnippetService snippetService,
            UserRepository userRepository) {
        this.snippetService = snippetService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(
            Principal principal,
            @Valid @RequestBody CodeSnippetRequest request) {

        CodeSnippet snippet = snippetService.create(
                getUserId(principal), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(snippet));
    }

    @GetMapping
    public List<Map<String, Object>> getAll(Principal principal) {
        return snippetService.getAll(getUserId(principal))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody CodeSnippetRequest request) {

        return toResponse(snippetService.update(
                getUserId(principal), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            Principal principal,
            @PathVariable Long id) {

        snippetService.delete(getUserId(principal), id);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.NOT_FOUND, "User not found."));
        return user.getId();
    }

    private Map<String, Object> toResponse(CodeSnippet snippet) {
        return Map.of(
                "id", snippet.getId(),
                "title", snippet.getTitle(),
                "language", snippet.getLanguage(),
                "code", snippet.getCode(),
                "createdAt", snippet.getCreatedAt().toString(),
                "updatedAt", snippet.getUpdatedAt().toString()
        );
    }
}
