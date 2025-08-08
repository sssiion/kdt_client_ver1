package com.example.KDT_bank_server_project2.manager.Controller;

import com.example.KDT_bank_server_project2.manager.DTO.ApiResponse;
import com.example.KDT_bank_server_project2.manager.DTO.ChatMessageDto;
import com.example.KDT_bank_server_project2.manager.DTO.UserResponseDto;
import com.example.KDT_bank_server_project2.manager.Entity.ChatMessage;
import com.example.KDT_bank_server_project2.manager.Entity.User;
import com.example.KDT_bank_server_project2.manager.Repository.ChatMessageRepository;
import com.example.KDT_bank_server_project2.manager.Service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Message")
@RequiredArgsConstructor
public class MessageController {
    private final ChatMessageService chatMessageService;

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getMessage(@PathVariable String roomId) {
        try{
            return ResponseEntity.ok(ApiResponse.success("사용자 조회 성공", chatMessageService.getRoomMessages(roomId)));

        }catch(IllegalArgumentException e){
            return ResponseEntity.notFound().build();
        }
    }




}
