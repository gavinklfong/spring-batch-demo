package space.gavinklfong.demo.batch.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.util.Objects.isNull;

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

    public boolean isAnyValueEmpty() {
        return isNull(value10) || isNull(value12) || isNull(value20)
                || isNull(value26) || isNull(value50)
                || isNull(value100) || isNull(value200);
    }
}
