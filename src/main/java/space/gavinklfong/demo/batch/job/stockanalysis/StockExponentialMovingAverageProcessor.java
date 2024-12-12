package space.gavinklfong.demo.batch.job.stockanalysis;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import space.gavinklfong.demo.batch.dao.StockExponentialMovingAverageDao;
import space.gavinklfong.demo.batch.dao.StockSimpleMovingAverageDao;
import space.gavinklfong.demo.batch.dto.StockMarketData;
import space.gavinklfong.demo.batch.dto.StockPeriodIntervalValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
public class StockExponentialMovingAverageProcessor implements ItemProcessor<StockMarketData, StockPeriodIntervalValue> {

    private static final int SMOOTHING = 2;

    private final StockExponentialMovingAverageDao stockExponentialMovingAverageDao;
    private final StockSimpleMovingAverageDao stockSimpleMovingAverageDao;
    private final LocalDate date;

    @Override
    public StockPeriodIntervalValue process(@NonNull StockMarketData stockMarketData) {

        // Get previous average
        Optional<StockPeriodIntervalValue> previousMovingAverage = retrievePreviousMovingAverage(stockMarketData.getTicker(), date);
        if (previousMovingAverage.isEmpty()) {
            log.warn("No previous average is found for ticker={}, date={}", stockMarketData.getTicker(), date);
            return null;
        }

        return buildExponentialMovingAverage(stockMarketData, previousMovingAverage.get());
    }

    private StockPeriodIntervalValue buildExponentialMovingAverage(StockMarketData stockMarketData,
                                                                   StockPeriodIntervalValue previousMovingAverage) {
        return StockPeriodIntervalValue.builder()
                .ticker(stockMarketData.getTicker())
                .date(stockMarketData.getDate())
                .value10(calculateMovingAverage(stockMarketData, previousMovingAverage.getValue10(), 10))
                .value12(calculateMovingAverage(stockMarketData, previousMovingAverage.getValue12(), 12))
                .value20(calculateMovingAverage(stockMarketData, previousMovingAverage.getValue20(), 20))
                .value26(calculateMovingAverage(stockMarketData, previousMovingAverage.getValue26(), 26))
                .value50(calculateMovingAverage(stockMarketData, previousMovingAverage.getValue50(), 50))
                .value100(calculateMovingAverage(stockMarketData, previousMovingAverage.getValue100(), 100))
                .value200(calculateMovingAverage(stockMarketData, previousMovingAverage.getValue200(), 200))
                .build();
    }

    private Optional<StockPeriodIntervalValue> retrievePreviousMovingAverage(String ticker, LocalDate date) {
        Optional<StockPeriodIntervalValue> exponentialMovingAverage = retrievePreviousExponentialMovingAverage(ticker, date);
        if (exponentialMovingAverage.isEmpty() || exponentialMovingAverage.get().isAnyValueEmpty()) {
            log.info("previous EMA does not exist, use the previous SMA instead for ticker={}, date={}", ticker, date);
            return retrievePreviousSimpleMovingAverage(ticker, date);
        }
        return exponentialMovingAverage;
    }

    private Optional<StockPeriodIntervalValue> retrievePreviousSimpleMovingAverage(String ticker, LocalDate date) {
        List<StockPeriodIntervalValue> averageList = stockSimpleMovingAverageDao.findByTickerAndOlderThanDateWithLimit(ticker, date, 1);
        if (averageList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(averageList.getFirst());
        }
    }

    private Optional<StockPeriodIntervalValue> retrievePreviousExponentialMovingAverage(String ticker, LocalDate date) {
        List<StockPeriodIntervalValue> averageList = stockExponentialMovingAverageDao.findByTickerAndOlderThenDateWithLimit(ticker, date, 1);
        if (averageList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(averageList.getFirst());
        }
    }

    private BigDecimal calculateMovingAverage(StockMarketData stockMarketData, BigDecimal previousAverage, int averageDayInternal) {
        if (isNull(previousAverage)) {
            log.warn("previousAverage is null, ticker={}, date={}", stockMarketData.getTicker(), date);
            return null;
        }

        BigDecimal factor = new BigDecimal(SMOOTHING).divide(new BigDecimal(1 + averageDayInternal), 4, RoundingMode.HALF_UP);
        return stockMarketData.getClose()
                .multiply(factor)
                .add(previousAverage
                        .multiply((BigDecimal.ONE.subtract(factor))));
    }

}
