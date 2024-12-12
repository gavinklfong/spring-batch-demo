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
import space.gavinklfong.demo.batch.dao.StockMarketDataDao;
import space.gavinklfong.demo.batch.dto.StockMarketData;
import space.gavinklfong.demo.batch.dto.StockPeriodIntervalValue;

import javax.sql.DataSource;
import java.time.LocalDate;

@Configuration
public class CalculateStockMoneyFlowIndexJobConfig {

    @Bean
    public Step calculateMoneyFlowIndexStep(JobRepository jobRepository,
                                                 DataSourceTransactionManager transactionManager,
                                                 JdbcCursorItemReader<StockMarketData> stockMarketDataDatabaseReader,
                                                 StockSimpleMovingAverageProcessor stockMoneyFlowIndexProcessor,
                                                 JdbcBatchItemWriter<StockPeriodIntervalValue> stockMoneyFlowIndexWriter) {
        return new StepBuilder("calculateMoneyFlowIndexStep", jobRepository)
                .<StockMarketData, StockPeriodIntervalValue> chunk(1000, transactionManager)
                .reader(stockMarketDataDatabaseReader) // get list of stock by date from job parameter
                .processor(stockMoneyFlowIndexProcessor)
                .writer(stockMoneyFlowIndexWriter)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public StockMoneyFlowIndexProcessor stockMoneyFlowIndexProcessor(
            @Value("#{jobParameters['date']}") LocalDate date, StockMarketDataDao stockMarketDataDao) {
        return new StockMoneyFlowIndexProcessor(stockMarketDataDao, date);
    }

    @Bean
    public JdbcBatchItemWriter<StockPeriodIntervalValue> stockMoneyFlowIndexWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<StockPeriodIntervalValue>()
                .sql("INSERT INTO stock_price_mfi (ticker, date, value_10, value_12, value_20, value_26, value_50, value_100, value_200) " +
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
