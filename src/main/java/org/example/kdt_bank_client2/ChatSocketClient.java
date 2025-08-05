package org.example.kdt_bank_client2;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.function.Consumer;
@Component
public class ChatSocketClient {
    private final WebSocketStompClient stompClient;
    private StompSession session;
    private final ObjectMapper mapper = new ObjectMapper();

    public ChatSocketClient() {
        // SockJS 클라이언트 설정
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // ObjectMapper 설정
        mapper.findAndRegisterModules();
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

        session.subscribe("/topic/room." + roomId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                try {
                    ChatMessageDto message = mapper.readValue(payload.toString(), ChatMessageDto.class);
                    messageListener.accept(message);
                } catch (Exception e) {
                    System.err.println("메시지 파싱 예외: " + e.getMessage());
                }
            }
        });

        System.out.println("🔔 방 구독 완료: " + roomId);
    }

    public void subscribePersonal(String topic, Consumer<String> messageListener) {
        if (!isConnected()) {
            System.err.println("WebSocket이 연결되어 있지 않습니다.");
            return;
        }

        session.subscribe("/user/queue/" + topic, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
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
        session.send("/app/chat.sendMessage", message);
        System.out.println("📤 메시지 전송: " + message.getMessage());
    }

    public void enterRoom(ChatMessageDto joinMessage) {
        if (!isConnected()) {
            System.err.println("WebSocket이 연결되어 있지 않습니다.");
            return;
        }
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
            session.disconnect();
            session = null;
            System.out.println("🔌 WebSocket 연결 해제");
        }
    }

    public boolean isConnected() {
        return session != null && session.isConnected();
    }
}
