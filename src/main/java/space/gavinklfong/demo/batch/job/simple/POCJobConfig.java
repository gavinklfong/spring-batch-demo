package space.gavinklfong.demo.batch.job.simple;

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
public class POCJobConfig {

    @Bean
    public Job pocJob(JobRepository jobRepository, Flow splitFlow) {
        return new JobBuilder("pocJob", jobRepository)
                .start(splitFlow)
                .end()
                .build();
    }

    @Bean
    public Flow flow1(Step step1) {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(step1)
                .build();
    }

    @Bean
    public Flow flow2(Step step2) {
        return new FlowBuilder<SimpleFlow>("flow2")
                .start(step2)
                .build();
    }

    @Bean
    public Flow splitFlow(Flow flow1, Flow flow2) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(new SimpleAsyncTaskExecutor("spring_batch"))
                .add(flow1, flow2)
                .build();
    }
}
