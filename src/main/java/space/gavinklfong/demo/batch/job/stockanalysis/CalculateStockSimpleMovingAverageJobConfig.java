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
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import space.gavinklfong.demo.batch.dao.StockMarketDataDao;
import space.gavinklfong.demo.batch.dao.StockMarketDataRowMapper;
import space.gavinklfong.demo.batch.dto.StockMarketData;
import space.gavinklfong.demo.batch.dto.StockSimpleMovingAverage;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
public class CalculateStockSimpleMovingAverageJobConfig {

    @Bean
    public Job calculateSimpleMovingAverageJob(JobRepository jobRepository, Step calculateSimpleMovingAverageStep) {
        return new JobBuilder("calculateSimpleMovingAverageJob", jobRepository)
                .start(calculateSimpleMovingAverageStep)
                .build();
    }

    @Bean
    public Step calculateSimpleMovingAverageStep(JobRepository jobRepository,
                                                 DataSourceTransactionManager transactionManager,
                                                 JdbcCursorItemReader<StockMarketData> stockMarketDataDatabaseReader,
                                                 StockSimpleMovingAverageProcessor stockSimpleMovingAverageProcessor,
                                                 JdbcBatchItemWriter<StockSimpleMovingAverage> stockMovingAverageWriter) {
        return new StepBuilder("calculateSimpleMovingAverageStep", jobRepository)
                .<StockMarketData, StockSimpleMovingAverage> chunk(1000, transactionManager)
                .reader(stockMarketDataDatabaseReader) // get list of stock by date from job parameter
                .processor(stockSimpleMovingAverageProcessor)
                .writer(stockMovingAverageWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @StepScope
    @Bean
    public JdbcCursorItemReader<StockMarketData> stockMarketDataDatabaseReader(
            StockMarketDataRowMapper stockMarketDataRowMapper,
            DataSource dataSource,
            @Value("#{jobParameters['date']}") LocalDate date) {
        return new JdbcCursorItemReaderBuilder<StockMarketData>()
                .name("stockMarketDataDatabaseReader")
                .dataSource(dataSource)
                .sql("SELECT " +
                        "ticker, date, open, close, high, low, volume " +
                        "FROM stock_price_history " +
                        "WHERE date = ? ")
                .queryArguments(date)
                .rowMapper(stockMarketDataRowMapper)
                .build();
    }

    @Bean
    @StepScope
    public StockSimpleMovingAverageProcessor stockSimpleMovingAverageProcessor(
            @Value("#{jobParameters['date']}") LocalDate date, StockMarketDataDao stockMarketDataDao) {
        return new StockSimpleMovingAverageProcessor(stockMarketDataDao, date);
    }

    @Bean
    public JdbcBatchItemWriter<StockSimpleMovingAverage> stockMovingAverageWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<StockSimpleMovingAverage>()
                .sql("INSERT INTO stock_price_sma (ticker, date, value_10, value_20, value_50, value_100, value_200) " +
                        "VALUES (:ticker, :date, :value10, :value20, :value50, :value100, :value200)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }
}
