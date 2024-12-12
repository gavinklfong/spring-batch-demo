package space.gavinklfong.demo.batch.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import space.gavinklfong.demo.batch.dto.StockMACD;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StockMACDRowMapper implements RowMapper<StockMACD> {

    @Override
    public StockMACD mapRow(ResultSet rs, int rowNum) throws SQLException {
        return StockMACD.builder()
                .date(rs.getDate("date").toLocalDate())
                .ticker(rs.getString("ticker"))
                .value(rs.getBigDecimal("value"))
                .sma9(rs.getBigDecimal("sma_9"))
                .ema9(rs.getBigDecimal("ema_9"))
                .build();
    }
}
