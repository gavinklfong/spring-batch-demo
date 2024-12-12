package space.gavinklfong.demo.batch.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import space.gavinklfong.demo.batch.dto.StockMACD;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StockMACDDao {

    private final JdbcClient jdbcClient;
    private final StockMACDRowMapper stockMACDRowMapper;

    private static final String SELECT_BY_TICKER_AND_OLDER_THAN_DATE_WITH_LIMIT = "SELECT " +
            "ticker, date, value, sma_9, ema_9 " +
            "FROM stock_price_macd " +
            "WHERE ticker = :ticker " +
            "AND date <= :date " +
            "ORDER BY date DESC " +
            "LIMIT :limit";

    public List<StockMACD> findByTickerAndOlderOrEqualToDateWithLimit(String ticker, LocalDate date,
                                                                      int limit) {

        return jdbcClient.sql(SELECT_BY_TICKER_AND_OLDER_THAN_DATE_WITH_LIMIT)
                .param("ticker", ticker)
                .param("date", date)
                .param("limit", limit)
                .query(stockMACDRowMapper)
                .list();
    }
}
