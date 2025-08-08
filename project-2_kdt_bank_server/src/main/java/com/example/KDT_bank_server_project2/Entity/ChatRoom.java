package com.example.KDT_bank_server_project2.Entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //방 id

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "room_uuid", unique = true, nullable =false)
    private String roomUuid;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "max_participants")
    private Integer maxParticipants = 100;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
    public ChatRoom() {}
    public ChatRoom(String roomName, String creatorId){
        this.roomName = roomName;
        this.roomUuid = UUID.randomUUID().toString();
        this.creatorId = creatorId;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getRoomUuid() { return roomUuid; }


    public String getCreatorId() { return creatorId; }


    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }




}
