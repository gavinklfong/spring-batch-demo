package space.gavinklfong.demo.batch.job.stockanalysis;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import space.gavinklfong.demo.batch.dao.StockExponentialMovingAverageDao;
import space.gavinklfong.demo.batch.dto.StockMACD;
import space.gavinklfong.demo.batch.dto.StockMarketData;
import space.gavinklfong.demo.batch.dto.StockPeriodIntervalValue;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class StockMACDProcessor implements ItemProcessor<StockMarketData, StockMACD> {

    private final StockExponentialMovingAverageDao stockExponentialMovingAverageDao;
    private final LocalDate date;

    @Override
    public StockMACD process(@NonNull StockMarketData stockMarketData) {

        StockPeriodIntervalValue ema = stockExponentialMovingAverageDao
                .findByTickerAndDate(stockMarketData.getTicker(), date)
                .orElseThrow();

        return StockMACD.builder()
                .ticker(stockMarketData.getTicker())
                .date(date)
                .value(calculateMACD(ema))
                .build();
    }

    private BigDecimal calculateMACD(StockPeriodIntervalValue ema) {
        return ema.getValue12().subtract(ema.getValue26());
    }
}
