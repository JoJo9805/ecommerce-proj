package com.webthuongmai.controller;

import com.webthuongmai.dto.ChatRequest;
import com.webthuongmai.dto.ChatResponse;
import com.webthuongmai.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String reply = chatbotService.handleChat(request.getMessage());
        return ResponseEntity.ok(new ChatResponse(reply));
    }
}
