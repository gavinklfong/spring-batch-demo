package space.gavinklfong.demo.batch.job.simple;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import space.gavinklfong.demo.batch.dto.InvestmentAccountHolding;
import space.gavinklfong.demo.batch.job.common.InvestmentAccountHoldingFieldSetMapper;

import java.io.IOException;
import java.net.MalformedURLException;

@Configuration
public class InvestmentAccountHoldingImportPartitionJobConfig {

    @Bean
    public Job importInvestmentAccountHoldingPartitionJob(JobRepository jobRepository,
                                                 Step masterStep,
                                                 JobCompletionNotificationListener listener) {
        return new JobBuilder("importInvestmentAccountHoldingPartitionJob", jobRepository)
                .listener(listener)
                .start(masterStep)
                .build();
    }

    @Bean
    @StepScope
    public Partitioner multipleInvestmentAccountHoldingFilesPartitioner() throws IOException {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;
        try {
            resources = resolver.getResources("classpath:data/investment/investment-account-*.csv");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        partitioner.setResources(resources);
        partitioner.partition(10);
        return partitioner;
    }

    @Bean
    public Step masterStep(JobRepository jobRepository,
                           Step importInvestmentAccountHoldingStep) throws IOException {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner("importInvestmentAccountHoldingPartitionStep", multipleInvestmentAccountHoldingFilesPartitioner())
                .step(importInvestmentAccountHoldingStep)
                .taskExecutor(new SimpleAsyncTaskExecutor("inv_import"))
                .build();
    }


    @Bean
    public Step importInvestmentAccountHoldingPartitionStep(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                                                   FlatFileItemReader<InvestmentAccountHolding> investmentAccountHoldingReaderWithContext,
                                                   JdbcBatchItemWriter<InvestmentAccountHolding> investmentAccountHoldingWriter) {
        return new StepBuilder("importInvestmentAccountHoldingPartitionStep", jobRepository)
                .<InvestmentAccountHolding, InvestmentAccountHolding> chunk(10000, transactionManager)
                .reader(investmentAccountHoldingReaderWithContext)
                .writer(investmentAccountHoldingWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<InvestmentAccountHolding> investmentAccountHoldingReaderWithContext(
            @Value("#{stepExecutionContext[fileName]}") String filename
    ) throws MalformedURLException {
        return new FlatFileItemReaderBuilder<InvestmentAccountHolding>()
                .name("investmentAccountHoldingItemReader")
                .resource(new UrlResource(filename))
                .linesToSkip(1)
                .delimited()
                .names("accountNumber", "date", "AAPL", "SBUX", "MSFT", "CSCO", "QCOM", "META", "AMZN", "TSLA", "AMD", "NFLX")
                .fieldSetMapper(new InvestmentAccountHoldingFieldSetMapper())
                .build();
    }
}
