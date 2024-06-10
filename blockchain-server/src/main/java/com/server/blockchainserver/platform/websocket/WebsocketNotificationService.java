package com.server.blockchainserver.platform.websocket;

// import com.server.blockchainserver.platform.entity.ForexApiResponse;
import com.server.blockchainserver.platform.entity.ForexDataSockket;
// import com.server.blockchainserver.platform.entity.ForexLiveRate;
import com.server.blockchainserver.platform.entity.LiveRates;
import com.server.blockchainserver.platform.scheduling.ScheduledTasks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebsocketNotificationService {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(WebsocketNotificationService.class);

    // Call this method to send updates
//    public void notifyFrontend(final String message) {
//        template.convertAndSend("/api/auth/topic/liverates", message);
//    }

//    public void sendLiveRatesUpdate(LiveRates liveRates) {
//        messagingTemplate.convertAndSend("/api/auth/topic/liverates", liveRates);
//    }

    // public boolean sendLiveRatesForex(ForexLiveRate forex) {
    //     try {
    //         messagingTemplate.convertAndSend("/api/auth/topic/forex", forex);
    //         return true;
    //     } catch (Exception e) {
    //         logger.error("Failed to send live rates via WebSocket", e);
    //         return false;
    //     }
    // }

    public boolean sendLiveRatesForexAPI(ForexDataSockket forex) {
        try {
            messagingTemplate.convertAndSend("/api/auth/topic/forexsocket", forex);
            return true;
        } catch (Exception e) {
            logger.error("Failed to send live rates via WebSocket", e);
            return false;
        }
    }
}
