package com.server.blockchainserver.platform.entity;

public class Quote {

    private Double ask;
    private String base_currency;
    private Double bid;
    private Double mid;
    private String quote_currency;

    public Double getAsk() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

    public String getBase_currency() {
        return base_currency;
    }

    public void setBase_currency(String base_currency) {
        this.base_currency = base_currency;
    }

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getMid() {
        return mid;
    }

    public void setMid(Double mid) {
        this.mid = mid;
    }

    public String getQuote_currency() {
        return quote_currency;
    }

    public void setQuote_currency(String quote_currency) {
        this.quote_currency = quote_currency;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "ask=" + ask +
                ", base_currency='" + base_currency + '\'' +
                ", bid=" + bid +
                ", mid=" + mid +
                ", quote_currency='" + quote_currency + '\'' +
                '}';
    }
}
