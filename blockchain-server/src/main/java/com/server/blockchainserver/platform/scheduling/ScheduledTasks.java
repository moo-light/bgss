package com.server.blockchainserver.platform.scheduling;

import com.fasterxml.jackson.core.JsonProcessingException;
// import com.server.blockchainserver.platform.entity.ForexApiResponse;
// import com.server.blockchainserver.platform.entity.ForexLiveRate;
import com.server.blockchainserver.platform.entity.LiveRates;
import com.server.blockchainserver.platform.platform_services.services_interface.MetalPriceApiService;
// import com.server.blockchainserver.platform.repositories.ForexLiveRatesRepository;
import com.server.blockchainserver.platform.repositories.LiveRatesRepository;
// import com.server.blockchainserver.platform.server.ForexServer;
import com.server.blockchainserver.platform.websocket.WebsocketNotificationService;
import com.server.blockchainserver.utils.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@EnableScheduling
@Component
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private WebsocketNotificationService websocketNotificationService;

    @Autowired
    private MetalPriceApiService metalPriceService;

    @Autowired
    private LiveRatesRepository liveRatesRepository;

    // @Autowired
    // private ForexLiveRatesRepository forexLiveRatesRepository;

    // @Autowired
    // private ForexServer forexServer;

//    @Scheduled(fixedDelayString = "${schedule.delay.liveRates}", initialDelayString = "${schedule.initialDelay.liveRates}")
//    public void schedulingLiveRates() throws JsonProcessingException {
//        // LiveRatesResponse response = metalPriceService.getLiveGoldPrice();
//
//        // if (response != null && response.getRates() != null) {
//
//        // Double goldRate = response.getRates().get("XAU");
//
//        // if (goldRate != null) {
//        // LiveRates liveRates = new LiveRates();
//        // // Set BigDecimal value for rates, assuming the rate is "XAU"
//        // liveRates.setRates(BigDecimal.valueOf(goldRate)); // Set the gold rate
//        // liveRates.setBase(response.getBase()); // Set the base used in the API
//
//        // // Convert timestamp from response to LocalDateTime
//        // LocalDateTime date =
//        // LocalDateTime.ofInstant(Instant.ofEpochSecond(response.getTimestamp()),
//        // ZoneId.systemDefault());
//        // liveRates.setTimestamp(date);
//
//        // // Save to repository
//        // liveRatesRepository.save(liveRates);
//        // // After saving new live rates, notify the subscribed clients
//        // // Gson gson = new Gson();
//        // // String liveRatesJson = gson.toJson(liveRates);
//        // // logger.info(liveRatesJson);
//        // websocketNotificationService.sendLiveRatesUpdate(liveRates);
//        // // websocketNotificationService.notifyFrontend("New LiveRates available!");
//        // logger.info("Saved live gold price to database: {}", liveRates);
//        // }
//        // }
//
//        // Mock data response
//        LiveRates liveRates = ObjectHelper.getMockLiveRates();
//        // Save to repository
//        liveRatesRepository.save(liveRates);
//        // After saving new live rates, notify the subscribed clients
//        websocketNotificationService.sendLiveRatesUpdate(liveRates);
//        logger.info("Saved live gold price to database: {}", liveRates);
//
//    }

//     @Scheduled(fixedDelayString = "${schedule.delay.forex}", initialDelayString = "${schedule.initialDelay.liveRates}")
//     public void schedulingLiveRatesFores()  {

// //        ForexLiveRate forexLiveRate1 = forexLiveRatesRepository.findTopByOrderByIdDesc();
//         ForexLiveRate forexLiveRate = new ForexLiveRate();

//         ForexApiResponse forexApiResponse = forexServer.fetchLiveGoldPriceForex();
//         forexLiveRate.setEndpoint(forexApiResponse.getEndpoint());
//         forexLiveRate.setAsk(forexApiResponse.getQuotes().get(0).getAsk());
//         forexLiveRate.setBaseCurrency(forexApiResponse.getQuotes().get(0).getBase_currency());
//         forexLiveRate.setBid(forexApiResponse.getQuotes().get(0).getBid());
//         forexLiveRate.setMid(forexApiResponse.getQuotes().get(0).getMid());
//         forexLiveRate.setQuoteCurrency(forexApiResponse.getQuotes().get(0).getQuote_currency());
//         forexLiveRate.setRequestedTime(forexApiResponse.getRequested_time());
//         forexLiveRate.setTimestamp(forexApiResponse.getTimestamp());
//         forexLiveRatesRepository.save(forexLiveRate);
// //        forexLiveRatesRepository.findTopByOrderByIdDesc();
//         boolean success = websocketNotificationService.sendLiveRatesForex(forexLiveRate);
//         if (success) {
//             logger.info("socket connected successfully: {}", forexLiveRate);
//         }
//     }

//     @MessageMapping("/request-live-rates-forex")
//     public ForexLiveRate fetchAndSaveForexRates() {
//         ForexApiResponse forexApiResponse = forexServer.fetchLiveGoldPriceForex();
//         ForexLiveRate forexLiveRate = new ForexLiveRate();
//         forexLiveRate.setEndpoint(forexApiResponse.getEndpoint());
//         forexLiveRate.setAsk(forexApiResponse.getQuotes().get(0).getAsk());
//         forexLiveRate.setBaseCurrency(forexApiResponse.getQuotes().get(0).getBase_currency());
//         forexLiveRate.setBid(forexApiResponse.getQuotes().get(0).getBid());
//         forexLiveRate.setMid(forexApiResponse.getQuotes().get(0).getMid());
//         forexLiveRate.setQuoteCurrency(forexApiResponse.getQuotes().get(0).getQuote_currency());
//         forexLiveRate.setRequestedTime(forexApiResponse.getRequested_time());
//         forexLiveRate.setTimestamp(forexApiResponse.getTimestamp());
//         forexLiveRatesRepository.save(forexLiveRate);
//         boolean success = websocketNotificationService.sendLiveRatesForex(forexLiveRate);
//         if (success) {
//             logger.info("socket connected successfully: {}", forexLiveRate);
//         }
//         return forexLiveRate;
//     }
}
