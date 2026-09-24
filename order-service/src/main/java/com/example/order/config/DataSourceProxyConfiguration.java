package com.example.order.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceProxyConfiguration {

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Bean(name = "realDataSource")
    public HikariDataSource realDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(dbUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    @Bean("dataSourceProxy")
    @Primary
    public DataSourceProxy dataSourceProxy(HikariDataSource realDataSource) {
        return new DataSourceProxy(realDataSource);
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProxy dataSourceProxy) {
        return dataSourceProxy;
    }
}
