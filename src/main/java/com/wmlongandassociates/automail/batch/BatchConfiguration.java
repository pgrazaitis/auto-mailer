package com.wmlongandassociates.automail.batch;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;

import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.support.DatabaseType;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Slf4j
@Configuration
public class BatchConfiguration {

    @Value("${directory.input}")
    private String inputDirectory;

    @Autowired
    ApplicationArguments arguments;

@Bean
public DataSource dataSource() {
    EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
    return embeddedDatabaseBuilder.addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
            .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
            .setType(EmbeddedDatabaseType.H2)
            .build();
}

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("batch-");
        executor.initialize();
        return executor;
    }


    @Bean
    public ItemReader<File> fileReader() throws IOException {
        List<File> files = Files.walk(Paths.get(inputDirectory))
                .filter(Files::isRegularFile)
                .filter(Predicate.not(Files::isDirectory))
                .map(Path::toFile)
                .collect(Collectors.toList());
        return new IteratorItemReader<>(files);
    }




    @Bean(name = "emailSenderJob")
    public Job emailSenderJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step emailStep) {
        return new JobBuilder("emailSenderJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(emailStep)
                .end()
                .build();
    }

    @Bean(name = "emailSenderStep")
    public Step emailStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, EmailSendListener processorListener, EmailSendProcessor processor, EmailWriteListener writeListener, EmailItemWriter writer) throws IOException {
        return new StepBuilder("emailSenderStep", jobRepository)
                .<File, File> chunk(10, transactionManager)
                .reader(fileReader())
                .processor(processor)
                .listener(processorListener)
                .faultTolerant()
                .retry(MessagingException.class)
                .retryLimit(3)
                .writer(writer)
                .listener(writeListener)
                .build();
    }

}