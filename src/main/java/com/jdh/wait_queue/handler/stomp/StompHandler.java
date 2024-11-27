package com.jdh.wait_queue.handler.stomp;

import com.jdh.wait_queue.util.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final RedisUtil redisUtil;

    @Value("${queue.wait}")
    private String waitQueueKey;

    @Value("${queue.job}")
    private String jobQueueKey;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // websocket 연결 시
        if (StompCommand.CONNECT == accessor.getCommand()) {
            log.info("[StompHandler] CONNECT header :: " + message.getHeaders());
        }
        // websocket 구독 요청 시
        else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            // path에서 key 획득
            String key = Objects.requireNonNull(accessor.getDestination())
                    .substring(accessor.getDestination().lastIndexOf("/") + 1);

            // redis 대기열 저장
            redisUtil.addZSet(waitQueueKey, key);

            // redis session key 저장
            redisUtil.add(accessor.getSessionId(), key);
            log.info("[StompHandler] ({}) {} key joined queue.", accessor.getSessionId(), key);
        }
        // websocket 연결 종료 시
        else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            log.info("[StompHandler] DISCONNECT header :: " + message.getHeaders());

            // 세션 id로 key 가져오기
            String key = String.valueOf(redisUtil.getValue(accessor.getSessionId()));
            log.info("[StompHandler] ({}) {} key left queue.", accessor.getSessionId(), key);

            // redis 대기열에서 제거
            redisUtil.deleteValue(waitQueueKey, key);

            // redis 작업열에서 제거
            redisUtil.deleteValue(jobQueueKey, key);

            // redis session 제거
            redisUtil.delete(accessor.getSessionId());
        }

        return message;
    }

}
