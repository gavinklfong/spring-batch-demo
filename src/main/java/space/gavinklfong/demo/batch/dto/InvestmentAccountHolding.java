package space.gavinklfong.demo.batch.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestmentAccountHolding(String accountNumber,
                                       LocalDate date,
                                       BigDecimal aapl,
                                       BigDecimal sbux,
                                       BigDecimal msft,
                                       BigDecimal csco,
                                       BigDecimal qcom,
                                       BigDecimal meta,
                                       BigDecimal amzn,
                                       BigDecimal tsla,
                                       BigDecimal amd,
                                       BigDecimal nflx) {

}
