package com.seailz.stu.ws;

import org.json.JSONObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.CompletableFuture;

/**
 * Represents an authenticated session.
 */
public class AuthenticatedSession implements Session {

    private final String id;
    private final WebSocketSession session;

    private boolean hasHeartbeated;

    /**
     * Creates a new authenticated session.
     * <br>This requires an existing raw session.
     */
    public AuthenticatedSession(RawSession rawSession) {
        this.id = rawSession.id();
        this.session = rawSession.websocketSession();

        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(6500);
                    if (!session.isOpen()) break;
                    if (!hasHeartbeated) {
                        session.sendMessage(
                                new TextMessage(
                                        new JSONObject().put("op", 4).toString()
                                )
                        );

                        Thread.sleep(10000);
                        if (!hasHeartbeated) {
                            // close connection
                            session.close(CloseStatus.POLICY_VIOLATION);
                            break;
                        }
                    }
                    hasHeartbeated = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public WebSocketSession websocketSession() {
        return session;
    }

    public void heartbeat() {
        hasHeartbeated = true;
    }
}
