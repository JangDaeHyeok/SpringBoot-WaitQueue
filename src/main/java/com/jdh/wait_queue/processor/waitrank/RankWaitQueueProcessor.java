package com.jdh.wait_queue.processor.waitrank;

import com.jdh.wait_queue.util.redis.RedisUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankWaitQueueProcessor implements ItemProcessor<String, String> {

    @Value("${queue.wait}")
    private String waitQueueKey;

    private final RedisUtil redisUtil;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public String process(@NonNull String key) {
        // key가 빈 값인지 체크
        if(checkKeyEmpty(key))
            return null;

        // 현재 대기 순서 조회
        Long rank = redisUtil.getzRank(waitQueueKey, key);

        // 현재 대기 순서 전달
        if(rank != null)
            messagingTemplate.convertAndSend("/topic/" + key, "대기순번 " + (rank + 1));

        return key;
    }

    /**
     * check key empty
     */
    private boolean checkKeyEmpty(String key) {
        return key.isEmpty();
    }
}
