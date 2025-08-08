package com.example.KDT_bank_server_project2.manager.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.time.LocalDateTime;

@Getter
@Entity //이 클래스는 데이터 베이스 테이블과 매핑되는 객체
// Entity가 하는 일 : JPA에게 이 클래스를 DB테이블로 관리해줘, Spring Boot 실행시 자동으로 테이블 생성, CRUD 연산을 자동으로 SQL로 변환
//@Entity가 없으면 그냥 일반 클래스로 JPA가 인식 못함
@Table(name="chat_messages")
@NoArgsConstructor  // 🔥 JPA에서 필수!
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    //하는일 : name, 실제 DB 컬럼명 지정 , unique 중복불가, nullable null 허용 여부(false: 필수입력) , length 문자열 최대 길이 설정
    // column 없는 경우 필두명 그대로 컬럼명 사용, 기본 설정 적용
    private String senderId;

    @Column(name="room_uuid", nullable = false)
    private String roomUuid;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(name="sent_at")
    private LocalDateTime sentAt;


    public enum MessageType{
        CHAT, JOIN, LEAVE;
        public static MessageType fromString(String type) {
            if (type == null) return CHAT;

            try {
                return MessageType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return CHAT;  // 잘못된 값이면 CHAT으로 기본 설정
            }
        }
    }
    @PrePersist
    //저장 직전에 현재 시간 자동 설정
    // 저장 직전에 자동으로 실행되는 메서드, 생성시간, 수정시간 등을 자동 설정, 개발자가 수동으로 시간을 설정할 필요 없음
    //객체 user > userRepository.save(user); 이 순간 oncreate() 자동 실행
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }

    public ChatMessage(String senderId, String roomUuid, String content, ChatMessage.MessageType type) {
        this.senderId = senderId;
        this.roomUuid = roomUuid;
        this.content = content;
        this.type = type;


    }


    public void setContent(String content) { this.content = content; }

    public void setType(MessageType type) { this.type = type; }

    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }



}
