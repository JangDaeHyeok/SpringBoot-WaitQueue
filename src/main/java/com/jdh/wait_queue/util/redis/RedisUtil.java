package com.jdh.wait_queue.util.redis;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Component
@AllArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    // Add Redis
    public void add(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // Add Sorted Set
    public Boolean addZSet(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    // Add Sorted Set
    public void addZSet(String key, String value) {
        double score = Instant.now().toEpochMilli();
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * Delete With Key
     */
    public Boolean delete(String key){
        return redisTemplate.delete(key);
    }

    /**
     * Delete Sorted Set With key, value
     */
    public Long deleteValue(String key, String value){
        return redisTemplate.opsForZSet().remove(key, value);
    }

    /**
     * Get Value With Key
     */
    public String getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Get Sorted Set Size
     */
    public Long getSize(String key){
        ZSetOperations<String, String> z = redisTemplate.opsForZSet();
        return z.size(key);
    }

    /**
     * Get Sorted Set Start ~ End
     */
    public Set<String> zRange(String key, Long start, Long end){
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * Get Sorted Set Rank
     */
    public Long getzRank(String key, String value){
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * Get Sorted Set Next
     */
    public String getNext(String key) {
        Set<String> result = redisTemplate.opsForZSet().range(key, 0, 0);

        return (result != null && !result.isEmpty()) ? result.iterator().next() : null;
    }
}