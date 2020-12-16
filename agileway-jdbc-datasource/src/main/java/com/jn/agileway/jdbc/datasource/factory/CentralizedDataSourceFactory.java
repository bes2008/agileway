package com.jn.agileway.jdbc.datasource.factory;

import com.jn.agileway.jdbc.datasource.*;
import com.jn.langx.util.Preconditions;

import java.util.Properties;

public class CentralizedDataSourceFactory implements DataSourceFactory {
    private DataSourceRegistry registry;

    public DataSourceRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(DataSourceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public NamedDataSource get(DataSourceProperties dataSourceProperties) {
        Preconditions.checkNotNull(registry);
        String name = dataSourceProperties.getName();
        Preconditions.checkNotNull(name, "the datasource name is null");

        NamedDataSource dataSource = registry.get(name);
        if (dataSource == null) {
            String implementationKey = dataSourceProperties.getImplementationKey();
            DataSourceFactory delegate = DataSourceFactoryProvider.getInstance().get(implementationKey);
            if (delegate != null) {
                dataSource = delegate.get(dataSourceProperties);
            }
            if (dataSource != null) {
                registry.register(name, dataSource);
            }
        }
        return dataSource;
    }

    @Override
    public NamedDataSource get(Properties properties) {
        Preconditions.checkNotNull(registry);
        String name = properties.getProperty(DataSourceConstants.DATASOURCE_NAME);
        Preconditions.checkNotNull(name, "the datasource name is null");

        NamedDataSource dataSource = registry.get(name);
        if (dataSource == null) {
            String implementationKey = properties.getProperty(DataSourceConstants.DATASOURCE_IMPLEMENT_KEY);
            DataSourceFactory delegate = DataSourceFactoryProvider.getInstance().get(implementationKey);
            if (delegate != null) {
                dataSource = delegate.get(properties);
            }
            if (dataSource != null) {
                registry.register(name, dataSource);
            }
        }
        return dataSource;
    }
}
