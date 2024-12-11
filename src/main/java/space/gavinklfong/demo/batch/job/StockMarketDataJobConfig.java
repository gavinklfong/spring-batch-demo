package space.gavinklfong.demo.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import space.gavinklfong.demo.batch.dto.StockMarketData;
import space.gavinklfong.demo.batch.dto.StockSimpleMovingAverage;

import javax.sql.DataSource;

@Configuration
public class StockMarketDataJobConfig {

    @Bean
    public Job importStockMarketDataJob(JobRepository jobRepository,Step importStockMarketDataStep) {
        return new JobBuilder("importStockMarketDataJob", jobRepository)
                .start(importStockMarketDataStep)
                .build();
    }

    @Bean
    public Step importStockMarketDataStep(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                                          FlatFileItemReader<StockMarketData> stockMarketDataReader,
                                          JdbcBatchItemWriter<StockMarketData> stockMarketDataWriter) {
        return new StepBuilder("importStockMarketDataStep", jobRepository)
                .<StockMarketData, StockMarketData> chunk(1000, transactionManager)
                .reader(stockMarketDataReader)
                .writer(stockMarketDataWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<StockMarketData> stockMarketDataReader(
            FieldSetMapper<StockMarketData> stockMarketDataRowMapper) {
        return new FlatFileItemReaderBuilder<StockMarketData>()
                .name("stockMarketDataReader")
                .resource(new ClassPathResource("data/stock_market_data.csv"))
                .delimited()
                .names("ticker", "date", "close", "volume", "open", "high", "low")
                .fieldSetMapper(stockMarketDataRowMapper)
                .linesToSkip(1)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<StockMarketData> stockMarketDataWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<StockMarketData>()
                .sql("INSERT INTO stock_price_history (ticker, date, open, close, high, low, volume) " +
                        "VALUES (:ticker, :date, :open, :close, :high, :low, :volume)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }


    @Bean
    public Step calculateSimpleMovingAverageStep(JobRepository jobRepository,
                                                 DataSourceTransactionManager transactionManager,
                                                 FlatFileItemReader<StockMarketData> stockMarketDataReader,
                                                 StockSimpleMovingAverageProcessor stockSimpleMovingAverageProcessor,
                                                 JdbcBatchItemWriter<StockSimpleMovingAverage> stockMovingAverageWriter) {
        return new StepBuilder("calculateSimpleMovingAverageStep", jobRepository)
                .<StockMarketData, StockSimpleMovingAverage> chunk(1000, transactionManager)
                .reader(stockMarketDataReader) // get list of stock by date from job parameter
                .processor(stockSimpleMovingAverageProcessor)
                .writer(stockMovingAverageWriter)
                .build();
    }

    @Bean
    public StockSimpleMovingAverageProcessor simpleMovingAverageProcessor() {
        return new StockSimpleMovingAverageProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<StockSimpleMovingAverage> stockMovingAverageWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<StockSimpleMovingAverage>()
                .sql("INSERT INTO stock_price_sma (ticker, date, value_10, value_20, value_50) " +
                        "VALUES (:ticker, :date, :value10, :value20, :value50)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }


}