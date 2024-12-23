package com.jdh.wait_queue.api.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;

    @Qualifier("moveUsersToJobQueueJob")
    private final Job moveUsersToJobQueue;

    @Qualifier("rankWaitQueueJob")
    private final Job rankWaitQueue;

    @Scheduled(fixedRate = 5000)
    public void runBatch() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("runBatchId", UUID.randomUUID().toString()) // 유일한 파라미터 추가
                    .toJobParameters();

            // 대기열에서 작업열 이동 Job 실행
            jobLauncher.run(moveUsersToJobQueue, jobParameters);

            // 현재 대기열 순번 Send Job 실행
            jobLauncher.run(rankWaitQueue, jobParameters);
        } catch (Exception e) {
            log.error("[BatchController] :: ", e);
        }
    }

}
