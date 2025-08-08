package org.example.kdt_bank_client2;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.kdt_bank_client2.DTO.ChatMessageDto;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
@Component
public class ChatSocketClient {
    private final WebSocketStompClient stompClient;
    private StompSession session;
    private final ThreadLocal<ObjectMapper> mapperThreadLocal = ThreadLocal.withInitial(() -> {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    });
    private final Map<String, StompSession.Subscription> subscriptions = new HashMap<>();

    public ChatSocketClient() {
        // SockJS 클라이언트 설정
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());


    }

    public void connect(String userId, Runnable onConnect, Consumer<String> onError) {
        stompClient.connectAsync("http://localhost:8080/ws", new StompSessionHandlerAdapter() {

            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                ChatSocketClient.this.session = session;
                System.out.println("✅ WebSocket 연결 성공: " + userId);
                onConnect.run();
            }

            @Override
            public void handleException(StompSession session, StompCommand command,
                                        StompHeaders headers, byte[] payload, Throwable exception) {
                System.err.println("❌ STOMP 예외: " + exception.getMessage());
                onError.accept(exception.getMessage());
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println("❌ Transport 예외: " + exception.getMessage());
                onError.accept("WebSocket 연결 오류: " + exception.getMessage());
            }
        });
    }

    public void subscribeRoom(String roomId, Consumer<ChatMessageDto> messageListener) {
        if (!isConnected()) {
            System.err.println("WebSocket이 연결되어 있지 않습니다.");
            return;
        }
        unsubscribeRoom(roomId);
        session.subscribe("/topic/chat.room." + roomId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessageDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    ObjectMapper mapper = mapperThreadLocal.get(); // ✅ 스레드별 ObjectMapper
                    ChatMessageDto message;

                    if (payload instanceof ChatMessageDto) {
                        message = (ChatMessageDto) payload;
                    } else {
                        message = mapper.readValue(payload.toString(), ChatMessageDto.class);
                    }

                    messageListener.accept(message);
                } catch (Exception e) {
                    System.err.println("메시지 처리 실패: " + e.getMessage());
                }
            }
        });

        System.out.println("🔔 방 구독 완료: " + roomId);
    }
    public void unsubscribeRoom(String roomId) {
        if (subscriptions.containsKey(roomId)) {
            subscriptions.get(roomId).unsubscribe();
            subscriptions.remove(roomId);
            System.out.println("🔕 방 구독 해제: " + roomId);
        }
    }
    public void subscribePersonal(String topic, Consumer<String> messageListener) {
        if (!isConnected()) {
            System.err.println("WebSocket이 연결되어 있지 않습니다.");
            return;
        }

        session.subscribe("/user/queue/" + topic, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Object.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                messageListener.accept(payload.toString());
            }
        });

        System.out.println("🔔 개인 큐 구독 완료: " + topic);
    }

    public void sendMessage(ChatMessageDto message) {
        if (!isConnected()) {
            System.err.println("WebSocket이 연결되어 있지 않습니다.");
            return;
        }
        session.send("/app/chat.sendMessage", message); // 기존과 동일
        System.out.println("📤 메시지 전송: " + message.getContent());
    }

    public void enterRoom(ChatMessageDto joinMessage) {
        if (!isConnected()) {
            System.err.println("WebSocket이 연결되어 있지 않습니다.");
            return;
        }
        // 🔥 수정: 서버 매핑 경로에 맞춤 (".chat.addUser" → "/app/chat.addUser")
        session.send("/app/chat.addUser", joinMessage);
        System.out.println("🚪 방 입장: " + joinMessage.getRoomId());
    }

    public void leaveRoom(ChatMessageDto leaveMessage) {
        if (!isConnected()) {
            System.err.println("WebSocket이 연결되어 있지 않습니다.");
            return;
        }
        session.send("/app/chat.removeUser", leaveMessage);
        System.out.println("🚪 방 퇴장: " + leaveMessage.getRoomId());
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            try {
                // 모든 구독 해제
                subscriptions.values().forEach(StompSession.Subscription::unsubscribe);
                subscriptions.clear();

                session.disconnect();
            } catch (Exception e) {
                System.err.println("연결 해제 중 오류: " + e.getMessage());
            } finally {
                session = null;
            }
        }
    }

    public boolean isConnected() {
        return session != null && session.isConnected();
    }
}
