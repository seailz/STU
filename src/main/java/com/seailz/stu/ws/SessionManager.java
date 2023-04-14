package com.seailz.stu.ws;

import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;

public class SessionManager {
    public static HashMap<WebSocketSession, RawSession> rawSessions = new HashMap<>();
    public static HashMap<WebSocketSession, AuthenticatedSession> authenticatedSessions = new HashMap<>();

    public static void addRawSession(RawSession session) {
        rawSessions.put(session.websocketSession(), session);
    }

    public static void addAuthenticatedSession(AuthenticatedSession session) {
        authenticatedSessions.put(session.websocketSession(), session);
    }

    public static RawSession getRawSession(WebSocketSession session) {
        return rawSessions.get(session);
    }

    public static AuthenticatedSession getAuthenticatedSession(WebSocketSession session) {
        return authenticatedSessions.get(session);
    }

    public static void removeRawSession(WebSocketSession session) {
        rawSessions.remove(session);
    }

    public static void broadcastToAllSessions(JSONObject obj) {
        new Thread(() -> {
            for (AuthenticatedSession session : authenticatedSessions.values()) {
                if (session.websocketSession().isOpen()) {
                    try {
                        session.websocketSession().sendMessage(new TextMessage(obj.toString()));
                    } catch (IOException e) {
                        continue;
                    }
                }
            }
        }).start();
    }
}
