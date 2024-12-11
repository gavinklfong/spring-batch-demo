package space.gavinklfong.demo.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import space.gavinklfong.demo.batch.dto.StockMarketData;
import space.gavinklfong.demo.batch.dto.StockSimpleMovingAverage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StockSimpleMovingAverageProcessor implements ItemProcessor<StockMarketData, StockSimpleMovingAverage> {

    @Value("#{jobParameters['date']}")
    private LocalDate date;

    @Override
    public StockSimpleMovingAverage process(StockMarketData stockMarketData) {

        List<StockMarketData> stockMarketDataList = new ArrayList<>();
        stockMarketDataList.add(stockMarketData);

        // get last 50 - 1 stock price by the ticker (assume no stock price missing)
        // stockMarketDataList.add(previousList);

        // calculate moving average 10, 20 and 50
        return StockSimpleMovingAverage.builder()
                .ticker(stockMarketData.getTicker())
                .date(stockMarketData.getDate())
                .value10(calculateMovingAverage(stockMarketDataList, 10))
                .value20(calculateMovingAverage(stockMarketDataList, 20))
                .value50(calculateMovingAverage(stockMarketDataList, 50))
                .build();
    }

    private BigDecimal calculateMovingAverage(List<StockMarketData> stockMarketDataList, int averageDayInterval) {
        BigDecimal sum = stockMarketDataList.stream()
                .limit(averageDayInterval)
                .map(StockMarketData::getClose)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(averageDayInterval), 2, RoundingMode.HALF_UP);
    }

}
