package space.gavinklfong.demo.batch.job;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import space.gavinklfong.demo.batch.dto.StockMarketData;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class StockMarketDataFieldSetMapper implements FieldSetMapper<StockMarketData> {
    @Override
    public StockMarketData mapFieldSet(FieldSet fieldSet) throws BindException {
        return StockMarketData.builder()
                .low(fieldSet.readBigDecimal("low"))
                .close(fieldSet.readBigDecimal("close"))
                .high(fieldSet.readBigDecimal("high"))
                .open(fieldSet.readBigDecimal("open"))
                .date(LocalDate.parse(fieldSet.readString("date"), DateTimeFormatter.ISO_DATE))
                .ticker(fieldSet.readString("ticker"))
                .volume(new BigInteger(fieldSet.readString("volume")))
                .build();
    }
}
