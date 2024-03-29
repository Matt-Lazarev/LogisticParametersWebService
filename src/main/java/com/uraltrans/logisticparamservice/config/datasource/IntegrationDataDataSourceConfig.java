package com.uraltrans.logisticparamservice.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.uraltrans.logisticparamservice.repository.integration"},
        entityManagerFactoryRef = "integrationEntityManagerFactory"
)
public class IntegrationDataDataSourceConfig {

    @Bean(name = "integrationDataDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.integration-data")
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean(name = "integrationEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("integrationDataDataSource") DataSource dataSource) {
        Map<String, String> properties = Map.of("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        return builder
                .dataSource(dataSource)
                .packages("com.uraltrans.logisticparamservice.entity.integration")
                .properties(properties)
                .build();
    }

    @Bean(name = "integrationTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("integrationEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
