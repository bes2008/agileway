package com.jn.agileway.jdbc.datasource.factory.hikaricp;

import com.jn.agileway.jdbc.Jdbcs;
import com.jn.agileway.jdbc.datasource.DataSourceFactory;
import com.jn.agileway.jdbc.datasource.NamedDataSource;
import com.jn.agileway.jdbc.datasource.factory.DataSourceProperties;
import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.OnClasses;
import com.jn.langx.text.StringTemplates;

import javax.sql.DataSource;

import java.util.Properties;

import static com.jn.agileway.jdbc.datasource.DataSourceConstants.DATASOURCE_IMPLEMENT_KEY_HIKARICP;

@Name(DATASOURCE_IMPLEMENT_KEY_HIKARICP)
@OnClasses({
        "com.zaxxer.hikari.HikariDataSource"
})
public class HikaricpDataSourceFactory implements DataSourceFactory {
    @Override
    public NamedDataSource get(DataSourceProperties dataSourceProperties) {
        if (Jdbcs.isImplementationKeyMatched(DATASOURCE_IMPLEMENT_KEY_HIKARICP, dataSourceProperties)) {
            return HikariDataSources.createDataSource(dataSourceProperties);
        }
        throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("Illegal datasource implementationKey {}, expected key is {}", dataSourceProperties.getImplementationKey(), DATASOURCE_IMPLEMENT_KEY_HIKARICP));
    }

    @Override
    public NamedDataSource get(Properties properties) {
        return HikariDataSources.createDataSource(properties);
    }
}
