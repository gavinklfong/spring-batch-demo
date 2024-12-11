package space.gavinklfong.demo.batch.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Value
public class StockSimpleMovingAverage {
    String ticker;
    LocalDate date;
    BigDecimal value10;
    BigDecimal value20;
    BigDecimal value50;
    BigDecimal value100;
    BigDecimal value200;
}
