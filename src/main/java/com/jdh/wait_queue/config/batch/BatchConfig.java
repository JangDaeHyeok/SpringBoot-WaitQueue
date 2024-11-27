package com.jdh.wait_queue.config.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    /**
     * batch 에서 사용할 h2 기본 테이블 생성
     */
    @Bean
    public ApplicationRunner initializeBatchTables(DataSource dataSource) {
        return args -> {
            Resource resource = new ClassPathResource("org/springframework/batch/core/schema-h2.sql");
            ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
        };
    }

}
