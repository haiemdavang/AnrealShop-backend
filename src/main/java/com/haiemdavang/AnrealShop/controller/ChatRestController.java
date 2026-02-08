package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.chat.*;
import com.haiemdavang.AnrealShop.service.chat.ChatService;
import com.haiemdavang.AnrealShop.service.chat.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;
    private final ChatbotService chatbotService;

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getCurrentUserRooms() {
        List<ChatRoomResponse> rooms = chatService.getCurrentUserRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getRoomMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ChatMessageResponse> messages = chatService.getRoomMessages(roomId, page, size);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/rooms/init")
    public ResponseEntity<InitRoomResponse> initRoom(@Valid @RequestBody InitRoomRequest request) {
        InitRoomResponse response = chatService.initRoom(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bot")
    public ResponseEntity<ChatbotResponse> askChatbot(@Valid @RequestBody ChatbotRequest request) {
        ChatbotResponse response = chatbotService.askChatbot(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/bot/history")
    public ResponseEntity<Page<ChatbotHistoryResponse>> getChatbotHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ChatbotHistoryResponse> history = chatbotService.getHistory(page, size);
        return ResponseEntity.ok(history);
    }
}
