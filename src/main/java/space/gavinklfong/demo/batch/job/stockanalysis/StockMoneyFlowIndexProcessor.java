package space.gavinklfong.demo.batch.job.stockanalysis;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.batch.item.ItemProcessor;
import space.gavinklfong.demo.batch.dao.StockMarketDataDao;
import space.gavinklfong.demo.batch.dto.StockMarketData;
import space.gavinklfong.demo.batch.dto.StockPeriodIntervalValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.compare.ComparableUtils.is;

@RequiredArgsConstructor
public class StockMoneyFlowIndexProcessor implements ItemProcessor<StockMarketData, StockPeriodIntervalValue> {

    private final StockMarketDataDao stockMarketDataDao;
    private final LocalDate date;

    @Override
    public StockPeriodIntervalValue process(@NonNull StockMarketData stockMarketData) {

        List<StockMarketData> stockMarketDataList = new ArrayList<>();
        stockMarketDataList.add(stockMarketData);
        stockMarketDataList.addAll(retrieveStockMarketData(stockMarketData.getTicker(), date));

        StockPeriodIntervalValue result = StockPeriodIntervalValue.builder()
                .ticker(stockMarketData.getTicker())
                .date(stockMarketData.getDate())
                .value10(calculateMFI(stockMarketDataList, 10))
                .value12(calculateMFI(stockMarketDataList, 12))
                .value20(calculateMFI(stockMarketDataList, 20))
                .value26(calculateMFI(stockMarketDataList, 26))
                .value50(calculateMFI(stockMarketDataList, 50))
                .value100(calculateMFI(stockMarketDataList, 100))
                .value200(calculateMFI(stockMarketDataList, 200))
                .build();

        return result;
    }

    private List<StockMarketData> retrieveStockMarketData(String ticker, LocalDate date) {
        return stockMarketDataDao.findByTickerAndOlderOrEqualToDateWithLimit(ticker, date, 200);
    }

    private BigDecimal calculateMFI(List<StockMarketData> stockMarketDataList, int averageDayInterval) {
        if (stockMarketDataList.size() < averageDayInterval) {
            return null;
        }

        List<MoneyFlow> moneyFlowList = stockMarketDataList.stream()
                .limit(averageDayInterval)
                .map(item -> MoneyFlow.builder()
                        .date(date)
                        .typicalPrice(calculateTypicalPrice(item))
                        .value(calculateMoneyFlow(item))
                        .volume(item.getVolume().longValue())
                        .build()
                )
                .toList();

        BigDecimal positiveMoneyFlow = BigDecimal.ZERO;
        BigDecimal negativeMoneyFlow = BigDecimal.ZERO;
        for (int i = moneyFlowList.size() - 1; i > 0; i--) {
            BigDecimal currentValue = moneyFlowList.get(i).getTypicalPrice();
            BigDecimal previousValue = moneyFlowList.get(i-1).getTypicalPrice();
            if (is(currentValue).greaterThan(previousValue)) {
                positiveMoneyFlow = positiveMoneyFlow.add(currentValue);
            } else if (is(currentValue).lessThan(previousValue)) {
                negativeMoneyFlow = negativeMoneyFlow.add(currentValue);
            }
        }

        return new BigDecimal("100")
                .multiply(positiveMoneyFlow)
                .divide(positiveMoneyFlow.add(negativeMoneyFlow), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateMoneyFlow(StockMarketData item) {
        return calculateTypicalPrice(item).multiply(BigDecimal.valueOf(item.getVolume().longValue()));
    }

    private static BigDecimal calculateTypicalPrice(StockMarketData item) {
        BigDecimal sum = item.getClose().add(item.getHigh()).add(item.getLow());
        return sum.divide(new BigDecimal(3), 3, RoundingMode.HALF_UP);
    }

    @Builder
    @Value
    static class MoneyFlow {
        LocalDate date;
        BigDecimal typicalPrice;
        long volume;
        BigDecimal value;
    }

}
