package com.uraltrans.logisticparamservice.config.datasource;

import com.uraltrans.logisticparamservice.entity.rjd.SegmentationAnalysisT13;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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
@EnableJpaRepositories(basePackages = { "com.uraltrans.logisticparamservice.repository.rjd" },
                       entityManagerFactoryRef = "rjdEntityManagerFactory")
public class RjdDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.rjd")
    public DataSource rjdDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "rjdEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("rjdDataSource") DataSource dataSource) {

        Map<String, String> properties = new HashMap<>();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        properties.put(Environment.HBM2DDL_AUTO, "none");
        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("com.uraltrans.logisticparamservice.entity.rjd")
                .build();
    }

    @Bean(name = "rjdTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("rjdEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}