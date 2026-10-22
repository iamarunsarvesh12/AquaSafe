package com.aquasafe.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${aquasafe.datasource.mysql.url}")
    private String mysqlUrl;

    @Value("${aquasafe.datasource.mysql.username}")
    private String mysqlUsername;

    @Value("${aquasafe.datasource.mysql.password}")
    private String mysqlPassword;

    @Value("${aquasafe.datasource.mysql.driver-class-name}")
    private String mysqlDriver;

    @Value("${aquasafe.datasource.h2.url}")
    private String h2Url;

    @Value("${aquasafe.datasource.h2.username}")
    private String h2Username;

    @Value("${aquasafe.datasource.h2.password}")
    private String h2Password;

    @Value("${aquasafe.datasource.h2.driver-class-name}")
    private String h2Driver;

    @Value("${DB_HOST:localhost}")
    private String dbHost;

    @Value("${DB_PORT:3306}")
    private int dbPort;

    private String activeDatabaseType = "UNKNOWN";

    @Bean
    @Primary
    public DataSource dataSource() {
        boolean mysqlAvailable = isMySQLAvailable();

        if (mysqlAvailable) {
            try {
                log.info("Connecting to MySQL Database at {}:{}...", dbHost, dbPort);
                DataSource ds = DataSourceBuilder.create()
                    .url(mysqlUrl)
                    .username(mysqlUsername)
                    .password(mysqlPassword)
                    .driverClassName(mysqlDriver)
                    .build();
                // Test connection
                try (Connection conn = ds.getConnection()) {
                    log.info("Successfully connected to MySQL database: {}", conn.getCatalog());
                    activeDatabaseType = "MySQL";
                    return ds;
                }
            } catch (Exception e) {
                log.warn("MySQL server detected on port {} but authentication/schema failed ({}). Switching to H2 MySQL-mode fallback.", dbPort, e.getMessage());
            }
        } else {
            log.warn("MySQL service not reachable on {}:{}. Falling back to in-memory H2 database with MySQL compatibility mode.", dbHost, dbPort);
        }

        activeDatabaseType = "Embedded H2 (MySQL Mode)";
        return DataSourceBuilder.create()
            .url(h2Url)
            .username(h2Username)
            .password(h2Password)
            .driverClassName(h2Driver)
            .build();
    }

    private boolean isMySQLAvailable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(dbHost, dbPort), 800);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getActiveDatabaseType() {
        return activeDatabaseType;
    }
}
