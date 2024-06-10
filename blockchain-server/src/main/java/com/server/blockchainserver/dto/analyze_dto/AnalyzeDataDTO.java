package com.server.blockchainserver.dto.analyze_dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;


public class AnalyzeDataDTO {

    private Map<LocalDate, BigDecimal> data;

    public AnalyzeDataDTO() {
    }

    public AnalyzeDataDTO(Map<LocalDate, BigDecimal> data) {
        this.data = data;
    }

    public Map<LocalDate, BigDecimal> getData() {
        return data;
    }

    public void setData(Map<LocalDate, BigDecimal> data) {
        this.data = data;
    }
}
