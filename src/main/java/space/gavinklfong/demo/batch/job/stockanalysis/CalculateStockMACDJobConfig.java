package space.gavinklfong.demo.batch.job.stockanalysis;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import space.gavinklfong.demo.batch.dao.StockExponentialMovingAverageDao;
import space.gavinklfong.demo.batch.dto.StockMACD;
import space.gavinklfong.demo.batch.dto.StockMarketData;

import javax.sql.DataSource;

@Configuration
public class CalculateStockMACDJobConfig {

    @Bean
    public Step calculateStockMACDStep(JobRepository jobRepository,
                                                 DataSourceTransactionManager transactionManager,
                                                 JdbcCursorItemReader<StockMarketData> stockMarketDataDatabaseReader,
                                                 StockMACDProcessor stockMACDProcessor,
                                                 JdbcBatchItemWriter<StockMACD> stockMACDJdbcBatchItemWriter) {
        return new StepBuilder("calculateStockMACDStep", jobRepository)
                .<StockMarketData, StockMACD> chunk(1000, transactionManager)
                .reader(stockMarketDataDatabaseReader) // get list of stock by date from job parameter
                .processor(stockMACDProcessor)
                .writer(stockMACDJdbcBatchItemWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public StockMACDProcessor stockMACDProcessor(
            StockExponentialMovingAverageDao stockExponentialMovingAverageDao) {
        return new StockMACDProcessor(stockExponentialMovingAverageDao);
    }

    @Bean
    public JdbcBatchItemWriter<StockMACD> stockMACDJdbcBatchItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<StockMACD>()
                .sql("INSERT INTO stock_price_macd (ticker, date, value) " +
                        "VALUES (:ticker, :date, :value) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "value = :value ")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }
}
