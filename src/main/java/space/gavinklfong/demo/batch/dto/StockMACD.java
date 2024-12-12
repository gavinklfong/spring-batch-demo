package space.gavinklfong.demo.batch.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Value
public class StockMACD {
    String ticker;
    LocalDate date;
    BigDecimal value;
    BigDecimal sma9;
    BigDecimal ema9;
}
