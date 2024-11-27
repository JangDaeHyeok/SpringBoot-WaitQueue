package com.jdh.wait_queue.api.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;

    @Qualifier("moveUsersToJobQueueJob")
    private final Job moveUsersToJobQueue;

    @Qualifier("rankWaitQueueJob")
    private final Job rankWaitQueue;

    @GetMapping("/run-batch-to-job")
    public String runBatchMoveUsersToJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("moveUsersToJobQueueRunId", UUID.randomUUID().toString()) // 유일한 파라미터 추가
                    .toJobParameters();

            jobLauncher.run(moveUsersToJobQueue, jobParameters);

            return "Batch job executed successfully!";
        } catch (Exception e) {
            log.error("[BatchController] :: ", e);
            return "Failed to execute batch job.";
        }
    }

    @GetMapping("/run-batch-rank-wait")
    public String runBatchRankWaitQueue() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("rankWaitQueueRunId", UUID.randomUUID().toString()) // 유일한 파라미터 추가
                    .toJobParameters();

            jobLauncher.run(rankWaitQueue, jobParameters);

            return "Batch job executed successfully!";
        } catch (Exception e) {
            log.error("[BatchController] :: ", e);
            return "Failed to execute batch job.";
        }
    }

}
