package com.jn.agileway.jdbc.datasource.factory.c3p0;

import com.jn.agileway.jdbc.Jdbcs;
import com.jn.agileway.jdbc.datasource.DataSourceFactory;
import com.jn.agileway.jdbc.datasource.NamedDataSource;
import com.jn.agileway.jdbc.datasource.factory.DataSourceProperties;
import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.OnClasses;
import com.jn.langx.text.StringTemplates;

import javax.sql.DataSource;

import java.util.Properties;

import static com.jn.agileway.jdbc.datasource.DataSourceConstants.DATASOURCE_IMPLEMENT_KEY_C3P0;

@Name(DATASOURCE_IMPLEMENT_KEY_C3P0)
@OnClasses({
        "com.mchange.v2.c3p0.WrapperConnectionPoolDataSource",
})
public class C3p0DataSourceFactory implements DataSourceFactory {
    @Override
    public NamedDataSource get(DataSourceProperties dataSourceProperties) {
        if (Jdbcs.isImplementationKeyMatched(DATASOURCE_IMPLEMENT_KEY_C3P0, dataSourceProperties)) {
            return C3p0DataSources.createDataSource(dataSourceProperties);
        }
        throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("Illegal datasource implementationKey {}, expected key is {}", dataSourceProperties.getImplementationKey(), DATASOURCE_IMPLEMENT_KEY_C3P0));
    }

    @Override
    public NamedDataSource get(Properties properties) {
        return C3p0DataSources.createDataSource(properties);
    }
}
