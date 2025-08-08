package com.example.KDT_bank_server_project2.manager.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import jakarta.persistence.*; //

import java.time.LocalDateTime;
import java.util.*;

@Entity //JPA에게 user 클래스를 DB 테이블과 매핑해줘
@Table(name="users") // 실제 DB 에서는 user 테이블을 사용해
@NoArgsConstructor  // 🔥 JPA에서 필수!
@AllArgsConstructor
@Setter
@Getter
public class User {

    @Getter
    @Id
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Getter
    @Column(name="user_authority")
    @Enumerated(EnumType.STRING)
    private UserType user_authority;
    //권한 유무

    @Getter
    @Column(name="user_phone", unique = true)
    private String userPhone;

    @Getter
    @Column(name="user_name")
    private String userName;


    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    @Column(name = "create_at")
    private LocalDateTime createAt;
    @Getter@Setter
    @Column(name="status")
    private UserStatus status;

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_joined_rooms", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "room_id")
    @Column(name = "room_name")
    private Map<String, String> joinedRooms= new HashMap<>();


    public List<String> getAllUserTypes() {
        List<String> list = new ArrayList<>();
        for(UserType i : UserType.values()){
            list.add(i.toString());
        }
        return list;
    }
    public enum UserStatus{
        out, meet, con, work, away, play, x;
        public static User.UserStatus fromString(String type) {
            if (type == null) return x;

            try {
                return User.UserStatus.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return x;  // 잘못된 값이면 CHAT으로 기본 설정
            }
        }
    }

    public enum UserType{
        BM, DM, DH,SC,AM, BT,CSM;
        public static User.UserType fromString(String type) {
            if (type == null) return BT;

            try {
                return User.UserType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return BT;  // 잘못된 값이면 CHAT으로 기본 설정
            }
        }
    }

    public User(String name, String userPhone){
        this.userId=  RandomStringUtils.randomAlphanumeric(6);
        this.userName = name;
        this.password = "1234";
        this.userPhone = userPhone;
        this.user_authority = UserType.BT;
        this.createAt = LocalDateTime.now();
        this.status = UserStatus.x;

        this.isOnline = false;
    }
    public boolean checkPassword(String password){
        return this.password != null && this.password.equals(password);
    }

    public void setOnline(){
        this.isOnline = true;
    }
    public void setOffline(){
        this.isOnline = false;
    }


    public boolean isOnline() { return isOnline; }

    public void joinedRooms(String roomId, String roomName){

        this.joinedRooms.put(roomId, roomName);
    }
    public void leaveRoom(String roomId){
        joinedRooms.remove(roomId);
    }

    public boolean isInRoom(String roomId){
        Map<String,String> joinedRooms = getJoinedRooms();
        return joinedRooms.containsKey(roomId);
    }


}
