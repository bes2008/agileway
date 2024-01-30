package com.jn.agileway.audit.spring.boot.autoconfigure;

import com.jn.agileway.audit.core.resource.ResourceMethodInvocationExtractor;
import com.jn.agileway.audit.core.resource.idresource.EntityLoader;
import com.jn.agileway.audit.core.resource.idresource.EntityLoaderDispatcher;
import com.jn.agileway.audit.core.resource.idresource.EntityLoaderRegistry;
import com.jn.agileway.audit.core.resource.idresource.LoadingEntityIdResourceExtractor;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ResourceExtractAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EntityLoaderRegistry.class)
    public EntityLoaderRegistry entityLoaderRegistry() {
        return new EntityLoaderRegistry();
    }

    @Bean
    @ConditionalOnMissingBean(EntityLoaderDispatcher.class)
    @Autowired
    public EntityLoaderDispatcher entityLoaderDispatcher(
            final EntityLoaderRegistry registry,
            @Autowired(required = false) ObjectProvider<List<EntityLoader>> entityLoadersProvider) {

        EntityLoaderDispatcher dispatcher = new EntityLoaderDispatcher();
        Collects.forEach(entityLoadersProvider.getIfAvailable(), new Consumer<EntityLoader>() {
            @Override
            public void accept(EntityLoader entityLoader) {
                registry.register(entityLoader);
            }
        });
        dispatcher.setRegistry(registry);
        return dispatcher;
    }

    @Bean
    @ConditionalOnMissingBean(LoadingEntityIdResourceExtractor.class)
    @Autowired
    public LoadingEntityIdResourceExtractor loadingEntityIdResourceExtractor(EntityLoaderDispatcher dispatcher) {
        LoadingEntityIdResourceExtractor extractor = new LoadingEntityIdResourceExtractor();
        extractor.setEntityLoader(dispatcher);
        return extractor;
    }

    @Bean
    @ConditionalOnMissingBean
    @Autowired
    public ResourceMethodInvocationExtractor resourceMethodInvocationExtractor(LoadingEntityIdResourceExtractor idResourceExtractor) {
        ResourceMethodInvocationExtractor resourceMethodInvocationExtractor = new ResourceMethodInvocationExtractor();
        resourceMethodInvocationExtractor.setIdResourceExtractor(idResourceExtractor);
        return resourceMethodInvocationExtractor;
    }

}
