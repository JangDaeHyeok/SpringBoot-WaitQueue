package com.jdh.wait_queue.config.job;

import com.jdh.wait_queue.processor.job.RedisToJobQueueItemReader;
import com.jdh.wait_queue.processor.job.RedisToJobQueueProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisToJobQueueJobConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final RedisToJobQueueItemReader redisToJobQueueItemReader;

    private final RedisToJobQueueProcessor redisToJobQueueProcessor;

    @Bean("moveUsersToJobQueueJob")
    public Job job() {
        return new JobBuilder("moveUsersToJobQueue", jobRepository)
                .start(moveUsersToJobQueueStep())
                .build();
    }

    @Bean
    public Step moveUsersToJobQueueStep() {
        return new StepBuilder("moveUsersToJobQueueStep", jobRepository)
                .<String, String>chunk(1, transactionManager) // 1명씩 처리
                .reader(redisToJobQueueItemReader)
                .processor(redisToJobQueueProcessor)
                .writer(items -> items.forEach(item -> log.info("[RedisToJobQueueJobConfig] Redis To Job Queue Job Processed item: {}", item))) // 처리된 데이터를 출력
                .build();
    }

}
