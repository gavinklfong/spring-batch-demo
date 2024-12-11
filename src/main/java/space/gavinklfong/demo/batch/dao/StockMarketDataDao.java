package space.gavinklfong.demo.batch.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import space.gavinklfong.demo.batch.dto.StockMarketData;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Component
public class StockMarketDataDao {

    private final JdbcClient jdbcClient;
    private final StockMarketDataRowMapper stockMarketDataRowMapper;

    private static final String SELECT_BY_TICKER_AND_OLDER_THAN_DATE_WITH_LIMIT = "SELECT " +
            "ticker, date, open, close, high, low, volume " +
            "FROM stock_price_history " +
            "WHERE ticker = :ticker " +
            "AND date <= :date " +
            "ORDER BY date DESC " +
            "LIMIT :limit";

//    private static final String SELECT_BY_TICKER_AND_OLDER_THAN_DATE_WITH_LIMIT = "SELECT " +
//            "ticker, date, open, close, high, low, volume " +
//            "FROM stock_price_history " +
//            "WHERE ticker = 'APPL' " +
//            "AND date <= '2024-10-15' " +
//            "ORDER BY date DESC " +
//            "LIMIT 5";

    public List<StockMarketData> findByTickerAndOlderOrEqualToDateWithLimit(String ticker, LocalDate date,
                                                                             int limit) {

        return jdbcClient.sql(SELECT_BY_TICKER_AND_OLDER_THAN_DATE_WITH_LIMIT)
                .param("ticker", ticker)
                .param("date", date)
                .param("limit", limit)
                .query(stockMarketDataRowMapper)
                .list();
    }
}
