package com.server.blockchainserver.platform.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "time_series")
public class TimeSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "endpoint")
    private String endpoint;
    @Column(name = "close")
    private double close;
    @Column(name = "date")
    private String date;
    @Column(name = "high")
    private double high;
    @Column(name = "low")
    private double low;
    @Column(name = "open")
    private double open;
    @Column(name = "request_time")
    private String request_time;
    @Column(name = "start_date")
    private String start_date;
    @Column(name = "end_date")
    private String end_date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public double getClose(double close) {
        return this.close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
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

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
