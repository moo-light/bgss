package com.server.blockchainserver.platform.payload.response;

import com.server.blockchainserver.platform.payload.response.request.TimeSeriesQuote;

import java.util.List;

public class ForexTimeSeriesResponse {

    private String base_currency;
    private String end_date;
    private String endpoint;
    private List<TimeSeriesQuote> quotes;
    private String quote_currency;
    private String request_time;
    private String start_date;

    public String getBase_currency() {
        return base_currency;
    }

    public void setBase_currency(String base_currency) {
        this.base_currency = base_currency;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<TimeSeriesQuote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<TimeSeriesQuote> quotes) {
        this.quotes = quotes;
    }

    public String getQuote_currency() {
        return quote_currency;
    }

    public void setQuote_currency(String quote_currency) {
        this.quote_currency = quote_currency;
    }

    public String getRequest_time() {
        return request_time;
    }

    public void setRequest_time(String request_time) {
        this.request_time = request_time;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }
}
