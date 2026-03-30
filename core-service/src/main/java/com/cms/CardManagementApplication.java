package com.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.cms")
@EntityScan(basePackages = "com.cms.dal.entity")
@EnableJpaRepositories(basePackages = "com.cms.dal.repository")
public class CardManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardManagementApplication.class, args);
    }
}
