package com.example.demo.batch.tut1;

import com.example.demo.batch.tut1.data.People;
import com.example.demo.batch.tut1.data.PeopleRepository;
import com.example.demo.batch.tut1.data.Person;
import com.example.demo.batch.tut1.service.JobNotificationListener;
import com.example.demo.batch.tut1.service.PeopleItemWriter;
import com.example.demo.batch.tut1.service.PersonItemProcessor;
import com.example.demo.batch.tut1.service.StepNotificationListener;
import com.example.demo.batch.websocket.service.JobExecutionEventEmitter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
@Profile({"tut1", "api"})
public class BatchJobConfig {
    @Bean
    public Job tut1Job(JobRepository jobRepository, Step step1, JobNotificationListener listener) {
        return new JobBuilder("tut1Job", jobRepository).listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, JpaTransactionManager transactionManager,
                      FlatFileItemReader<Person> reader, PersonItemProcessor processor,
                      PeopleItemWriter writer, StepExecutionListener stepExecutionListener) {
        return new StepBuilder("step1", jobRepository).<Person, People>chunk(3, transactionManager)
                .listener(stepExecutionListener)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build()
                ;
    }

    @StepScope
    @Bean
    // The SpEL to lookup jobParameters data map will work only when @StepScope is present.
    public FlatFileItemReader<Person> reader(@Value("#{jobParameters['file.input.classpath']}") String input) {
        return new FlatFileItemReaderBuilder<Person>().name("personItemReader")
                .resource(new ClassPathResource(input))
                .delimited()
                .names("firstName", "lastName")
                .targetType(Person.class)
                .build();
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public PeopleItemWriter writer(PeopleRepository peopleRepository) {
        return new PeopleItemWriter(peopleRepository);
    }

    @Bean
    public JobNotificationListener jobNotificationListener(JobExecutionEventEmitter jobExecutionEventEmitter) {
        return new JobNotificationListener(jobExecutionEventEmitter);
    }

    @Bean
    public StepNotificationListener stepNotificationListener(JobExecutionEventEmitter jobExecutionEventEmitter) {
        return new StepNotificationListener(jobExecutionEventEmitter);
    }
}
