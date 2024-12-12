package space.gavinklfong.demo.batch.job.stockanalysis;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class CalculateStockTechnicalAnalysisIndicatorsJobConfig {

    @Bean
    public Job calculateStockTechnicalAnalysisIndicatorsSequentialJob(JobRepository jobRepository,
                                                            Step calculateSimpleMovingAverageStep,
                                                            Step calculateStockExponentialMovingAverageStep,
                                                            Step calculateMoneyFlowIndexStep) {
        return new JobBuilder("calculateStockTechnicalAnalysisIndicatorsSequentialJob", jobRepository)
                .start(calculateSimpleMovingAverageStep)
                .next(calculateStockExponentialMovingAverageStep)
                .next(calculateMoneyFlowIndexStep)
                .build();
    }

    @Bean
    public Job calculateStockTechnicalAnalysisIndicatorsSplitFlowJob(JobRepository jobRepository,
                                                                     Flow stockTechnicalAnalysisIndicatorsSplitFlow) {
        return new JobBuilder("calculateStockTechnicalAnalysisIndicatorsSplitFlowJob", jobRepository)
                .start(stockTechnicalAnalysisIndicatorsSplitFlow)
                .end().build();
    }

    @Bean
    public Flow stockTechnicalAnalysisIndicatorsSplitFlow(Flow stockTechnicalAnalysisIndicatorsFlow1,
                                                          Flow stockTechnicalAnalysisIndicatorsFlow2) {
        return new FlowBuilder<SimpleFlow>("stockTechnicalAnalysisIndicatorsSplitFlow")
                .split(new SimpleAsyncTaskExecutor("stock_indicator"))
                .add(stockTechnicalAnalysisIndicatorsFlow1,
                        stockTechnicalAnalysisIndicatorsFlow2)
                .end();
    }

    @Bean
    public Flow stockTechnicalAnalysisIndicatorsFlow1(Step calculateSimpleMovingAverageStep,
                                                      Step calculateStockExponentialMovingAverageStep,
                                                      Step calculateStockMACDStep) {
        return new FlowBuilder<SimpleFlow>("stockTechnicalAnalysisIndicatorsFlow1")
//                .start(calculateStockExponentialMovingAverageStep)
                .start(calculateSimpleMovingAverageStep)
                .next(calculateStockExponentialMovingAverageStep)
                .next(calculateStockMACDStep)
                .end();
    }

    @Bean
    public Flow stockTechnicalAnalysisIndicatorsFlow2(Step calculateMoneyFlowIndexStep) {
        return new FlowBuilder<SimpleFlow>("stockTechnicalAnalysisIndicatorsFlow2")
                .start(calculateMoneyFlowIndexStep)
                .end();
    }

//    @Bean
//    public TaskExecutor taskExecutor() {
//        return new SimpleAsyncTaskExecutor("stock_indicator");
//    }
}
