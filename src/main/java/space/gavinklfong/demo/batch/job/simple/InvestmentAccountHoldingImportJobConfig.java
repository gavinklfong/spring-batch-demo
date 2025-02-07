package space.gavinklfong.demo.batch.job.simple;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import space.gavinklfong.demo.batch.dto.InvestmentAccountHolding;
import space.gavinklfong.demo.batch.job.common.InvestmentAccountHoldingFieldSetMapper;

import javax.sql.DataSource;

@Configuration
public class InvestmentAccountHoldingImportJobConfig {

    @Bean
    public Job importInvestmentAccountHoldingJob(JobRepository jobRepository,
                                                 Step importInvestmentAccountHoldingStep,
                                                 JobCompletionNotificationListener listener) {
        return new JobBuilder("importInvestmentAccountHoldingJob", jobRepository)
                .listener(listener)
                .start(importInvestmentAccountHoldingStep)
                .build();
    }

    @Bean
    public Step importInvestmentAccountHoldingStep(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      MultiResourceItemReader<InvestmentAccountHolding> investmentAccountHoldingMultiResourceReader,
                      JdbcBatchItemWriter<InvestmentAccountHolding> investmentAccountHoldingWriter) {
        return new StepBuilder("importInvestmentAccountHoldingStep", jobRepository)
                .<InvestmentAccountHolding, InvestmentAccountHolding> chunk(1000, transactionManager)
                .reader(investmentAccountHoldingMultiResourceReader)
                .writer(investmentAccountHoldingWriter)
//                .taskExecutor(new SimpleAsyncTaskExecutor("inv_import"))
                .build();
    }

    @Bean
    public FlatFileItemReader<InvestmentAccountHolding> investmentAccountHoldingReader() {
        return new FlatFileItemReaderBuilder<InvestmentAccountHolding>()
                .name("investmentAccountHoldingItemReader")
                .linesToSkip(1)
                .delimited()
                .names("accountNumber", "date", "AAPL", "SBUX", "MSFT", "CSCO", "QCOM", "META", "AMZN", "TSLA", "AMD", "NFLX")
                .fieldSetMapper(new InvestmentAccountHoldingFieldSetMapper())
                .build();
    }

    @Bean
    public MultiResourceItemReader<InvestmentAccountHolding> investmentAccountHoldingMultiResourceReader(
            @Value("classpath:data/investment/investment-account-*.csv") Resource[] resources) {
        return new MultiResourceItemReaderBuilder<InvestmentAccountHolding>()
                .name("investmentAccountHoldingMultiResourceReader")
                .delegate(investmentAccountHoldingReader())
                .resources(resources)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<InvestmentAccountHolding> investmentAccountHoldingWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<InvestmentAccountHolding>()
                .sql("INSERT INTO investment_account_holding (account_number, date, aapl, sbux, msft, csco, qcom, meta, amzn, tsla, amd, nflx) " +
                        "VALUES (:accountNumber, :date, :aapl, :sbux, :msft, :csco, :qcom, :meta, :amzn, :tsla, :amd, :nflx) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "aapl = :aapl, " +
                        "sbux = :sbux, " +
                        "msft = :msft, " +
                        "csco = :csco, " +
                        "qcom = :qcom, " +
                        "meta = :meta, " +
                        "amzn = :amzn, " +
                        "tsla = :tsla, " +
                        "amd = :amd, " +
                        "nflx = :nflx")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }
}
