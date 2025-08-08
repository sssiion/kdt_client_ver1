package com.example.KDT_bank_server_project2.manager.Controller;

import com.example.KDT_bank_server_project2.manager.DTO.ChatMessageDto;
import com.example.KDT_bank_server_project2.manager.Entity.ChatMessage;
import com.example.KDT_bank_server_project2.manager.RabbitMQ.RabbitMQConfig;
import com.example.KDT_bank_server_project2.manager.Service.ChatMessageService;
import com.example.KDT_bank_server_project2.manager.Service.ChatRoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller // html 페이지 반환하는데 사용 , 전통적인 웹 애플리케이션 방식
@RequiredArgsConstructor
@Transactional
//템플릿 엔진으로 HTMl을 생성해서 보여줌
public class ChatController {
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final RabbitTemplate     rabbitTemplate;     // 🔥 핵심
    private final ObjectMapper objectMapper;

    @MessageMapping("/chat.sendMessage")
    // pyload : websocket 메시지 통신에  사용하고 STOMP 메시지임
    // RequestBody : REST API에 사용 HTTP 요청 , HTTP body에 담아서 보낸 Json. xml 데이터를  java 객체로 변환
    public void  sendMessage(@Payload ChatMessageDto dto)  { //throws JsonProcessingException

        ChatMessage saved =chatMessageService.saveMessage(
                dto.getUserId(),
                dto.getRoomId(),
                dto.getContent(),
                ChatMessage.MessageType.fromString(dto.getType())); // 메시지를 데이터 베이스에 저장
        ChatMessageDto reponseDto = new ChatMessageDto(saved);

        //String json = objectMapper.writeValueAsString(reponseDto);
        rabbitTemplate.convertAndSend(RabbitMQConfig.CHAT_EXCHANGE, "chat.room."+dto.getRoomId(),reponseDto); //exchate명, routing key, 실제 데이터
    }
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessageDto dto, SimpMessageHeaderAccessor headers)  {
        //세션에 사용자 정보 저장
        headers.getSessionAttributes().put("userId", dto.getUserId());
        headers.getSessionAttributes().put("roomId", dto.getRoomId());
        //방 참가 처리
        //chatRoomService.joinRoom(dto.getUserId(), dto.getRoomId());
        //ChatMessage join =chatMessageService.saveJoinMessage(dto.getUserId(), dto.getRoomId());
        //ChatMessageDto responseDto = new ChatMessageDto(join);
        //String json = objectMapper.writeValueAsString(responseDto);
        //rabbitTemplate.convertAndSend( RabbitMQConfig.CHAT_EXCHANGE, "chat.room."+dto.getRoomId()  );
    }
    @MessageMapping("/chat.removeUser")
    public void removeUser(@Payload ChatMessageDto dto,SimpMessageHeaderAccessor headers) {
        try{
            String userId = (String)headers.getSessionAttributes().get("userId");
            String roomId = (String)headers.getSessionAttributes().get("roomId");
            if(userId !=null && roomId !=null){
                chatRoomService.leaveRoom(userId,roomId);
                ChatMessage leaveMessage = chatMessageService.saveLeaveMessage(userId,roomId);

                ChatMessageDto responseDto = new ChatMessageDto(leaveMessage);
                String json = objectMapper.writeValueAsString(responseDto);
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.CHAT_EXCHANGE,
                        "chat.room."+roomId,
                        responseDto
                );
            }
        }catch(Exception e){
            System.err.println("사용자 퇴장 실패" + e.getMessage());
        }
    }


}
