package space.gavinklfong.demo.batch.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import space.gavinklfong.demo.batch.dto.StockMarketData;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StockMarketDataRowMapper implements RowMapper<StockMarketData> {

    @Override
    public StockMarketData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return StockMarketData.builder()
                .low(rs.getBigDecimal("low"))
                .high(rs.getBigDecimal("high"))
                .date(rs.getDate("date").toLocalDate())
                .close(rs.getBigDecimal("close"))
                .open(rs.getBigDecimal("open"))
                .ticker(rs.getString("ticker"))
                .volume(BigInteger.valueOf(rs.getLong("volume")))
                .build();
    }
}
