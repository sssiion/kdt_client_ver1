package com.example.KDT_bank_server_project2.Entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="room_participants")
public class RoomParticipant {

    @Id // id랑 사람 list
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="room_uuid", nullable = false)
    private String roomUuid;

    @Column(name = "user_id", nullable = false)
    private String  userId;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name="left_at")
    private LocalDateTime leftAt;

    @Column(name="is_active")
    private Boolean isActive;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
    public RoomParticipant() {}

    public RoomParticipant(String roomUuid, String userId) {
        this.roomUuid = roomUuid;
        this.userId= userId;
    }

    public Long getId() {return  this.id;}
    public String getRoomUuid() {return this.roomUuid;}


    public String getUserId() {return this.userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public LocalDateTime getJoinedAt() {return this.joinedAt;}
    public void setJoinedAt(LocalDateTime joinedAt) {this.joinedAt = joinedAt;}

    public LocalDateTime getLeftAt() {return this.leftAt;}
    public void setLeftAt(LocalDateTime leftAt) {this.leftAt = leftAt;}

    public Boolean getIsActive() {return this.isActive;}
    public void setIsActive(Boolean isActive) {this.isActive = isActive;}



}
