package com.jn.agileway.jdbc.datasource.factory.tomcatjdbc;

import com.jn.agileway.jdbc.Jdbcs;
import com.jn.agileway.jdbc.datasource.DataSourceFactory;
import com.jn.agileway.jdbc.datasource.factory.DataSourceProperties;
import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.OnClasses;
import com.jn.langx.text.StringTemplates;

import javax.sql.DataSource;

import java.util.Properties;

import static com.jn.agileway.jdbc.datasource.DataSourceConstants.DATASOURCE_IMPLEMENT_KEY_TOMCAT;

@Name(DATASOURCE_IMPLEMENT_KEY_TOMCAT)
@OnClasses({
        "org.apache.tomcat.jdbc.pool.DataSource",
        "org.apache.tomcat.jdbc.pool.DataSourceFactory"
})
public class TomcatJdbcDataSourceFactory implements DataSourceFactory {
    @Override
    public DataSource get(DataSourceProperties dataSourceProperties) {
        if (Jdbcs.isImplementationKeyMatched(DATASOURCE_IMPLEMENT_KEY_TOMCAT, dataSourceProperties)) {
            return TomcatJdbcDataSources.createDataSource(dataSourceProperties);
        }
        throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("Illegal datasource implementationKey {}, expected key is {}", dataSourceProperties.getImplementationKey(), DATASOURCE_IMPLEMENT_KEY_TOMCAT));
    }

    @Override
    public DataSource get(Properties properties) {
        return TomcatJdbcDataSources.createDataSource(properties);
    }
}
