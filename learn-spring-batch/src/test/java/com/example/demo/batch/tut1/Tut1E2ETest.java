package com.example.demo.batch.tut1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

@ActiveProfiles({"tut1"})
@SpringBatchTest
@SpringJUnitConfig({BatchJobConfig.class, JobCompletionNotificationListener.class})
@EnableAutoConfiguration // Auto initialize some necessary beans (eg. dataSource, transactionManager)
@PropertySources({
        @PropertySource("classpath:application.properties")
})
public class Tut1E2ETest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JobLauncher tut1JobLauncher;
    @Autowired
    private PeopleRepository peopleRepository;

    private JobParameters defaultParameters() {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addString("file.input.classpath", "tut1/sample-data.csv");
        return builder.toJobParameters();
    }

    @Test
    public void testTut1Job(@Autowired Job tut1Job) throws Exception {
        List<People> expectedOutput = Arrays.asList(
                new People(1L, "JILL", "DOE"),
                new People(2L, "JOE", "DOE"),
                new People(3L, "JUSTIN", "DOE"),
                new People(4L, "JANE", "DOE"),
                new People(5L, "JOHN", "DOE"),
                new People(6L, "JUAN", "DOE")
        );

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultParameters());

        Assertions.assertEquals("COMPLETED", jobExecution.getExitStatus()
                .getExitCode());
        List<People> actualOutput = peopleRepository.findAll();
        Assertions.assertEquals(expectedOutput.size(), actualOutput.size());
        Assertions.assertIterableEquals(expectedOutput, actualOutput);
    }
}
