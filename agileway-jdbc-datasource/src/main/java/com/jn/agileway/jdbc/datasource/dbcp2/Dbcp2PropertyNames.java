package com.jn.agileway.jdbc.datasource.dbcp2;

public class Dbcp2PropertyNames {
    public static final String PROP_DEFAULT_AUTO_COMMIT = "defaultAutoCommit";
    public static final String PROP_DEFAULT_READ_ONLY = "defaultReadOnly";
    public static final String PROP_DEFAULT_TRANSACTION_ISOLATION = "defaultTransactionIsolation";
    public static final String PROP_DEFAULT_CATALOG = "defaultCatalog";
    public static final String PROP_DEFAULT_SCHEMA = "defaultSchema";
    public static final String PROP_CACHE_STATE = "cacheState";
    public static final String PROP_DRIVER_CLASS_NAME = "driverClassName";
    public static final String PROP_LIFO = "lifo";
    public static final String PROP_MAX_TOTAL = "maxTotal";
    public static final String PROP_MAX_IDLE = "maxIdle";
    public static final String PROP_MIN_IDLE = "minIdle";
    public static final String PROP_INITIAL_SIZE = "initialSize";
    public static final String PROP_MAX_WAIT_MILLIS = "maxWaitMillis";
    public static final String PROP_TEST_ON_CREATE = "testOnCreate";
    public static final String PROP_TEST_ON_BORROW = "testOnBorrow";
    public static final String PROP_TEST_ON_RETURN = "testOnReturn";
    public static final String PROP_TIME_BETWEEN_EVICTION_RUNS_MILLIS = "timeBetweenEvictionRunsMillis";
    public static final String PROP_NUM_TESTS_PER_EVICTION_RUN = "numTestsPerEvictionRun";
    public static final String PROP_MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";
    public static final String PROP_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = "softMinEvictableIdleTimeMillis";
    public static final String PROP_EVICTION_POLICY_CLASS_NAME = "evictionPolicyClassName";
    public static final String PROP_TEST_WHILE_IDLE = "testWhileIdle";
    public static final String PROP_PASSWORD = "password";
    public static final String PROP_URL = "url";
    public static final String PROP_USER_NAME = "username";
    public static final String PROP_VALIDATION_QUERY = "validationQuery";
    public static final String PROP_VALIDATION_QUERY_TIMEOUT = "validationQueryTimeout";
    public static final String PROP_JMX_NAME = "jmxName";
    public static final String PROP_CONNECTION_FACTORY_CLASS_NAME = "connectionFactoryClassName";

    /**
     * The property name for connectionInitSqls. The associated value String must be of the form [query;]*
     */
    public static final String PROP_CONNECTION_INIT_SQLS = "connectionInitSqls";
    public static final String PROP_ACCESS_TO_UNDERLYING_CONNECTION_ALLOWED = "accessToUnderlyingConnectionAllowed";
    public static final String PROP_REMOVE_ABANDONED_ON_BORROW = "removeAbandonedOnBorrow";
    public static final String PROP_REMOVE_ABANDONED_ON_MAINTENANCE = "removeAbandonedOnMaintenance";
    public static final String PROP_REMOVE_ABANDONED_TIMEOUT = "removeAbandonedTimeout";
    public static final String PROP_LOG_ABANDONED = "logAbandoned";
    public static final String PROP_ABANDONED_USAGE_TRACKING = "abandonedUsageTracking";
    public static final String PROP_POOL_PREPARED_STATEMENTS = "poolPreparedStatements";
    public static final String PROP_MAX_OPEN_PREPARED_STATEMENTS = "maxOpenPreparedStatements";
    public static final String PROP_CONNECTION_PROPERTIES = "connectionProperties";
    public static final String PROP_MAX_CONN_LIFETIME_MILLIS = "maxConnLifetimeMillis";
    public static final String PROP_LOG_EXPIRED_CONNECTIONS = "logExpiredConnections";
    public static final String PROP_ROLLBACK_ON_RETURN = "rollbackOnReturn";
    public static final String PROP_ENABLE_AUTO_COMMIT_ON_RETURN = "enableAutoCommitOnReturn";
    public static final String PROP_DEFAULT_QUERY_TIMEOUT = "defaultQueryTimeout";
    public static final String PROP_FAST_FAIL_VALIDATION = "fastFailValidation";

    /**
     * Value string must be of the form [STATE_CODE,]*
     */
    public static final String PROP_DISCONNECTION_SQL_CODES = "disconnectionSqlCodes";

    /*
     * Block with obsolete properties from DBCP 1.x. Warn users that these are ignored and they should use the 2.x
     * properties.
     */
    public static final String NUPROP_MAX_ACTIVE = "maxActive";
    public static final String NUPROP_REMOVE_ABANDONED = "removeAbandoned";
    public static final String NUPROP_MAXWAIT = "maxWait";

    /*
     * Block with properties expected in a DataSource This props will not be listed as ignored - we know that they may
     * appear in Resource, and not listing them as ignored.
     */
    public static final String SILENT_PROP_FACTORY = "factory";
    public static final String SILENT_PROP_SCOPE = "scope";
    public static final String SILENT_PROP_SINGLETON = "singleton";
    public static final String SILENT_PROP_AUTH = "auth";
}
