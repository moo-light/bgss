package com.server.blockchainserver.models.enums;

public enum ELiveRateType {
    _1M("1 minute"),
    _5M("5 minutes"),
    _15M("15 minutes"),
    _30M("30 minutes"),
    _1H("1 hour"),
    _2H("2 hours"),
    _4H("4 hours"),
    D("1 day"),
    W("1 week"),
    M("1 month");

    private final String interval;
    private final String intervalTime;

    ELiveRateType(String interval) {
        this.interval = interval;
        this.intervalTime = interval.replaceFirst("(?<=\\d)\\s(?=\\w)", "000 ");
    }

    public String getInterval() {
        return interval;
    }

    public String getIntervalTime() {
        return intervalTime;
    }
}