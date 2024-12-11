package space.gavinklfong.demo.batch.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import space.gavinklfong.demo.batch.dto.StockMovingAverage;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StockMovingAverageRowMapper implements RowMapper<StockMovingAverage> {

    @Override
    public StockMovingAverage mapRow(ResultSet rs, int rowNum) throws SQLException {
        return StockMovingAverage.builder()
                .date(rs.getDate("date").toLocalDate())
                .ticker(rs.getString("ticker"))
                .value10(rs.getBigDecimal("value_10"))
                .value20(rs.getBigDecimal("value_20"))
                .value50(rs.getBigDecimal("value_50"))
                .value100(rs.getBigDecimal("value_100"))
                .value200(rs.getBigDecimal("value_200"))
                .build();
    }
}
