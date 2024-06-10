package com.server.blockchainserver.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "analyze_transactions")
public class AnalyzeTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant date_time;
    private BigDecimal profit;

    public AnalyzeTransactions() {
    }

    public AnalyzeTransactions(Instant date_time, BigDecimal profit) {
        this.date_time = date_time;
        this.profit = profit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate_time() {
        return date_time;
    }

    public void setDate_time(Instant date_time) {
        this.date_time = date_time;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }
}
