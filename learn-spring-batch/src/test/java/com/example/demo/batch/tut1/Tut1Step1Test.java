package com.example.demo.batch.tut1;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

@ActiveProfiles("tut1")
@SpringBatchTest
// This annotation imports StepScopeTestExecutionListener and JobScopeTestExecutionListener. (Starting from spring batch version 4.1)
@SpringJUnitConfig({BatchJobConfig.class})
@EnableAutoConfiguration
public class Tut1Step1Test {

    @Autowired
    private FlatFileItemReader<Person> reader;
    @Autowired
    private PersonItemProcessor processor;
    @Autowired
    private PeopleItemWriter writer;
    @MockBean
    private PeopleRepository peopleRepository;

    public JobParameters defaultJobParameters() {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("file.input.classpath", "tut1/sample-data-one.csv");
        return builder.toJobParameters();
    }

    @Test
    public void givenFlatFileReaderStep_whenReadCalled_thenSuccess() throws Exception {
        // Given
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(defaultJobParameters());

        // When
        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            // doInStepScope is required to inject jobParameters into step scoped bean initialization.
            // See "reader" bean definition in BatchJobConfig.java
            Person person;
            reader.open(stepExecution.getExecutionContext());
            while ((person = reader.read()) != null) {
                // Then
                Assertions.assertEquals("Jill", person.firstName());
                Assertions.assertEquals("Doe", person.lastName());
            }
            reader.close();

            return null;
        });
    }

    @Test
    public void givenPersonItemProcessor_whenProcessCalled_thenSuccess() throws Exception {
        // Given
        Person input = new Person("Jill", "Doe");
        People expected = new People("JILL", "DOE");

        // When
        People output = processor.process(input);

        // Then
        Assertions.assertEquals(expected, output);
    }

    @Test
    public void givenPeopleItemWriterStep_whenWriteCalled_thenSuccess() {
        // Given
        People input = new People("JILL", "DOE");

        // When
        writer.write(new Chunk<>(List.of(input)));

        // Then
        Mockito.verify(peopleRepository, Mockito.times(1))
                .saveAll(List.of(input));
    }
}
