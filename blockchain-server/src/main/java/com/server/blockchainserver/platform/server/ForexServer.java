// package com.server.blockchainserver.platform.server;

// import com.server.blockchainserver.platform.entity.ForexApiResponse;
// import com.server.blockchainserver.platform.entity.TimeSeries;
// import com.server.blockchainserver.platform.payload.response.ForexTimeSeriesResponse;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
// import org.springframework.web.client.RestTemplate;

// @Component
// public class ForexServer {

//     @Autowired
//     private RestTemplate restTemplate;

//     private final String api_key = "UHpDFkrzz7URaHCxioGV";
//     private final String forex_server = "https://marketdata.tradermade.com/api/v1/live";

//     private final String timeSeries = "https://marketdata.tradermade.com/api/v1/timeseries";


//     public ForexApiResponse fetchLiveGoldPriceForex() {
//         String currency = "XAUUSD";
//         String requestUrl = forex_server + "?currency=" + currency + "&api_key=" + api_key;
//         ForexApiResponse response = restTemplate.getForObject(requestUrl, ForexApiResponse.class);

//         return response;
//     }


//     public ForexTimeSeriesResponse timeSeries(String start_date, String end_date) {
//         String format = "records";
//         String currency = "XAUUSD";
//         String requestUrl = timeSeries + "?currency=" + currency + "&api_key=" + api_key + "&start_date=" +  start_date + "&end_date=" + end_date + "&format=" + format;
//         ForexTimeSeriesResponse response = restTemplate.getForObject(requestUrl, ForexTimeSeriesResponse.class);

//         return response;
//     }


//     public ForexTimeSeriesResponse timeSeriesSaveToDB(String start_date, String end_date) {
//         String format = "records";
//         String currency = "XAUUSD";
//         String requestUrl = timeSeries + "?currency=" + currency + "&api_key=" + api_key + "&start_date=" +  start_date + "&end_date=" + end_date + "&format=" + format;
//         ForexTimeSeriesResponse response = restTemplate.getForObject(requestUrl, ForexTimeSeriesResponse.class);
//         if (response != null) {
//             TimeSeries timeSeries = new TimeSeries();
//             timeSeries.setEndpoint(response.getEndpoint());
//             timeSeries.getClose(response.getQuotes().get(0).getClose());
//         }
//         return response;
//     }

// }
