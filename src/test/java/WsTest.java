import org.json.JSONObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

public class WsTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new WsHandler();

        // Stops from shutting down
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private static class WsHandler extends TextWebSocketHandler {

        private WebSocketClient client;
        private WebSocketSession session;

        public WsHandler() throws ExecutionException, InterruptedException {
            WebSocketClient client = new StandardWebSocketClient();
            this.client = client;
            this.session = client.execute(this, new WebSocketHttpHeaders(), URI.create("ws://localhost:8080")).get();
            session.setTextMessageSizeLimit(1000000);
            session.setBinaryMessageSizeLimit(1000000);
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            System.out.println("Connected");
            session.sendMessage(
                    new TextMessage(
                            new JSONObject()
                                    .put("op", 0)
                                    .put("token", "hi lol").toString()
                    )
            );

            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        session.sendMessage(
                                new TextMessage(
                                        new JSONObject()
                                                .put("op", 1)
                                                .toString()
                                )
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            System.out.println("Disconnected");
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) {
            System.out.println("Received: " + message.getPayload());
        }
    }
}