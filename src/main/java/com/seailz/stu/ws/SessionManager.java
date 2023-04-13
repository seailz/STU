package com.seailz.stu.ws;

import org.springframework.web.socket.WebSocketSession;

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
}
