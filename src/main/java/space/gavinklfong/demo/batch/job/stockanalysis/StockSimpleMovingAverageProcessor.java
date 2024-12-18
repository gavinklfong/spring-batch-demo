package space.gavinklfong.demo.batch.job.stockanalysis;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import space.gavinklfong.demo.batch.dao.StockMarketDataDao;
import space.gavinklfong.demo.batch.dto.StockMarketData;
import space.gavinklfong.demo.batch.dto.StockPeriodIntervalValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class StockSimpleMovingAverageProcessor implements ItemProcessor<StockMarketData, StockPeriodIntervalValue> {

    private final StockMarketDataDao stockMarketDataDao;

    @Override
    public StockPeriodIntervalValue process(@NonNull StockMarketData stockMarketData) {

        List<StockMarketData> stockMarketDataList = new ArrayList<>();
        stockMarketDataList.add(stockMarketData);
        stockMarketDataList.addAll(retrieveStockMarketData(stockMarketData.getTicker(), stockMarketData.getDate()));

        return StockPeriodIntervalValue.builder()
                .ticker(stockMarketData.getTicker())
                .date(stockMarketData.getDate())
                .value10(calculateMovingAverage(stockMarketDataList, 10))
                .value12(calculateMovingAverage(stockMarketDataList, 12))
                .value20(calculateMovingAverage(stockMarketDataList, 20))
                .value26(calculateMovingAverage(stockMarketDataList, 26))
                .value50(calculateMovingAverage(stockMarketDataList, 50))
                .value100(calculateMovingAverage(stockMarketDataList, 100))
                .value200(calculateMovingAverage(stockMarketDataList, 200))
                .build();
    }

    private List<StockMarketData> retrieveStockMarketData(String ticker, LocalDate date) {
        return stockMarketDataDao.findByTickerAndOlderOrEqualToDateWithLimit(ticker, date, 200);
    }

    private BigDecimal calculateMovingAverage(List<StockMarketData> stockMarketDataList, int averageDayInterval) {
        if (stockMarketDataList.size() < averageDayInterval) {
            return null;
        }

        BigDecimal sum = stockMarketDataList.stream()
                .limit(averageDayInterval)
                .map(StockMarketData::getClose)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(averageDayInterval), 2, RoundingMode.HALF_UP);
    }

}
