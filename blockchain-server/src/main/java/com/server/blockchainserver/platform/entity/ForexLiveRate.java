// package com.server.blockchainserver.platform.entity;


// import jakarta.persistence.*;

// import java.time.LocalDateTime;

// @Entity
// @Table(name = "forex_live_rates")
// public class ForexLiveRate {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "entpoint")
//     private String endpoint;

//     @Column(name = "ask_price")
//     private Double ask;

//     @Column(name = "base_currency")
//     private String baseCurrency;

//     @Column(name = "bid_price")
//     private Double bid;

//     @Column(name = "mid_price")
//     private Double mid;

//     @Column(name = "quote_currency")
//     private String quoteCurrency;

//     @Column(name = "requested_time")
//     private String requestedTime;

//     @Column(name = "timestamp")
//     private Long timestamp;


//     public Long getId() {
//         return id;
//     }

//     public void setId(Long id) {
//         this.id = id;
//     }

//     public String getEndpoint() {
//         return endpoint;
//     }

//     public void setEndpoint(String endpoint) {
//         this.endpoint = endpoint;
//     }

//     public Double getAsk() {
//         return ask;
//     }

//     public void setAsk(Double ask) {
//         this.ask = ask;
//     }

//     public String getBaseCurrency() {
//         return baseCurrency;
//     }

//     public void setBaseCurrency(String baseCurrency) {
//         this.baseCurrency = baseCurrency;
//     }

//     public Double getBid() {
//         return bid;
//     }

//     public void setBid(Double bid) {
//         this.bid = bid;
//     }

//     public Double getMid() {
//         return mid;
//     }

//     public void setMid(Double mid) {
//         this.mid = mid;
//     }

//     public String getQuoteCurrency() {
//         return quoteCurrency;
//     }

//     public void setQuoteCurrency(String quoteCurrency) {
//         this.quoteCurrency = quoteCurrency;
//     }

//     public String getRequestedTime() {
//         return requestedTime;
//     }

//     public void setRequestedTime(String requestedTime) {
//         this.requestedTime = requestedTime;
//     }

//     public Long getTimestamp() {
//         return timestamp;
//     }

//     public void setTimestamp(Long timestamp) {
//         this.timestamp = timestamp;
//     }

//     @Override
//     public String toString() {
//         return "ForexLiveRate{" +
//                 "id=" + id +
//                 ", endpoint='" + endpoint + '\'' +
//                 ", ask=" + ask +
//                 ", baseCurrency='" + baseCurrency + '\'' +
//                 ", bid=" + bid +
//                 ", mid=" + mid +
//                 ", quoteCurrency='" + quoteCurrency + '\'' +
//                 ", requestedTime='" + requestedTime + '\'' +
//                 ", timestamp=" + timestamp +
//                 '}';
//     }
// }
