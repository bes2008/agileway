package com.jn.agileway.jdbc.datasource.dbcp2;

import com.jn.agileway.jdbc.Jdbcs;
import com.jn.agileway.jdbc.datasource.DataSourceFactory;
import com.jn.agileway.jdbc.datasource.DataSourceProperties;
import com.jn.langx.annotation.Name;
import com.jn.langx.annotation.OnClasses;
import com.jn.langx.text.StringTemplates;

import javax.sql.DataSource;

import static com.jn.agileway.jdbc.datasource.DataSourceConstants.DATASOURCE_IMPLEMENT_KEY_DBCP2;

@Name(DATASOURCE_IMPLEMENT_KEY_DBCP2)
@OnClasses({
        "org.apache.commons.dbcp2.BasicDataSource",
        "org.apache.commons.dbcp2.BasicDataSourceFactory"
})
public class Dbcp2DataSourceFactory implements DataSourceFactory {
    @Override
    public DataSource get(DataSourceProperties dataSourceProperties) {
        if (Jdbcs.isImplementationKeyMatched(DATASOURCE_IMPLEMENT_KEY_DBCP2, dataSourceProperties)) {
            return Dbcp2DataSources.createDataSource(dataSourceProperties);
        }
        throw new IllegalArgumentException(StringTemplates.formatWithPlaceholder("Illegal datasource implementationKey {}, expected key is {}", dataSourceProperties.getImplementationKey(), DATASOURCE_IMPLEMENT_KEY_DBCP2));
    }
}
