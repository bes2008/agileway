package com.jn.agileway.jdbc.datasource;

public abstract class DataSourceSelector {
    protected DataSourceRegistry registry;

    public void setDataSourceRegistry(DataSourceRegistry registry) {
        this.registry = registry;
    }

    public abstract void select();
}
