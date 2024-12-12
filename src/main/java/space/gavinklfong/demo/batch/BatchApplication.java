package space.gavinklfong.demo.batch;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication implements CommandLineRunner {
	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	JobLocator jobLocator;

	public static void main(String[] args) {
		System.exit(
				SpringApplication.exit(
						SpringApplication.run(BatchApplication.class, args)
				)
		);
	}

	@Override
	public void run(String... args) throws Exception {
		jobLauncher.run(jobLocator.getJob("importUserJob"), new JobParameters());

		JobParameters jobParametersForStockImport = new JobParametersBuilder()
				.addLocalDateTime("timestamp", LocalDateTime.now())
				.toJobParameters();
		jobLauncher.run(jobLocator.getJob("importStockMarketDataJob"), jobParametersForStockImport);

		LocalDate effectiveDate = LocalDate.parse("2023-07-01");
		while (effectiveDate.isBefore(LocalDate.parse("2023-07-20"))) {

			JobParameters jobParametersForTechnicalAnalysisIndictorsJob = new JobParametersBuilder()
					.addLocalDate("date", effectiveDate)
					.addLocalDateTime("timestamp", LocalDateTime.now())
					.toJobParameters();
//			jobLauncher.run(jobLocator.getJob("calculateStockTechnicalAnalysisIndicatorsSequentialJob"), jobParametersForTechnicalAnalysisIndictorsJob);
			jobLauncher.run(jobLocator.getJob("calculateStockTechnicalAnalysisIndicatorsSplitFlowJob"), jobParametersForTechnicalAnalysisIndictorsJob);

			effectiveDate = effectiveDate.plusDays(1);
		}

		while (true) {
			Thread.sleep(5000);
		}

	}
}
