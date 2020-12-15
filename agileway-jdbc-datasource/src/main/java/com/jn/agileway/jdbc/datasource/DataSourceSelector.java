package com.jn.agileway.jdbc.datasource;

public interface DataSourceSelector {

    void setDataSourceRegistry(DataSourceRegistry registry);

    void select();
}
