package com.server.blockchainserver.platform.entity;

public class ForexDataSockket {
    private String symbol;
    private String ts;
    private double bid;
    private double ask;
    private double mid;

    public ForexDataSockket() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    @Override
    public String toString() {
        return "ForexDataSocket{" +
                "symbol='" + symbol + '\'' +
                ", ts='" + ts + '\'' +
                ", bid=" + bid +
                ", ask=" + ask +
                ", mid=" + mid +
                '}';
    }
}
