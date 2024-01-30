package com.jn.agileway.audit.core.resource.idresource;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.List;

public class EntityLoaderDispatcher<E> implements EntityLoader<E> {
    private static final Logger logger = Loggers.getLogger(EntityLoaderDispatcher.class);
    private EntityLoaderRegistry registry;

    @Override
    public String getName() {
        return Reflects.getFQNClassName(EntityLoaderDispatcher.class);
    }

    @Override
    public List<E> load(AuditRequest request, ResourceDefinition resourceDefinition, List<Serializable> ids) {
        String entityLoaderName = resourceDefinition.getEntityLoader();
        if (Emptys.isEmpty(entityLoaderName)) {
            logger.warn("Can't load resource entities, because of the idLoader is null or empty");
            return null;
        }
        Preconditions.checkNotNull(registry);
        EntityLoader loader = registry.get(entityLoaderName);
        if (loader == null) {
            logger.warn("the entity loader named {} is not exists", entityLoaderName);
            return null;
        }
        List<E> entities = null;
        try {
            entities = loader.load(request, resourceDefinition, ids);
        } catch (Throwable ex) {
            logger.error("error occur when loading auditing resources by the {} entity loader, ids: {} ", entityLoaderName, Strings.join(",", ids), ex);
        }
        return entities;
    }

    public EntityLoaderRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(EntityLoaderRegistry registry) {
        this.registry = registry;
    }
}
