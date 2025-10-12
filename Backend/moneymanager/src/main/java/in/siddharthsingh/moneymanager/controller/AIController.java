package in.siddharthsingh.moneymanager.controller;

import in.siddharthsingh.moneymanager.dto.ChatRequest;
import in.siddharthsingh.moneymanager.dto.ChatResponse;
import in.siddharthsingh.moneymanager.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AIController {

    private final AIService aiService;

    /**
     * Basic Q/A endpoint
     */
    @PostMapping("/ask")
    public String ask(@RequestBody String prompt) {
        return aiService.ask(prompt);
    }

    /**
     * Generate 2-3 personalized financial suggestions for a user
     */
    @GetMapping("/suggest/{profileId}")
    public ResponseEntity<List<String>> getSuggestions(@PathVariable Long profileId) {
        List<String> suggestions = aiService.generateSuggestions(profileId);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Simple chat endpoint (AI response) using ChatResponse DTO
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String response = aiService.ask(request.getPrompt());
        return ResponseEntity.ok(new ChatResponse(response));
    }

    /**
     * Chat endpoint with profile-specific financial context using ChatResponse DTO
     */
    @PostMapping("/chat/{id}")
    public ResponseEntity<ChatResponse> chat(
            @PathVariable Long id,
            @RequestBody ChatRequest request
    ) {
        String response = aiService.getChatResponse(id, request.getPrompt());
        return ResponseEntity.ok(new ChatResponse(response));
    }
}
