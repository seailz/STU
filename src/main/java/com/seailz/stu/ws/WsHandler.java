package com.seailz.stu.ws;

import com.seailz.stu.ServiceTeamUnion;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WsHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        RawSession rawSession = new RawSession(session);
        SessionManager.addRawSession(rawSession);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject payload;

        try {
            payload = new JSONObject(message.getPayload());
        } catch (Exception e) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        if (!payload.has("op")) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        switch (payload.getInt("op")) {
            case 0: {
                RawSession rawSession = SessionManager.getRawSession(session);
                if (rawSession == null) {
                    session.close(CloseStatus.POLICY_VIOLATION);
                    return;
                }

                if (rawSession.hasIdentified()) {
                    session.close(CloseStatus.POLICY_VIOLATION);
                    return;
                }

                if (!payload.has("token")) {
                    session.close(CloseStatus.BAD_DATA);
                    return;
                }

                boolean isTokenValid = ServiceTeamUnion.validateToken(payload.getString("token"));
                if (!isTokenValid) {
                    session.close(CloseStatus.POLICY_VIOLATION);
                    return;
                }

                rawSession.identify();
                session.sendMessage(new TextMessage(new JSONObject().put("op", 2).toString()));
                SessionManager.removeRawSession(session);

                AuthenticatedSession authenticatedSession = new AuthenticatedSession(rawSession);
                SessionManager.addAuthenticatedSession(authenticatedSession);
                break;
            }
            case 1: {
                AuthenticatedSession authenticatedSession = SessionManager.getAuthenticatedSession(session);
                if (authenticatedSession == null) {
                    session.close(CloseStatus.POLICY_VIOLATION);
                    return;
                }

                authenticatedSession.heartbeat();
                session.sendMessage(
                        new TextMessage(
                                new JSONObject()
                                        .put("op", 5)
                                        .toString()
                        )
                );
            }
        }
    }
}
