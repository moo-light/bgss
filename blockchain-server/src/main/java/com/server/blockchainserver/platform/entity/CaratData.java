package com.server.blockchainserver.platform.entity;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CaratData {

    private String base;
    private long timestamp;
    private Map<String, Double> data;
//    private LocalDateTime lastUpdated;

    public CaratData() {
    }

    public CaratData(String base, long timestamp, Map<String, Double> data) {
//        this.date = date;
        this.base = base;
        this.timestamp = timestamp;
        this.data = data;
    }

//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Double> getData() {
        return data;
    }

    public void setData(Map<String, Double> data) {
        this.data = data;
    }

    public double getCaratPrice(String karat) {
        if (data != null && data.containsKey(karat)) {
            return data.get(karat);
        }
        return -1.0;
    }

//    public LocalDateTime getLastUpdated() {
//        return lastUpdated;
//    }
//
//    public void setLastUpdated(LocalDateTime lastUpdated) {
//        this.lastUpdated = lastUpdated;
//    }
}
