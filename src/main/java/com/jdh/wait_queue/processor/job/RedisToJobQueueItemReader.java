package com.jdh.wait_queue.processor.job;

import com.jdh.wait_queue.util.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisToJobQueueItemReader implements ItemReader<String> {

    @Value("${queue.wait}")
    private String waitQueueKey;

    @Value("${queue.job}")
    private String jobQueueKey;

    @Value("${queue.max-job-size}")
    private long maxJobSize;

    private final RedisUtil redisUtil;

    @Override
    public String read() {
        // 작업열이 꽉 찬 경우
        if(!checkMaxJobQueue()) {
            return null;
        }

        // 대기열의 다음 사용자 조회
        String result = String.valueOf(redisUtil.getNext(waitQueueKey));

        log.info("[RedisToJobQueueItemReader] 작업열로 넘어갈 사용자 :: {}", result);

        return "null".equals(result) || result.isEmpty() ? null : result; // 가장 오래된 사용자 반환
    }

    /**
     * check job queue count max
     */
    private boolean checkMaxJobQueue() {
        // 현재 작업열에 있는 사용자 수 조회
        Long jobCount = redisUtil.getSize(jobQueueKey);

        // 현재 작업열이 존재하지 않는 경우 default 0
        jobCount = Objects.requireNonNullElse(jobCount, 0L);

        log.info("[RedisToJobQueueItemReader] 현재 작업열에 있는 사용자 수 :: {}", jobCount);

        return jobCount < maxJobSize;
    }

}
