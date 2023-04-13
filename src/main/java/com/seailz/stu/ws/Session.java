package com.seailz.stu.ws;

import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.CompletableFuture;

/**
 * Represents a session by a particular client.
 * <br>Each session is unique to a client.
 * @author Seailz
 */
public interface Session {

    /**
     * The unique ID of this session.
     */
    String id();

    WebSocketSession websocketSession();

}
