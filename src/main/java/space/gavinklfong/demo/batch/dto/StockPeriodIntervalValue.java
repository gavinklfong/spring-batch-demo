package space.gavinklfong.demo.batch.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Value
public class StockPeriodIntervalValue {
    String ticker;
    LocalDate date;
    BigDecimal value10;
    BigDecimal value12;
    BigDecimal value20;
    BigDecimal value26;
    BigDecimal value50;
    BigDecimal value100;
    BigDecimal value200;
}