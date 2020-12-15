package com.jn.agileway.jdbc.datasource.factory.druid;

import com.jn.agileway.jdbc.Jdbcs;
import com.jn.agileway.jdbc.datasource.DataSourceFactory;
import com.jn.agileway.jdbc.datasource.DataSourceProperties;
import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.OnClasses;
import com.jn.langx.text.StringTemplates;

import javax.sql.DataSource;

import java.util.Properties;

import static com.jn.agileway.jdbc.datasource.DataSourceConstants.DATASOURCE_IMPLEMENT_KEY_DRUID;

@Name(DATASOURCE_IMPLEMENT_KEY_DRUID)
@OnClasses({
        "com.alibaba.druid.pool.DruidDataSource",
        "com.alibaba.druid.pool.DruidDataSourceFactory",
})
public class AlibabaDruidDataSourceFactory implements DataSourceFactory {
    @Override
    public DataSource get(DataSourceProperties dataSourceProperties) {
        if (Jdbcs.isImplementationKeyMatched(DATASOURCE_IMPLEMENT_KEY_DRUID, dataSourceProperties)) {
            return AlibabaDruidDataSources.createDataSource(dataSourceProperties);
        }
        throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("Illegal datasource implementationKey {}, expected key is {}", dataSourceProperties.getImplementationKey(), DATASOURCE_IMPLEMENT_KEY_DRUID));
    }

    @Override
    public DataSource get(Properties properties) {
        return AlibabaDruidDataSources.createDataSource(properties);
    }
}
