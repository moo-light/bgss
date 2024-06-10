package com.server.blockchainserver.platform.server;

import com.server.blockchainserver.exeptions.BadRequestException;
import com.server.blockchainserver.models.enums.ELiveRateType;
import com.server.blockchainserver.platform.entity.LiveRates;
import com.server.blockchainserver.platform.payload.response.LiveRatesResponse;
import com.server.blockchainserver.platform.repositories.LiveRatesRepository;
import com.server.blockchainserver.utils.ObjectHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Component
public class MetalPriceApiServer {

    // private final String apiKey = "bf8b249f21b791b4c400f1f3459679aa";
    private final String apiKey = "";
    // LIVE RATES
    private final String liveRatesUrl = "https://api.metalpriceapi.com/v1/latest";

    // Timeframe Query
    private final String timeFrameQuery = "https://api.metalpriceapi.com/v1/timeframe";

    // Another Url

    @Autowired
    private RestTemplate restTemplate;

    public LiveRatesResponse fetchLiveGoldPrice() {
        String base = "USD";
        String requestUrl = liveRatesUrl + "?api_key=" + apiKey + "&base=" + base + "&currencies=XAU";
        LiveRatesResponse response = restTemplate.getForObject(requestUrl, LiveRatesResponse.class);

        if (response != null && response.getRates() != null) {
            double rateXAUtoBase = response.getRates().get("XAU");
            double convertedRate = convertXAUToBase(rateXAUtoBase);
            // Cập nhật lại map rates với giá trị đã chuyển đổi
            response.getRates().put("XAU", convertedRate);
        }
        return response;
    }

    public Map<String, Double> fetchTimeframeGoldPrice() {
        // String base = "USD";
        // DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // LocalDateTime now = LocalDateTime.now().minusDays(1);
        // String endDate = dtf.format(now);
        // String startDate = dtf.format(now.minusDays(365));

        // String requestUrl = timeFrameQuery + "?api_key=" + apiKey + "&base=" + base +
        // "&currencies=XAU&start_date=" + startDate + "&end_date=" + endDate;

        // TimeframeRatesResponse response = restTemplate.getForObject(requestUrl,
        // TimeframeRatesResponse.class);

        // Map<String, Double> goldPriceByDate = new LinkedHashMap<>();

        // if (response != null && response.isSuccess() && response.getRates() != null)
        // {
        // response.getRates().forEach((date, currencies) -> {
        // Double goldPrice = currencies.get("XAU");
        // if (goldPrice != null) {
        // goldPriceByDate.put(date, convertXAUToBase(goldPrice));
        // }
        // });
        // }
        // Mock response data
        Map<String, Double> goldPriceByDate = ObjectHelper.getMockGoldPriceData();

        return goldPriceByDate;
    }

    private double convertXAUToBase(double rateXAUtoBase) {
        // Áp dụng DecimalFormat để định dạng số
        DecimalFormat df = new DecimalFormat("#.00");
        double invertedRate = 1 / rateXAUtoBase;
        String formattedRate = df.format(invertedRate);
        return Double.parseDouble(formattedRate);
    }

    public String fetchHistoricalGoldPrice(String startDate, String endDate, String base) {
        String requestUrl = timeFrameQuery +
                "?api_key=" + apiKey +
                "&start_date=" + startDate +
                "&end_date=" + endDate +
                "&base=" + base +
                "&currencies=XAU";
        return restTemplate.getForObject(requestUrl, String.class);
    }

    @Autowired
    private LiveRatesRepository liveRatesRepository;

    public List<LiveRates> getLiveRates(ELiveRateType type) {
        switch (type) {
            case D, W, M:
                throw new BadRequestException("getLiveRates is only allow type smaller than 1 days");
            default:
                return liveRatesRepository.findFirst1000rateWithInterval(type.getInterval(),
                        type.getIntervalTime());
        }
        // return liveRatesRepository.testQuery("100 seconds");

    }

}
