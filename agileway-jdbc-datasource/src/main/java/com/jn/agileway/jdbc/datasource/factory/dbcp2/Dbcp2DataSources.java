package com.jn.agileway.jdbc.datasource.factory.dbcp2;

import com.jn.agileway.jdbc.datasource.DataSourceConstants;
import com.jn.agileway.jdbc.datasource.DelegatingNamedDataSource;
import com.jn.agileway.jdbc.datasource.NamedDataSource;
import com.jn.agileway.jdbc.datasource.factory.DataSourceProperties;
import com.jn.langx.util.Maths;
import com.jn.langx.util.Throwables;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

import static com.jn.agileway.jdbc.datasource.factory.dbcp2.Dbcp2PropertyNames.*;

/**
 * http://commons.apache.org/proper/commons-dbcp/configuration.html
 */
public class Dbcp2DataSources {
    private Dbcp2DataSources() {
    }

    public static NamedDataSource createDataSource(DataSourceProperties properties) {
        Properties props = properties.getDriverProps();
        if (props == null) {
            props = new Properties();
        }

        props.setProperty(DataSourceConstants.DATASOURCE_NAME, properties.getName());

        String username = properties.getUsername();
        if (username != null) {
            props.setProperty(PROP_USER_NAME, username);
        }

        String password = properties.getPassword();
        if (password != null) {
            props.setProperty(PROP_PASSWORD, password);
        }

        String url = properties.getUrl();
        if (url != null) {
            props.setProperty(PROP_URL, url);
        }

        String driverClassName = properties.getDriverClassName();
        if (driverClassName != null) {
            props.setProperty(PROP_DRIVER_CLASS_NAME, driverClassName);
        }

        props.setProperty(PROP_DEFAULT_AUTO_COMMIT, "" + properties.isAutoCommit());
        props.setProperty(PROP_DEFAULT_READ_ONLY, "" + properties.isReadOnly());
        props.setProperty(PROP_DEFAULT_TRANSACTION_ISOLATION, properties.getTransactionIsolationName());

        String catalog = properties.getCatalog();
        if (catalog != null) {
            props.setProperty(PROP_DEFAULT_CATALOG, catalog);
        }

        String schema = properties.getSchema();
        if (schema != null) {
            props.setProperty(PROP_DEFAULT_SCHEMA, schema);
        }

        props.setProperty(PROP_INITIAL_SIZE, "" + properties.getInitialSize());
        props.setProperty(PROP_MIN_IDLE, "" + properties.getMinIdle());
        props.setProperty(PROP_MAX_IDLE, "" + Maths.max(8, properties.getMinIdle()));
        props.setProperty(PROP_MAX_TOTAL, "" + Maths.max(8, properties.getMaxPoolSize()));


        String validationQuery = properties.getValidationQuery();
        if (validationQuery != null) {
            props.setProperty(PROP_VALIDATION_QUERY, validationQuery);
        }

        props.setProperty(PROP_MAX_CONN_LIFETIME_MILLIS, "" + properties.getMaxLifetimeInMills());


        try {
            return createDataSource(props);
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    public static NamedDataSource createDataSource(Properties props ){
        try {
            String name = props.getProperty(DataSourceConstants.DATASOURCE_NAME);
            DataSource dataSource =  BasicDataSourceFactory.createDataSource(props);
            return DelegatingNamedDataSource.of(dataSource, name);
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }
}
