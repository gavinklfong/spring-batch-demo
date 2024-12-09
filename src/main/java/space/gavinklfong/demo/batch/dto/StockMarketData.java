package space.gavinklfong.demo.batch.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Builder
@Data
public class StockMarketData {
    private String ticker;
    private LocalDate date;
    private BigDecimal close;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigInteger volume;
}
