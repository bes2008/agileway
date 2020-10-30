package com.jn.agileway.jdbc.datasource;

import com.jn.langx.factory.Factory;

import javax.sql.DataSource;

public interface DataSourceFactory extends Factory<DataSourceProperties, DataSource> {
    @Override
    DataSource get(DataSourceProperties dataSourceProperties);
}
