package com.jn.agileway.audit.core.resource.idresource;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.model.Resource;
import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.agileway.audit.core.resource.parser.clazz.DefaultEntityClassResourceSupplierParser;
import com.jn.agileway.audit.core.resource.supplier.EntityResourceSupplier;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.struct.Holder;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LoadingEntityIdResourceExtractor<E, AuditedRequest, AuditedRequestContext> extends AbstractIdResourceExtractor<E, AuditedRequest, AuditedRequestContext> {
    private static final Logger logger = Loggers.getLogger(LoadingEntityIdResourceExtractor.class);
    private static final String SELECT_LIST_SEPARATOR = "separator";
    private final ConcurrentHashMap<Class, Holder<EntityResourceSupplier<E>>> entityResourceSupplierMap = new ConcurrentHashMap<Class, Holder<EntityResourceSupplier<E>>>();

    private DefaultEntityClassResourceSupplierParser parser = DefaultEntityClassResourceSupplierParser.DEFAULT_INSTANCE;

    private EntityLoader entityLoader;

    @Override
    public List<Serializable> findIds(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        return Collections.emptyList();
    }

    @Override
    public List<E> findEntities(AuditRequest<AuditedRequest, AuditedRequestContext> request, List<Serializable> ids) {
        ResourceDefinition resourceDefinition = request.getAuditEvent().getOperation().getDefinition().getResourceDefinition();
        MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();
        if (Emptys.isEmpty(ids)) {
            return null;
        }
        boolean idIsString = ids.get(0) instanceof String;

        final String selectListIdSeparator = mapAccessor.getString(SELECT_LIST_SEPARATOR, "");
        if (idIsString && Strings.isNotBlank(selectListIdSeparator)) {
            final List transformedIds = Collects.emptyArrayList();
            // 过滤出有效的id,并根据指定的分隔符进行id 分割提取
            Pipeline.of(ids).filter(new Predicate<Serializable>() {
                @Override
                public boolean test(Serializable id) {
                    return Strings.isNotBlank((String) id);
                }
            }).map(new Function<Serializable, List<String>>() {
                @Override
                public List<String> apply(Serializable id) {
                    String idListString = (String) id;
                    return Collects.newArrayList(Strings.split(idListString, selectListIdSeparator));
                }
            }).flatMap(Functions.noopFunction())
                    .addTo(transformedIds);
            ids.clear();
            ids.addAll(transformedIds);
        }
        if (Emptys.isEmpty(ids)) {
            return null;
        }
        return entityLoader.load(request, resourceDefinition, ids);
    }

    @Override
    public Resource extractResource(AuditRequest<AuditedRequest, AuditedRequestContext> request, E entity) {
        return asResource(entity);
    }

    private Resource asResource(E entity) {
        if (entity == null) {
            return null;
        }
        Class entityClass = entity.getClass();
        if (!entityResourceSupplierMap.containsKey(entityClass)) {
            Holder<EntityResourceSupplier<E>> supplier = new Holder<EntityResourceSupplier<E>>();
            try {
                supplier.set(this.parser.parse(entityClass));
            } catch (Throwable ex) {
                logger.error("Error occur when parse the ResourceSupplier for the class : {}", Reflects.getFQNClassName(entityClass));
            }
            this.entityResourceSupplierMap.putIfAbsent(entityClass, supplier);
        }
        final Holder<EntityResourceSupplier<E>> supplier = this.entityResourceSupplierMap.get(entityClass);
        if (supplier.isNull()) {
            logger.warn("Can't find a ResourceSupplier for the class: {}", Reflects.getFQNClassName(entityClass));
            return null;
        }
        return supplier.get().get(entity);
    }

    public EntityLoader getEntityLoader() {
        return entityLoader;
    }

    public void setEntityLoader(EntityLoader entityLoader) {
        this.entityLoader = entityLoader;
    }

    public DefaultEntityClassResourceSupplierParser getParser() {
        return parser;
    }

    public void setParser(DefaultEntityClassResourceSupplierParser parser) {
        this.parser = parser;
    }
}
