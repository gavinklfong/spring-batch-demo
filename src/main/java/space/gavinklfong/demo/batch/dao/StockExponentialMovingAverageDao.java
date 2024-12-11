package space.gavinklfong.demo.batch.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import space.gavinklfong.demo.batch.dto.StockMovingAverage;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StockExponentialMovingAverageDao {

    private final JdbcClient jdbcClient;
    private final StockMovingAverageRowMapper stockMovingAverageRowMapper;

    private static final String SELECT_BY_TICKER_AND_OLDER_THAN_DATE_WITH_LIMIT = "SELECT " +
            "ticker, date, value_10, value_20, value_50, value_100, value_200 " +
            "FROM stock_price_ema " +
            "WHERE ticker = :ticker " +
            "AND date < :date " +
            "ORDER BY date DESC " +
            "LIMIT :limit";

    public List<StockMovingAverage> findByTickerAndOlderOrEqualToDateWithLimit(String ticker, LocalDate date,
                                                                               int limit) {

        return jdbcClient.sql(SELECT_BY_TICKER_AND_OLDER_THAN_DATE_WITH_LIMIT)
                .param("ticker", ticker)
                .param("date", date)
                .param("limit", limit)
                .query(stockMovingAverageRowMapper)
                .list();
    }
}
