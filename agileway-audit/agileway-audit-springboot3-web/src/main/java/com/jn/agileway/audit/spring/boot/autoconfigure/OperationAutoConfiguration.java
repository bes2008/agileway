package com.jn.agileway.audit.spring.boot.autoconfigure;

import com.jn.agileway.audit.core.model.OperationDefinition;
import com.jn.agileway.audit.core.operation.OperationDefinitionParserRegistry;
import com.jn.agileway.audit.core.operation.OperationIdGenerator;
import com.jn.agileway.audit.core.operation.method.OperationAnnotationParser;
import com.jn.agileway.audit.core.operation.method.OperationMethodAnnotationDefinitionParser;
import com.jn.agileway.audit.core.operation.repository.*;
import com.jn.agileway.audit.servlet.ServletUrlOperationIdGenerator;
import com.jn.agileway.audit.spring.webmvc.RequestMappingOperationDefinitionIdGenerator;
import com.jn.langx.cache.Cache;
import com.jn.langx.cache.CacheBuilder;
import com.jn.langx.configuration.MultipleLevelConfigurationRepository;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.concurrent.CommonThreadFactory;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.timing.timer.Timer;
import com.jn.langx.util.timing.timer.WheelTimers;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Configuration
@EnableConfigurationProperties(OperationDefinitionProperties.class)
public class OperationAutoConfiguration {

    @Bean(name = "Operation-Timer")
    @ConditionalOnMissingBean(name = {"Operation-Timer"})
    public Timer timer() {
        return WheelTimers.newHashedWheelTimer(new CommonThreadFactory());
    }

    @Bean("operationDefinitionCache")
    @ConditionalOnMissingBean(name = {"operationDefinitionCache"})
    public Cache<String, OperationDefinition> operationDefinitionCache(@Autowired Timer timer) {
        return CacheBuilder.<String, OperationDefinition>newBuilder()
                .initialCapacity(500)
                .capacityHeightWater(0.95f)
                .timer(timer)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(value = {OperationDefinitionLoader.class})
    public OperationDefinitionLoader yamlOperationDefinitionLoader(OperationDefinitionProperties definitionProperties) {
        YamlOperationDefinitionLoader loader = new YamlOperationDefinitionLoader();
        loader.setDefinitionFilePath(definitionProperties.getLocation());
        return loader;
    }


    @Bean("yamlOperationDefinitionRepository")
    @ConditionalOnMissingBean(value = {OperationDefinitionRepository.class})
    @ConditionalOnBean(value = {OperationDefinitionLoader.class})
    public OperationDefinitionRepository yamlOperationDefinitionRepository(
            OperationDefinitionProperties definitionProperties,
            @Autowired @Qualifier("operationDefinitionCache")
                    Cache<String, OperationDefinition> operationDefinitionCache,
            @Autowired
                    Timer timer,
            @Autowired OperationDefinitionLoader yamlLoader
    ) {
        OperationDefinitionRepository repository = new OperationDefinitionRepository();
        repository.setName("Operation-Definition-Repository-YAML");
        repository.setCache(operationDefinitionCache);
        repository.setTimer(timer);
        repository.setConfigurationLoader(yamlLoader);
        repository.setReloadIntervalInSeconds(definitionProperties.getReloadIntervalInSeconds());
        return repository;
    }

    @Bean("multipleLevelOperationDefinitionRepository")
    @ConditionalOnMissingBean(name = "multipleLevelOperationDefinitionRepository")
    public MultipleLevelConfigurationRepository multipleLevelOperationDefinitionRepository(
            @Autowired @Qualifier("yamlOperationDefinitionRepository")
                    OperationDefinitionRepository yamlRepository,
            @Autowired @Qualifier("Operation-Timer")
                    Timer timer
    ) {
        MultipleLevelConfigurationRepository repository = new MultipleLevelConfigurationRepository();
        repository.addRepository(yamlRepository);
        repository.setName("Operation-Definition-Multiple-Repository");
        repository.setCache(CacheBuilder.<String, OperationDefinition>newBuilder()
                .initialCapacity(500)
                .capacityHeightWater(0.95f)
                .timer(timer)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build());
        repository.startup();
        return repository;
    }

    @Bean
    @Autowired
    public DefaultOperationRepositoryParser defaultOperationRepositoryParser(
            @Qualifier("multipleLevelOperationDefinitionRepository")
                    MultipleLevelConfigurationRepository repository) {
        DefaultOperationRepositoryParser parser = new DefaultOperationRepositoryParser();
        parser.setRepository(repository);
        return parser;
    }

    @Bean
    @ConditionalOnMissingBean(value = {OperationAnnotationParser.class})
    @Order(0)
    public OperationAnnotationParser operationAnnotationParser() {
        return new OperationAnnotationParser();
    }

    @Autowired
    @ConditionalOnMissingBean(value = {OperationDefinitionParserRegistry.class})
    @Bean("operationDefinitionParserRegistry")
    public OperationDefinitionParserRegistry operationDefinitionParserRegistry(
            ObjectProvider<List<OperationMethodAnnotationDefinitionParser>> methodAnnotationDefinitionParsersProvider,
            @Autowired(required = false) ObjectProvider<List<OperationRepositoryParser>> repositoryDefinitionParsersProvider
    ) {
        final OperationDefinitionParserRegistry registry = new OperationDefinitionParserRegistry();
        Collects.forEach(methodAnnotationDefinitionParsersProvider.getIfAvailable(), new Consumer<OperationMethodAnnotationDefinitionParser>() {
            @Override
            public void accept(OperationMethodAnnotationDefinitionParser operationMethodAnnotationDefinitionParser) {
                registry.registry(operationMethodAnnotationDefinitionParser);
            }
        });
        Collects.forEach(repositoryDefinitionParsersProvider.getIfAvailable(), new Consumer<OperationRepositoryParser>() {
            @Override
            public void accept(OperationRepositoryParser operationRepositoryParser) {
                registry.registry(operationRepositoryParser);
            }
        });
        return registry;
    }

    @Order(3)
    @Bean
    @ConditionalOnWebApplication
    public OperationIdGenerator<HttpServletRequest, MethodInvocation> urlOperationDefinitionIdGenerator() {
        return new ServletUrlOperationIdGenerator();
    }


    @Order(2)
    @Bean
    @ConditionalOnWebApplication
    public OperationIdGenerator<HttpServletRequest, MethodInvocation> requestMappingOperationDefinitionIdGenerator() {
        return new RequestMappingOperationDefinitionIdGenerator();
    }
}
