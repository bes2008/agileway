package com.jn.agileway.audit.entityloader.mybatis;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.agileway.audit.core.resource.idresource.AbstractEntityLoader;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.logging.Loggers;
import com.jn.sqlhelper.mybatis.session.factory.SimpleSqlSessionFactoryProvider;
import com.jn.sqlhelper.mybatis.session.factory.SqlSessionFactoryProvider;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.List;

public class MybatisEntityLoader extends AbstractEntityLoader<Object> {
    private static final Logger logger = Loggers.getLogger(MybatisEntityLoader.class);
    private static final String STATEMENT_ID = "statementId";
    private static final String SELECT_TYPE = "selectType";
    private String name = "mybatis";
    private SqlSessionFactoryProvider<AuditRequest> sessionFactoryProvider;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (Emptys.isNotEmpty(name)) {
            this.name = name;
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected int getBatchSize(MapAccessor mapAccessor, List<Serializable> ids) {
        String selectType = mapAccessor.getString(SELECT_TYPE, "selectList");
        if ("selectOne".equals(selectType)) {
            return 1;
        }
        if ("selectList".equals(selectType)) {
            return mapAccessor.getInteger("batchSize", 100);
        }
        return super.getBatchSize(mapAccessor, ids);
    }

    @Override
    protected List<Object> loadInternal(AuditRequest request, ResourceDefinition resourceDefinition, List<Serializable> partitionIds) {
        if (Emptys.isEmpty(partitionIds)) {
            return null;
        }
        MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        final String statementId = mapAccessor.getString(STATEMENT_ID);
        String selectType = mapAccessor.getString(SELECT_TYPE, "selectList");
        Preconditions.checkNotEmpty(statementId, "the {} is undefined in the resource definition", STATEMENT_ID);
        if ("selectOne".equals(selectType)) {
            SqlSessionFactory sessionFactory = getSessionFactory(request);
            Preconditions.checkNotNull(sessionFactory, "the session factory is null");
            SqlSession session = sessionFactory.openSession();
            try {
                Object object = session.selectOne(statementId, partitionIds.get(0));
                return Collects.newArrayList(object);
            } finally {
                session.close();
            }
        } else if ("selectList".equals(selectType)) {
            SqlSessionFactory sessionFactory = getSessionFactory(request);
            Preconditions.checkNotNull(sessionFactory, "the session factory is null");
            final SqlSession session = sessionFactory.openSession();
            try {
                List partition = session.selectList(statementId, partitionIds);
                return partition;
            } finally {
                session.close();
            }
        } else {
            logger.warn("the selectType is unsupported : {}", selectType);
        }
        return null;
    }

    public SqlSessionFactory getSessionFactory(AuditRequest request) {
        return this.sessionFactoryProvider.get(request);
    }

    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        if (sessionFactory != null) {
            setSessionFactoryProvider(new SimpleSqlSessionFactoryProvider<AuditRequest>(sessionFactory));
        }
    }

    public void setSessionFactoryProvider(SqlSessionFactoryProvider<AuditRequest> provider) {
        if (provider != null) {
            this.sessionFactoryProvider = provider;
        }
    }
}
