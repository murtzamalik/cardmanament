package com.cms.app.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Builds the CMS DataSource from the {@code cms.datasource.*} namespace instead of
 * {@code spring.datasource.*}. This is deliberate: this container is started with the
 * shared platform env-file, which defines the generic SPRING_DATASOURCE_* variables
 * pointing at another service's schema ("barkatpay"). Those env vars auto-bind to
 * spring.datasource.* and outrank application.properties, which previously forced CMS
 * to connect as the wrong user (ORA-00942 / "table does not exist").
 *
 * <p>Reading cms.datasource.* (fed only by CMS_SPRING_DATASOURCE_*) puts CMS out of their
 * reach. Declaring this bean also makes Spring Boot's DataSourceAutoConfiguration back
 * off, so SPRING_DATASOURCE_* is ignored here entirely.
 *
 * <p>DataSourceProperties is used (not a raw HikariDataSource bound directly) so that
 * {@code cms.datasource.url} maps to Hikari's {@code jdbcUrl} — Hikari has no {@code url}
 * setter and otherwise fails with "jdbcUrl is required with driverClassName".
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("cms.datasource")
    public DataSourceProperties cmsDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties cmsDataSourceProperties) {
        return cmsDataSourceProperties.initializeDataSourceBuilder().build();
    }
}
