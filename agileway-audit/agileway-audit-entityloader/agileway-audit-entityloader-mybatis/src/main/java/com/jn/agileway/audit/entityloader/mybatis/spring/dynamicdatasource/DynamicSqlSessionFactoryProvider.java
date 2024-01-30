package com.jn.agileway.audit.entityloader.mybatis.spring.dynamicdatasource;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.sqlhelper.datasource.key.DataSourceKeySelector;
import com.jn.sqlhelper.datasource.key.MethodInvocationDataSourceKeySelector;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicDataSourceSqlSessionFactoryProvider;
import com.jn.sqlhelper.mybatis.spring.session.factory.dynamicdatasource.DynamicSqlSessionFactory;

public class DynamicSqlSessionFactoryProvider extends DynamicDataSourceSqlSessionFactoryProvider<AuditRequest> {
    public DynamicSqlSessionFactoryProvider(@Nullable MethodInvocationDataSourceKeySelector delegateSelector, @NonNull DynamicSqlSessionFactory dynamicSqlSessionFactory) {
        this(new AuditRequestDynamicDataSourceKeySelector(delegateSelector), dynamicSqlSessionFactory);
    }

    public DynamicSqlSessionFactoryProvider(DataSourceKeySelector<AuditRequest> selector, DynamicSqlSessionFactory dynamicSqlSessionFactory) {
        setSelector(selector);
        setDynamicSqlSessionFactory(dynamicSqlSessionFactory);
    }
}
