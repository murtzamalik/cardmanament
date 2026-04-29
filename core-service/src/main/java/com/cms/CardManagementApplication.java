package com.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages = "com.cms")
@EntityScan(basePackages = "com.cms.dal.entity")
@EnableJpaRepositories(basePackages = "com.cms.dal.repository")
@EnableScheduling
@EnableAspectJAutoProxy

public class CardManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardManagementApplication.class, args);
    }
}
