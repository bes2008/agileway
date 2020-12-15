package com.jn.agileway.jdbc.datasource;

import com.jn.langx.Delegatable;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Preconditions;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DelegatingDataSource implements DataSource, Delegatable<DataSource>, Initializable {
    private DataSource delegate;

    @Override
    public DataSource getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(DataSource delegate) {
        Preconditions.checkNotNull(delegate, "'delegate' must not be null");
        this.delegate = delegate;
    }


    /**
     * Create a new DelegatingDataSource.
     */
    public DelegatingDataSource() {
    }

    /**
     * Create a new DelegatingDataSource.
     * @param targetDataSource the target DataSource
     */
    public DelegatingDataSource(DataSource targetDataSource) {
        setDelegate(targetDataSource);
    }


    public void init() throws InitializationException{
        if (getDelegate() == null) {
            throw new InitializationException("Property 'targetDataSource' is required");
        }
    }


    @Override
    public Connection getConnection() throws SQLException {
        return getDelegate().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getDelegate().getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getDelegate().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        getDelegate().setLogWriter(out);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getDelegate().getLoginTimeout();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        getDelegate().setLoginTimeout(seconds);
    }


    //---------------------------------------------------------------------
    // Implementation of JDBC 4.0's Wrapper interface
    //---------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return getDelegate().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || getDelegate().isWrapperFor(iface));
    }


    //---------------------------------------------------------------------
    // Implementation of JDBC 4.1's getParentLogger method
    //---------------------------------------------------------------------

    @Override
    public Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

}
