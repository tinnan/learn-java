package com.example.demo.batch.tut1;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
@Profile({"tut1", "api"})
public class BatchJobConfig {

    @Profile({"tut1"})
    @Bean
    public JobLauncher tut1JobLauncher(JobRepository jobRepository) throws Exception {
        final TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public Job tut1Job(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("tut1Job", jobRepository).listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, JpaTransactionManager transactionManager,
                      FlatFileItemReader<Person> reader, PersonItemProcessor processor,
                      PeopleItemWriter writer) {
        return new StepBuilder("step1", jobRepository).<Person, People>chunk(3, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build()
                ;
    }

    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>().name("personItemReader")
                .resource(new ClassPathResource(
                        "tut1/sample-data.csv"))
                .delimited()
                .names("firstName", "lastName")
                .targetType(Person.class)
                .build();
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }
}
