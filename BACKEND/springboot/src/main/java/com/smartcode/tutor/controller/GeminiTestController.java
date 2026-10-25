
package com.smartcode.tutor.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcode.tutor.service.GeminiService;

@RestController
@RequestMapping("/api/gemini-test")
public class GeminiTestController {

    private final GeminiService geminiService;

    public GeminiTestController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping
    public Map<String, String> testGemini() {
        String result = geminiService.generateText(
                "Reply with exactly: Gemini connection successful."
        );

        return Map.of("response", result);
    }
}
