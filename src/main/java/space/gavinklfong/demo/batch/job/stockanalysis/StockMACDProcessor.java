package space.gavinklfong.demo.batch.job.stockanalysis;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import space.gavinklfong.demo.batch.dao.StockExponentialMovingAverageDao;
import space.gavinklfong.demo.batch.dto.StockMACD;
import space.gavinklfong.demo.batch.dto.StockMarketData;
import space.gavinklfong.demo.batch.dto.StockPeriodIntervalValue;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class StockMACDProcessor implements ItemProcessor<StockMarketData, StockMACD> {

    private final StockExponentialMovingAverageDao stockExponentialMovingAverageDao;

    @Override
    public StockMACD process(@NonNull StockMarketData stockMarketData) {

        return stockExponentialMovingAverageDao
                .findByTickerAndDate(stockMarketData.getTicker(), stockMarketData.getDate())
                .map(ema -> buildStockMACD(stockMarketData, ema))
                .orElse(null);
    }

    private StockMACD buildStockMACD(StockMarketData stockMarketData, StockPeriodIntervalValue ema) {
        return StockMACD.builder()
                .ticker(stockMarketData.getTicker())
                .date(stockMarketData.getDate())
                .value(calculateMACD(ema))
                .build();
    }

    private BigDecimal calculateMACD(StockPeriodIntervalValue ema) {
        return ema.getValue12().subtract(ema.getValue26());
    }
}
