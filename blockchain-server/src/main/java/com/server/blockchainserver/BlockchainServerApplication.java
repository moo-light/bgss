package com.server.blockchainserver;

import com.server.blockchainserver.platform.entity.ForexDataSockket;
import com.server.blockchainserver.platform.websocket.WebsocketNotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.*;

import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootApplication
@Configuration
//@EnableScheduling
@EnableAsync
//@CrossOrigin(origins = "*", maxAge = 3600)
public class BlockchainServerApplication {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    private static volatile boolean stop = false;

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = SpringApplication.run(BlockchainServerApplication.class, args);
 //       WebsocketNotificationService websocketNotificationService = applicationContext.getBean(WebsocketNotificationService.class);
//        CountDownLatch latch = new CountDownLatch(1);
        connectWebSocket(applicationContext);

        // Replace the while loop with a cleaner shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stop = true));
//        WebSocket ws = HttpClient
//                .newHttpClient()
//                .newWebSocketBuilder()
//                .buildAsync(URI.create("wss://marketdata.tradermade.com/feedadv"), new WebSocketClient(websocketNotificationService))
//                .join();
//        while(true){}
    }

    private static void connectWebSocket(ApplicationContext applicationContext) {
        WebsocketNotificationService websocketNotificationService = applicationContext.getBean(WebsocketNotificationService.class);

        newWebSocket(websocketNotificationService);

        // Wait until stop signal received to terminate
        while (!stop) {
            // Sleep a bit to avoid busy waiting
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void newWebSocket(WebsocketNotificationService websocketNotificationService) {
        WebSocketClient client = new WebSocketClient(websocketNotificationService);
        HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create("wss://marketdata.tradermade.com/feedadv"), client)
                .thenAccept(client::setWebSocket)
                .join();
    }

    private static class WebSocketClient implements WebSocket.Listener {
        private WebSocketClient(WebsocketNotificationService websocketNotificationService) {
            this.websocketNotificationService = websocketNotificationService;
        }

        public void setWebSocket(WebSocket webSocket) {
            //        CountDownLatch latch,
            webSocket.sendText("{\"userKey\":\"wspbLDK35hN5mkn8EiHw\", \"symbol\":\"XAUUSD\"}", true);
        }
        private WebsocketNotificationService websocketNotificationService;
        public void onOpen(WebSocket webSocket) {
                System.out.println("onOpen using subprotocol " + webSocket.getSubprotocol());
                WebSocket.Listener.super.onOpen(webSocket);
                webSocket.sendText("{\"userKey\":\"wsJOkWdBPD4NJqzf4P3g\", \"symbol\":\"XAUUSD\"}", true);
            }

            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                String dataString = data.toString();
//                System.out.println("onText received " + dataString);

                if (dataString.startsWith("{") && dataString.endsWith("}")) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        ForexDataSockket forexData = mapper.readValue(dataString, ForexDataSockket.class);
                        websocketNotificationService.sendLiveRatesForexAPI(forexData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Received non-JSON message: " + dataString);
                }
                return WebSocket.Listener.super.onText(webSocket, data, last);
            }

            public void onError(WebSocket webSocket, Throwable error) {
                System.out.println("Bad day! " + webSocket.toString());
                webSocket.abort();
                if (!stop) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    newWebSocket(websocketNotificationService);
                }
                WebSocket.Listener.super.onError(webSocket, error);
            }

        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            System.out.println("WebSocket connection closed. Attempting to reconnect...");
            if (!stop) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                newWebSocket(websocketNotificationService);
            }
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }
        }

}
