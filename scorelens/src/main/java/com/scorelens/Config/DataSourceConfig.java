package com.scorelens.Config;

import com.scorelens.Client.GgSecretManager;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Autowired
    private GgSecretManager secretManager;

    @Bean
    public DataSource dataSource() throws Exception {
        String projectId = "scorelens-461406";

        String url = secretManager.accessSecret(projectId, "DB-URL");
        String username = secretManager.accessSecret(projectId, "DB-USERNAME");
        String password = secretManager.accessSecret(projectId, "DB-PASSWORD");

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        return dataSource;
    }

}
