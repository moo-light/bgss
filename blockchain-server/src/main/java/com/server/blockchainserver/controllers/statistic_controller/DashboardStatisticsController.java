package com.server.blockchainserver.controllers.statistic_controller;

import com.server.blockchainserver.advices.Response;
import com.server.blockchainserver.dto.analyze_dto.AnalyzeDataDTO;
import com.server.blockchainserver.dto.analyze_dto.DataSetAnalyze;
import com.server.blockchainserver.models.AnalyzeBalance;
import com.server.blockchainserver.models.AnalyzeOrder;
import com.server.blockchainserver.models.AnalyzeTransactions;
import com.server.blockchainserver.payload.request.FromAndToRequest;
import com.server.blockchainserver.payload.response.DashboardOrderResponse;
import com.server.blockchainserver.payload.response.DashboardTransactionResponse;
import com.server.blockchainserver.payload.response.DashboardWithdrawResponse;
import com.server.blockchainserver.payload.response.QuantityStatisticResponse;
import com.server.blockchainserver.repository.AnalyzeBalanceRepository;
import com.server.blockchainserver.repository.AnalyzeOrderRepository;
import com.server.blockchainserver.repository.AnalyzeTransactionsRepository;
import com.server.blockchainserver.services.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class DashboardStatisticsController {

    @Autowired
    private Services statisticService;
    @Autowired
    private AnalyzeBalanceRepository analyzeBalanceRepository;
    @Autowired
    private AnalyzeTransactionsRepository analyzeTransactionsRepository;
    @Autowired
    private AnalyzeOrderRepository analyzeOrderRepository;

    // PARAMETER type = {DAY, WEEK, MONTH,QUARTER,YEAR}
    //Sales of Orders
    @GetMapping("/calculate-order-sales")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> calculateOrderSalesStatistics(@RequestParam String type, @RequestParam(required = false) Long monthChose,
                                                                  @RequestParam(required = false) Long quarterChoose, @RequestParam(required = false) Long yearChoose){
        DashboardOrderResponse salesOrder = statisticService.calculateOrderSalesStatistics(type, monthChose, quarterChoose, yearChoose);
        Response response = new Response(HttpStatus.OK, "Show sales of order success.", salesOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/calculate-order-sales-from-and-to")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> calculateOrderSalesStatisticsByFromAndTo(@RequestBody FromAndToRequest request){
        DashboardOrderResponse salesOrder = statisticService.calculateOrderSalesStatisticsFromAndTo(request);
        Response response = new Response(HttpStatus.OK, "Show sales of order by from and to success.", salesOrder);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //Sales of Transactions request BUY
    @GetMapping("/calculate-transaction-buy-sales")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> calculateTransactionBuySales(@RequestParam String type, @RequestParam(required = false) Long monthChose,
                                                                 @RequestParam(required = false) Long quarterChoose, @RequestParam(required = false) Long yearChoose){
        DashboardTransactionResponse salesTransactionBuy = statisticService.calculateTransactionBuySales(type, monthChose, quarterChoose, yearChoose);
        Response response = new Response(HttpStatus.OK, "Show sales of transaction request buy success.", salesTransactionBuy);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/calculate-transaction-buy-sales-from-and-to")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> calculateTransactionBuySalesByFromAndTo(@RequestBody FromAndToRequest request){
        DashboardTransactionResponse salesTransactionBuy = statisticService.calculateTransactionBuySalesByFromAndTo(request);
        Response response = new Response(HttpStatus.OK, "Show sales of transaction buy by from and to success.", salesTransactionBuy);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Sales of Transactions request SELL
    @GetMapping("/calculate-transaction-sell-sales")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> calculateTransactionSellSales(@RequestParam String type, @RequestParam(required = false) Long monthChose,
                                                                  @RequestParam(required = false) Long quarterChoose, @RequestParam(required = false) Long yearChoose){
        DashboardTransactionResponse salesTransactionSell = statisticService.calculateTransactionSellSales(type, monthChose, quarterChoose, yearChoose);
        Response response = new Response(HttpStatus.OK, "Show sales of transaction request sell success.", salesTransactionSell);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/calculate-transaction-sell-sales-from-and-to")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> calculateTransactionSellSalesByFromAndTo(@RequestBody FromAndToRequest request){
        DashboardTransactionResponse salesTransactionSell = statisticService.calculateTransactionSellSalesByFromAndTo(request);
        Response response = new Response(HttpStatus.OK, "Show sales of transaction sell by from and to success.", salesTransactionSell);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Quantity of user, post, product and review star of product
    @GetMapping("/quantity-statistic")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> quantityStatistics(){
        QuantityStatisticResponse quantityStatistic = statisticService.quantityStatistics();
        Response response = new Response(HttpStatus.OK, "Show the quantity object success.", quantityStatistic);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/calculate-withdraw-request")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> calculateWithdrawRequestStatistics(@RequestParam String type, @RequestParam(required = false) Long monthChose,
                                                                       @RequestParam(required = false) Long quarterChoose, @RequestParam(required = false) Long yearChoose){
        DashboardWithdrawResponse requestWithdraw = statisticService.calculateWithdrawGoldStatistics(type, monthChose, quarterChoose, yearChoose);
        Response response = new Response(HttpStatus.OK, "Show requests of withdraw gold success.", requestWithdraw);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/calculate-withdraw-request-from-and-to")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_STAFF')")
    public ResponseEntity<Response> calculateWithdrawRequestStatisticsByFromAndTo(@RequestBody FromAndToRequest request){
        DashboardWithdrawResponse requestWithdraw = statisticService.calculateWithdrawGoldStatisticsFromAndTo(request);
        Response response = new Response(HttpStatus.OK, "Show requests of withdraw gold by from and to success.", requestWithdraw);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/analyze-data-set")
    public DataSetAnalyze analyzeDataSet() {
        DataSetAnalyze dataSet = new DataSetAnalyze();

        Map<LocalDate, BigDecimal> balances = convertBalancesToMap(analyzeBalanceRepository.findAll());
        Map<LocalDate, BigDecimal> orders = convertOrdersToMap(analyzeOrderRepository.findAll());
        Map<LocalDate, BigDecimal> transactions = convertTransactionsToMap(analyzeTransactionsRepository.findAll());

        dataSet.setAnalyzeBalance(new AnalyzeDataDTO(balances.isEmpty() ? Collections.emptyMap() : balances));
        dataSet.setAnalyzeOrder(new AnalyzeDataDTO(orders.isEmpty() ? Collections.emptyMap() : orders));
        dataSet.setAnalyzeTransactions(new AnalyzeDataDTO(transactions.isEmpty() ? Collections.emptyMap() : transactions));

        return dataSet;

    }

    private Map<LocalDate, BigDecimal> convertBalancesToMap(List<AnalyzeBalance> balances) {
        return balances.stream()
                .sorted(Comparator.comparing(AnalyzeBalance::getDate_time))
                .collect(Collectors.toMap(
                        balance -> balance.getDate_time().atZone(ZoneId.systemDefault()).toLocalDate(),
                        AnalyzeBalance::getProfit,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
    }

    private Map<LocalDate, BigDecimal> convertOrdersToMap(List<AnalyzeOrder> orders) {
        return orders.stream()
                .sorted(Comparator.comparing(AnalyzeOrder::getDate_time))
                .collect(Collectors.toMap(
                        order -> order.getDate_time().atZone(ZoneId.systemDefault()).toLocalDate(),
                        AnalyzeOrder::getProfit,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
    }

    private Map<LocalDate, BigDecimal> convertTransactionsToMap(List<AnalyzeTransactions> transactions) {
        return transactions.stream()
                .sorted(Comparator.comparing(AnalyzeTransactions::getDate_time))
                .collect(Collectors.toMap(
                        transaction -> transaction.getDate_time().atZone(ZoneId.systemDefault()).toLocalDate(),
                        AnalyzeTransactions::getProfit,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
    }


}
