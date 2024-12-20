package space.gavinklfong.demo.batch.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Builder
@Value
public class StockMarketData {
    String ticker;
    LocalDate date;
    BigDecimal close;
    BigDecimal open;
    BigDecimal high;
    BigDecimal low;
    BigInteger volume;
}
