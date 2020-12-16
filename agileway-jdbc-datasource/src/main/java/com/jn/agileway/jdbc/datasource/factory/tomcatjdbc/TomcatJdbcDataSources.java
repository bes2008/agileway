package com.jn.agileway.jdbc.datasource.factory.tomcatjdbc;

import com.jn.agileway.jdbc.datasource.DataSourceConstants;
import com.jn.agileway.jdbc.datasource.DelegatingNamedDataSource;
import com.jn.agileway.jdbc.datasource.NamedDataSource;
import com.jn.agileway.jdbc.datasource.factory.DataSourceProperties;
import com.jn.langx.util.Maths;
import com.jn.langx.util.Throwables;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

import static com.jn.agileway.jdbc.datasource.factory.tomcatjdbc.TomcatJdbcDataSourcePropertyNames.*;

public class TomcatJdbcDataSources {
    private TomcatJdbcDataSources() {
    }

    public static NamedDataSource createDataSource(final DataSourceProperties properties) {
        DataSourceFactory dsf = new DataSourceFactory();
        Properties props = properties.getDriverProps();
        if (props == null) {
            props = new Properties();
        }

        String username = properties.getUsername();
        if (username != null) {
            props.setProperty(PROP_USERNAME, username);
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
            props.setProperty(PROP_DRIVERCLASSNAME, driverClassName);
        }

        props.setProperty(PROP_DEFAULTAUTOCOMMIT, "" + properties.isAutoCommit());
        props.setProperty(PROP_DEFAULTREADONLY, "" + properties.isReadOnly());
        props.setProperty(PROP_DEFAULTTRANSACTIONISOLATION, properties.getTransactionIsolationName());

        String catalog = properties.getCatalog();
        if (catalog != null) {
            props.setProperty(PROP_DEFAULTCATALOG, catalog);
        }

        props.setProperty(PROP_INITIALSIZE, "" + properties.getInitialSize());
        props.setProperty(PROP_MINIDLE, "" + properties.getMinIdle());
        props.setProperty(PROP_MAXIDLE, "" + Maths.max(8, properties.getMinIdle()));
        props.setProperty(PROP_MAXACTIVE, "" + Maths.max(8, properties.getMaxPoolSize()));


        String validationQuery = properties.getValidationQuery();
        if (validationQuery != null) {
            props.setProperty(PROP_VALIDATIONQUERY, validationQuery);
        }
        try {
            DataSource dataSource = dsf.createDataSource(props);
            String name = properties.getName();
            return DelegatingNamedDataSource.of(dataSource, name);
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    public static NamedDataSource createDataSource(Properties properties) {
        try {
            DataSourceFactory dsf = new DataSourceFactory();
            DataSource dataSource = dsf.createDataSource(properties);
            String name = properties.getProperty(DataSourceConstants.DATASOURCE_NAME);
            return DelegatingNamedDataSource.of(dataSource, name);
        } catch (Exception ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

}
