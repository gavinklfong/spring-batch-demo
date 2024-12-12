package space.gavinklfong.demo.batch.job.stockanalysis;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
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
import space.gavinklfong.demo.batch.dao.StockSimpleMovingAverageDao;
import space.gavinklfong.demo.batch.dto.StockMarketData;
import space.gavinklfong.demo.batch.dto.StockPeriodIntervalValue;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
public class CalculateStockExponentialMovingAverageJobConfig {

    @Bean
    public Job calculateStockExponentialMovingAverageJob(JobRepository jobRepository,
                                                        Step calculateStockExponentialMovingAverageStep) {
        return new JobBuilder("calculateExponentialMovingAverageJob", jobRepository)
                .start(calculateStockExponentialMovingAverageStep)
                .build();
    }

    @Bean
    public Step calculateStockExponentialMovingAverageStep(JobRepository jobRepository,
                                                          DataSourceTransactionManager transactionManager,
                                                          JdbcCursorItemReader<StockMarketData> stockMarketDataDatabaseReader,
                                                          StockExponentialMovingAverageProcessor stockExponentialMovingAverageProcessor,
                                                          JdbcBatchItemWriter<StockPeriodIntervalValue> stockExponentialMovingAverageWriter) {
        return new StepBuilder("calculateStockExponentialMovingAverageStep", jobRepository)
                .<StockMarketData, StockPeriodIntervalValue> chunk(1000, transactionManager)
                .reader(stockMarketDataDatabaseReader) // get list of stock by date from job parameter
                .processor(stockExponentialMovingAverageProcessor)
                .writer(stockExponentialMovingAverageWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public StockExponentialMovingAverageProcessor stockExponentialMovingAverageProcessor(
            @Value("#{jobParameters['date']}") LocalDate date,
            StockExponentialMovingAverageDao stockExponentialMovingAverageDao,
            StockSimpleMovingAverageDao stockSimpleMovingAverageDao) {
        return new StockExponentialMovingAverageProcessor(stockExponentialMovingAverageDao, stockSimpleMovingAverageDao, date);
    }

    @Bean
    public JdbcBatchItemWriter<StockPeriodIntervalValue> stockExponentialMovingAverageWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<StockPeriodIntervalValue>()
                .sql("INSERT INTO stock_price_ema (ticker, date, value_10, value_12, value_20, value_26, value_50, value_100, value_200) " +
                        "VALUES (:ticker, :date, :value10, :value12, :value20, :value26, :value50, :value100, :value200) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "value_10 = :value10, " +
                        "value_12 = :value12, " +
                        "value_20 = :value20, " +
                        "value_26 = :value26, " +
                        "value_50 = :value50, " +
                        "value_100 = :value100, " +
                        "value_200 = :value200 "
                )
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }
}
