package com.jdh.wait_queue.processor.job;

import com.jdh.wait_queue.util.redis.RedisUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisToJobQueueProcessor implements ItemProcessor<String, String> {

    @Value("${queue.wait}")
    private String waitQueueKey;

    @Value("${queue.job}")
    private String jobQueueKey;

    private final RedisUtil redisUtil;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public String process(@NonNull String key) {
        // 작업열에 추가하기 전, 대기열에 사용자가 존재하는지 확인
        Boolean isPossible = checkKeyExistsWaitQueue(key);

        // 대기열에 없거나 이미 처리 중인 사용자라면 null 반환
        if(Boolean.FALSE.equals(isPossible)) {
            return null;
        }

        // 대기열에서 처리한 사용자 제거
        redisUtil.deleteValue("waitingQueue", key);

        // 작업열로 사용자 추가
        redisUtil.addZSet(jobQueueKey, key);

        // 작업열로 이동한 사용자에게 STOMP를 통해 알림 전송
        messagingTemplate.convertAndSend("/topic/" + key, "Now Asctive!");

        return key;
    }

    /**
     * Check Key exists at Wait Queue
     */
    private Boolean checkKeyExistsWaitQueue(String key) {
        // 대기열에서 key의 rank 조회
        long rank = redisUtil.getzRank(waitQueueKey, key);

        log.info("[RedisToJobQueueProcessor] {} 사용자 rank :: {}", key, rank);

        return rank >= 0;
    }

}
