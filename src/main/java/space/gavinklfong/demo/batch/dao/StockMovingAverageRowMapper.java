package space.gavinklfong.demo.batch.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import space.gavinklfong.demo.batch.dto.StockPeriodIntervalValue;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StockMovingAverageRowMapper implements RowMapper<StockPeriodIntervalValue> {

    @Override
    public StockPeriodIntervalValue mapRow(ResultSet rs, int rowNum) throws SQLException {
        return StockPeriodIntervalValue.builder()
                .date(rs.getDate("date").toLocalDate())
                .ticker(rs.getString("ticker"))
                .value10(rs.getBigDecimal("value_10"))
                .value12(rs.getBigDecimal("value_12"))
                .value20(rs.getBigDecimal("value_20"))
                .value26(rs.getBigDecimal("value_26"))
                .value50(rs.getBigDecimal("value_50"))
                .value100(rs.getBigDecimal("value_100"))
                .value200(rs.getBigDecimal("value_200"))
                .build();
    }
}
