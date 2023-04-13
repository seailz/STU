package com.seailz.stu.ws;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

/**
 * Represents an un-authenticated session.
 */
public class RawSession implements Session {

    private final UUID id;
    private final WebSocketSession session;
    private boolean hasIdentified;

    public RawSession(WebSocketSession session) {
        this.id = UUID.randomUUID();
        this.session = session;

        // wait for identify payload
        new Thread(() -> {
            try {
                Thread.sleep(30000);
                if (!hasIdentified) {
                    System.out.println("Session " + id + " timed out.");
                    session.close(CloseStatus.POLICY_VIOLATION);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public String id() {
        return id.toString();
    }

    @Override
    public WebSocketSession websocketSession() {
        return session;
    }

    public boolean hasIdentified() {
        return hasIdentified;
    }

    public void identify() {
        hasIdentified = true;
    }
}
