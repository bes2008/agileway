package com.jn.agileway.jdbc.datasource;

import com.jn.langx.util.Preconditions;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceRegistry implements DataSourceFactory {
    private ConcurrentHashMap<String, DataSource> dataSourceRegistry = new ConcurrentHashMap<String, DataSource>();

    @Override
    public DataSource get(DataSourceProperties dataSourceProperties) {
        String name = dataSourceProperties.getName();
        Preconditions.checkNotNull(name, "the datasource name is null");

        DataSource dataSource = dataSourceRegistry.get(name);
        if (dataSource == null) {
            String implementationKey = dataSourceProperties.getImplementationKey();
            DataSourceFactory delegate = DataSourceFactoryProvider.getInstance().get(implementationKey);
            if (delegate != null) {
                dataSource = delegate.get(dataSourceProperties);
            }
            if (dataSource != null) {
                dataSourceRegistry.putIfAbsent(name, dataSource);
            }
        }
        return dataSource;
    }

    @Override
    public DataSource get(Properties properties) {
        String name = properties.getProperty(DataSourceConstants.DATASOURCE_NAME);
        Preconditions.checkNotNull(name, "the datasource name is null");

        DataSource dataSource = dataSourceRegistry.get(name);
        if (dataSource == null) {
            String implementationKey = properties.getProperty(DataSourceConstants.DATASOURCE_IMPLEMENT_KEY);
            DataSourceFactory delegate = DataSourceFactoryProvider.getInstance().get(implementationKey);
            if (delegate != null) {
                dataSource = delegate.get(properties);
            }
            if (dataSource != null) {
                dataSourceRegistry.putIfAbsent(name, dataSource);
            }
        }
        return dataSource;
    }

    public void register(String dataSourceName, DataSource dataSource) {
        Preconditions.checkNotNull(dataSourceName);
        Preconditions.checkNotNull(dataSource);
        dataSourceRegistry.put(dataSourceName, dataSource);
    }

    public DataSource get(String dataSource) {
        return dataSourceRegistry.get(dataSource);
    }
}
