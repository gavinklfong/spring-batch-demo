package space.gavinklfong.demo.batch.job.stockanalysis;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalculateStockTechnicalAnalysisIndicatorsJobConfig {

    @Bean
    public Job calculateStockTechnicalAnalysisIndicatorsJob(JobRepository jobRepository,
                                                            Step calculateSimpleMovingAverageStep,
                                                            Step calculateStockExponentialMovingAverageStep,
                                                            Step calculateMoneyFlowIndexStep) {
        return new JobBuilder("calculateStockTechnicalAnalysisIndicatorsJob", jobRepository)
                .start(calculateSimpleMovingAverageStep)
                .next(calculateStockExponentialMovingAverageStep)
                .next(calculateMoneyFlowIndexStep)
                .build();
    }

//    @Bean
//    public Job calculateStockTechnicalAnalysisIndicatorsJob(JobRepository jobRepository,
//                                                            Flow stockTechnicalAnalysisIndicatorsSplitFlow) {
//        return new JobBuilder("calculateStockTechnicalAnalysisIndicatorsJob", jobRepository)
//                .start(stockTechnicalAnalysisIndicatorsSplitFlow)
//                .build()
//                .build();
//    }

//    @Bean
//    public Flow stockTechnicalAnalysisIndicatorsSplitFlow(Flow stockTechnicalAnalysisIndicatorsFlow1,
//                                                          Flow stockTechnicalAnalysisIndicatorsFlow2) {
//        return new FlowBuilder<SimpleFlow>("stockTechnicalAnalysisIndicatorsSplitFlow")
//                .split(taskExecutor())
//                .add(stockTechnicalAnalysisIndicatorsFlow1,
//                        stockTechnicalAnalysisIndicatorsFlow2)
//                .build();
//    }
//
//    @Bean
//    public Flow stockTechnicalAnalysisIndicatorsFlow1(Step calculateSimpleMovingAverageStep,
//                                                      Step calculateStockExponentialMovingAverageStep) {
//        return new FlowBuilder<SimpleFlow>("stockTechnicalAnalysisIndicatorsFlow1")
//                .start(calculateSimpleMovingAverageStep)
//                .next(calculateStockExponentialMovingAverageStep)
//                .build();
//    }
//
//    @Bean
//    public Flow stockTechnicalAnalysisIndicatorsFlow2(Step calculateMoneyFlowIndexStep) {
//        return new FlowBuilder<SimpleFlow>("stockTechnicalAnalysisIndicatorsFlow2")
//                .start(calculateMoneyFlowIndexStep)
//                .build();
//    }

//    @Bean
//    public TaskExecutor taskExecutor() {
//        return new SimpleAsyncTaskExecutor("spring_batch");
//    }
}
