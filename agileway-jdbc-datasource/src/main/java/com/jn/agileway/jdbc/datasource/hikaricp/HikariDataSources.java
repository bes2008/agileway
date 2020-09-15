package com.jn.agileway.jdbc.datasource.hikaricp;

import com.jn.agileway.jdbc.datasource.DataSourceProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.UtilityElf;

import javax.sql.DataSource;
import java.io.File;

public class HikariDataSources {
    public static DataSource createDataSource(final DataSourceProperties props) {
        final String driverPropsFilePath = props.getDriverPropsFile();
        HikariConfig config = null;
        if (driverPropsFilePath != null && new File(driverPropsFilePath).exists()) {
            config = new HikariConfig(driverPropsFilePath);
        }
        else {
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
        config.setDataSourceClassName(props.getDataSourceClassName());
        int txIsoLevel = -1;
        try {
            txIsoLevel = UtilityElf.getTransactionIsolation(props.getTransactionIsolationName());
        }
        catch (Throwable t) {}
        finally {
            if (txIsoLevel == -1) {
                props.setTransactionIsolationName("TRANSACTION_READ_COMMITTED");
            }
        }
        config.setTransactionIsolation(props.getTransactionIsolationName());
        config.setReadOnly(props.isReadOnly());
        return (DataSource)new HikariDataSource(config);
    }
}
