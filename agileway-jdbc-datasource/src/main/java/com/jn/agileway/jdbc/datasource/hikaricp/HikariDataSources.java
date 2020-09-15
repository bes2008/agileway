package com.jn.agileway.jdbc.datasource.hikaricp;

import com.jn.agileway.jdbc.datasource.DataSourceProperties;
import com.jn.agileway.jdbc.Jdbcs;
import com.jn.langx.util.Emptys;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class HikariDataSources {
    public static DataSource createDataSource(final DataSourceProperties props) {
        Properties driverProps = props.getDriverProps();
        HikariConfig config = null;
        if (Emptys.isNotEmpty(driverProps)) {
            config = new HikariConfig(driverProps);
        } else {
            config = new HikariConfig();
        }
        config.setDriverClassName(props.getDriverClassName());
        config.setJdbcUrl(props.getUrl());
        config.setUsername(props.getUsername());
        config.setPassword(props.getPassword());
        config.setPoolName(props.getName());
        config.setCatalog(props.getCatalog());
        config.setSchema(props.getSchema());
        config.setLeakDetectionThreshold(props.getLeakDetectionThresholdInMills());
        config.setConnectionTimeout(props.getConnectionTimeoutInMills());
        config.setValidationTimeout(props.getValidationTimeoutInMills());
        config.setConnectionInitSql(props.getValidationQuery());
        config.setIdleTimeout(props.getIdleTimeoutInMills());
        config.setMaxLifetime(props.getMaxLifetimeInMills());
        config.setMaximumPoolSize(props.getMaxPoolSize());
        config.setMinimumIdle(props.getMinIdle());
        config.setAutoCommit(props.isAutoCommit());
        int txIsoLevel = -1;
        try {
            txIsoLevel = Jdbcs.getTransactionIsolation(props.getTransactionIsolationName());
        } catch (Throwable t) {

        } finally {
            if (txIsoLevel == -1) {
                props.setTransactionIsolationName("TRANSACTION_READ_COMMITTED");
            }
        }
        config.setTransactionIsolation(props.getTransactionIsolationName());
        config.setReadOnly(props.isReadOnly());
        return new HikariDataSource(config);
    }
}
