package com.server.blockchainserver.dto.analyze_dto;

import com.server.blockchainserver.models.AnalyzeBalance;
import com.server.blockchainserver.models.AnalyzeOrder;
import com.server.blockchainserver.models.AnalyzeTransactions;

public class DataSetAnalyze {
    private AnalyzeDataDTO analyzeBalance;
    private AnalyzeDataDTO analyzeOrder;
    private AnalyzeDataDTO analyzeTransactions;


    public DataSetAnalyze() {
    }

    public AnalyzeDataDTO getAnalyzeOrder() {
        return analyzeOrder;
    }

    public void setAnalyzeOrder(AnalyzeDataDTO analyzeOrder) {
        this.analyzeOrder = analyzeOrder;
    }

    public AnalyzeDataDTO getAnalyzeBalance() {
        return analyzeBalance;
    }

    public void setAnalyzeBalance(AnalyzeDataDTO analyzeBalance) {
        this.analyzeBalance = analyzeBalance;
    }

    public AnalyzeDataDTO getAnalyzeTransactions() {
        return analyzeTransactions;
    }

    public void setAnalyzeTransactions(AnalyzeDataDTO analyzeTransactions) {
        this.analyzeTransactions = analyzeTransactions;
    }
}
