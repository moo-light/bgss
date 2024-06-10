// package com.server.blockchainserver.controllers.forex_controller;


// import com.server.blockchainserver.platform.entity.ForexApiResponse;
// import com.server.blockchainserver.platform.payload.response.ForexTimeSeriesResponse;
// import com.server.blockchainserver.platform.server.ForexServer;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;

// @CrossOrigin(origins = "*", maxAge = 3600)
// @RestController
// @RequestMapping("/api/auth")
// public class ForexController {

//     @Autowired
//     private ForexServer forexServer;

//     @GetMapping("/get-forex-rate")
//     public ForexApiResponse getForexRate()  {
//         return forexServer.fetchLiveGoldPriceForex();
//     }

//     //TimeSerries requests
//     @GetMapping("/get-forex-time-series")
//     public ForexTimeSeriesResponse getForexTimeSeries(@RequestParam String start_date,
//                                                       @RequestParam String end_date)  {
//         return forexServer.timeSeries(start_date, end_date);
//     }

// }
