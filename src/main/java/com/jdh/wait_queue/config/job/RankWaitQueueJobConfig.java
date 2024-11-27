package com.jdh.wait_queue.config.job;

import com.jdh.wait_queue.processor.waitrank.RankWaitQueueItemReader;
import com.jdh.wait_queue.processor.waitrank.RankWaitQueueProcessor;
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
public class RankWaitQueueJobConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final RankWaitQueueItemReader rankWaitQueueItemReader;

    private final RankWaitQueueProcessor rankWaitQueueProcessor;

    @Bean("rankWaitQueueJob")
    public Job job() {
        return new JobBuilder("rankWaitQueue", jobRepository)
                .start(rankWaitQueueJobStep())
                .build();
    }

    @Bean
    public Step rankWaitQueueJobStep() {
        return new StepBuilder("rankWaitQueueJob", jobRepository)
                .<String, String>chunk(10, transactionManager) // 10명씩 처리
                .reader(rankWaitQueueItemReader)
                .processor(rankWaitQueueProcessor)
                .writer(items -> items.forEach(item -> log.info("[RankWaitQueueJobConfig] Rank Wait Queue Job Processed item: {}", item))) // 처리된 데이터를 출력
                .build();
    }

}
