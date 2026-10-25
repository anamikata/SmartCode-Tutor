
package com.smartcode.tutor.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class GeminiService {

    private final RestClient restClient;
    private final String apiKey;

    public GeminiService(
            @Value("${gemini.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(
                    "https://generativelanguage.googleapis.com/v1beta"
                )
                .defaultHeader(
                    HttpHeaders.CONTENT_TYPE,
                    MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    public String generateText(String prompt) {
        Map<String, Object> request = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        Map<?, ?> response;

        try {
            response = restClient.post()
                    .uri("/models/gemini-3.5-flash-lite:generateContent")
                    .header("x-goog-api-key", apiKey)
                    .body(request)
                    .retrieve()
                    .body(Map.class);

        } catch (RestClientResponseException exception) {
            HttpStatusCode status = exception.getStatusCode();

            if (status.value() == 401 || status.value() == 403) {
                throw new IllegalStateException(
                        "Gemini authentication failed. Check the server configuration."
                );
            }

            if (status.value() == 429) {
                throw new IllegalStateException(
                        "Gemini is rate-limiting requests. Please try again later."
                );
            }

            if (status.is5xxServerError()) {
                throw new IllegalStateException(
                        "Gemini is temporarily unavailable. Please try again later."
                );
            }

            throw new IllegalStateException(
                    "Gemini rejected the analysis request (HTTP "
                    + status.value() + ")."
            );

        } catch (RestClientException exception) {
            throw new IllegalStateException(
                    "Could not connect to Gemini. Please try again later."
            );
        }

        if (response == null) {
            throw new IllegalStateException(
                    "Gemini returned an empty response."
            );
        }

        Object candidatesObject = response.get("candidates");

        if (!(candidatesObject instanceof List<?> candidates)
                || candidates.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini returned no generated content."
            );
        }

        Object contentObject =
                ((Map<?, ?>) candidates.get(0)).get("content");

        if (!(contentObject instanceof Map<?, ?> content)) {
            throw new IllegalStateException(
                    "Gemini response did not contain content."
            );
        }

        Object partsObject = content.get("parts");

        if (!(partsObject instanceof List<?> parts)
                || parts.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini response did not contain text."
            );
        }

        Object textObject =
                ((Map<?, ?>) parts.get(0)).get("text");

        if (!(textObject instanceof String generatedText)
                || generatedText.isBlank()) {
            throw new IllegalStateException(
                    "Gemini returned no usable text."
            );
        }

        return generatedText;
    }
}
