package com.jn.agileway.jdbc.datasource;

import com.jn.langx.registry.Registry;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceRegistry implements Registry<String, DataSource> {
    private ConcurrentHashMap<String, DataSource> dataSourceRegistry = new ConcurrentHashMap<String, DataSource>();

    public void register(String dataSourceName, DataSource dataSource) {
        Preconditions.checkNotNull(dataSourceName);
        Preconditions.checkNotNull(dataSource);
        dataSourceRegistry.put(dataSourceName, dataSource);
    }

    @Override
    public void register(DataSource dataSource) {
        String name = Reflects.invokePublicMethod(dataSource, "getName", null, null, true, false);
        if (Emptys.isNotEmpty(name)) {
            register(name, dataSource);
        }
    }

    public DataSource get(String dataSource) {
        return dataSourceRegistry.get(dataSource);
    }
}
