package com.example.KDT_bank_server_project2.Entity;

import jakarta.persistence.*;

import java.awt.*;
import java.time.LocalDateTime;

@Entity
@Table(name="chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name="room_uuid", nullable = false)
    private String roomUuid;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private TrayIcon.MessageType type;

    @Column(name="sent_at")
    private LocalDateTime sentAt;


    @Column(name="receiver_id")
    private String receiverId;

    public enum MessageType{
        CHAT, JOIN, LEAVE, PRIVATE
    }
    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }
    public ChatMessage() {}

    public ChatMessage(String senderId, String roomUuid, String content, TrayIcon.MessageType type, String receiverId) {
        this.senderId = senderId;
        this.roomUuid = roomUuid;
        this.content = content;
        this.type = type;
        this.receiverId = receiverId;
    }
    public Long getId() { return id;}


    public String getSenderId() { return senderId;}


    public String getRoomUuid() { return roomUuid;}


    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public TrayIcon.MessageType getType() { return type; }
    public void setType(TrayIcon.MessageType type) { this.type = type; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public String getReceiverId() { return receiverId; }


}
