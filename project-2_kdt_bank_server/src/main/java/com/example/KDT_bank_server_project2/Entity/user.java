package com.example.KDT_bank_server_project2.Entity;


import jakarta.persistence.*; //

import java.time.LocalDateTime;

@Entity //JPA에게 user 클래스를 DB 테이블과 매핑해줘
@Table(name="users") // 실제 DB 에서는 user 테이블을 사용해
public class user {
    @Id  // 이 필드가 primary key라고 JPA에게 알림
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long id;

    @Column(name="user_email", unique = true)
    private String userEmail;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name="joined_rooms", columnDefinition = "JSON")
    private String joinedRooms; // Map<String, String> 을 JSON으로 저장

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }

    public user(){ }

    public user(String userId, String password, String userEmail){
        this.userId= userId;
        this.password = "1234";
        this.userEmail = userEmail;
        this.isOnline = false;
    }
    public boolean checkPassword(String password){
        return this.password != null && this.password.equals(password);
    }
    public void setOnline(Boolean online){
        this.isOnline = online;
    }
    public void setOffline(){
        this.isOnline = false;
    }

    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isOnline() { return isOnline; }

    public LocalDateTime getCreateAt() { return createAt; }
    public void setCreateAt(LocalDateTime createAt) { this.createAt = createAt; }

    public String getJoinedRooms() { return joinedRooms; }
    public void setJoinedRooms(String joinedRooms) { this.joinedRooms = joinedRooms; }
}
