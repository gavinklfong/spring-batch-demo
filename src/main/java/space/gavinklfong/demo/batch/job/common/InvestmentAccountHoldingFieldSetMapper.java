package space.gavinklfong.demo.batch.job.common;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;
import space.gavinklfong.demo.batch.dto.InvestmentAccountHolding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InvestmentAccountHoldingFieldSetMapper implements FieldSetMapper<InvestmentAccountHolding> {

    @Override
    public InvestmentAccountHolding mapFieldSet(FieldSet fs) throws BindException {
        return new InvestmentAccountHolding(
                fs.readString("accountNumber"),
                LocalDate.parse(fs.readString("date"), DateTimeFormatter.ISO_DATE),
                fs.readBigDecimal("AAPL"),
                fs.readBigDecimal("SBUX"),
                fs.readBigDecimal("MSFT"),
                fs.readBigDecimal("CSCO"),
                fs.readBigDecimal("QCOM"),
                fs.readBigDecimal("META"),
                fs.readBigDecimal("AMZN"),
                fs.readBigDecimal("TSLA"),
                fs.readBigDecimal("AMD"),
                fs.readBigDecimal("NFLX")
        );

    }
}
