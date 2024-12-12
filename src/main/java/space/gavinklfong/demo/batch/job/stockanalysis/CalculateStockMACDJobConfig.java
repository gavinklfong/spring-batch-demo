package space.gavinklfong.demo.batch.job.stockanalysis;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import space.gavinklfong.demo.batch.dao.StockExponentialMovingAverageDao;
import space.gavinklfong.demo.batch.dto.StockMACD;
import space.gavinklfong.demo.batch.dto.StockMarketData;
import space.gavinklfong.demo.batch.dto.StockPeriodIntervalValue;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
public class CalculateStockMACDJobConfig {

    @Bean
    public Step calculateMACDStep(JobRepository jobRepository,
                                                 DataSourceTransactionManager transactionManager,
                                                 JdbcCursorItemReader<StockMarketData> stockMarketDataDatabaseReader,
                                                 StockSimpleMovingAverageProcessor stockSimpleMovingAverageProcessor,
                                                 JdbcBatchItemWriter<StockPeriodIntervalValue> stockSimpleMovingAverageWriter) {
        return new StepBuilder("calculateSimpleMovingAverageStep", jobRepository)
                .<StockMarketData, StockPeriodIntervalValue> chunk(1000, transactionManager)
                .reader(stockMarketDataDatabaseReader) // get list of stock by date from job parameter
                .processor(stockSimpleMovingAverageProcessor)
                .writer(stockSimpleMovingAverageWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public StockMACDProcessor stockMACDProcessor(
            @Value("#{jobParameters['date']}") LocalDate date,
            StockExponentialMovingAverageDao stockExponentialMovingAverageDao) {
        return new StockMACDProcessor(stockExponentialMovingAverageDao, date);
    }

    @Bean
    public JdbcBatchItemWriter<StockMACD> stockMACDJdbcBatchItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<StockMACD>()
                .sql("INSERT INTO stock_price_macd (ticker, date, value, sma_9, ema_9) " +
                        "VALUES (:ticker, :date, :value, :sma9, :ema9) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "value = :value ")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }
}
