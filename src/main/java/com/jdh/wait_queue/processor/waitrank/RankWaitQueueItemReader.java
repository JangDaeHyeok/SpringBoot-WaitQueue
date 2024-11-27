package com.jdh.wait_queue.processor.waitrank;

import com.jdh.wait_queue.util.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankWaitQueueItemReader implements ItemReader<String> {

    @Value("${queue.wait}")
    private String waitQueueKey;

    private final RedisUtil redisUtil;

    private Set<String> dataSet;
    private Iterator<String> iterator;

    @Override
    public String read() {
        // 조회된 데이터가 없는 경우
        if (iterator == null) {
            // 현재 대기열에 있는 사용자 수 조회
            Long waitCount = redisUtil.getSize(waitQueueKey);
            log.info("[RankWaitQueueJobConfig] 현재 대기열에 있는 사용자 수 :: {}", waitCount);

            // data set 조회
            dataSet = redisUtil.zRange("waitingQueue", 0L, waitCount);

            // Iterator로 변환
            iterator = dataSet.iterator();
        }

        // Iterator에서 다음 데이터 조회
        String result = iterator.hasNext() ? iterator.next() : null;
        log.info("[RankWaitQueueItemReader] 대기열 순서 전송할 사용자 :: {}", result);

        // Iterator에 더 이상 데이터가 없는 경우
        if(result == null) {
            // 데이터 초기화
            initData();
        }

        return result;
    }

    /**
     * initialize dataFlux, iterator
     */
    private void initData() {
        dataSet = null;
        iterator = null;
    }
}
