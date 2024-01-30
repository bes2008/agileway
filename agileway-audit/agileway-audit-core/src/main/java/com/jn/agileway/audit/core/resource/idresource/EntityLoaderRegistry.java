package com.jn.agileway.audit.core.resource.idresource;

import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.Preconditions;

public class EntityLoaderRegistry extends GenericRegistry<EntityLoader> {

    @Override
    public void register(EntityLoader entityLoader) {
        register(entityLoader.getName(), entityLoader);
    }

    @Override
    public void register(String name, EntityLoader entityLoader) {
        if(entityLoader instanceof EntityLoaderDispatcher){
            return;
        }
        Preconditions.checkNotEmpty(name,"the entity loader name is null or empty");
        super.register(name, entityLoader);
    }

}
