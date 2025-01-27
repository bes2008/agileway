package com.jn.agileway.audit.core;

import com.jn.agileway.audit.core.model.AuditEvent;
import com.jn.agileway.dmmq.core.*;
import com.jn.agileway.dmmq.core.allocator.AbstractMultipleCandidateTopicAllocator;
import com.jn.agileway.dmmq.core.allocator.DefaultTopicAllocator;
import com.jn.agileway.dmmq.core.event.TopicEvent;
import com.jn.langx.event.EventPublisher;
import com.jn.langx.event.local.SimpleEventPublisher;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.concurrent.CommonThreadFactory;
import com.jn.langx.util.concurrent.executor.ExclusiveExecutor;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function2;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Supplier;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import com.lmax.disruptor.WaitStrategy;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleAuditorFactory<Settings extends AuditSettings> implements AuditorFactory<Settings> {
    private static final Logger logger = Loggers.getLogger(SimpleAuditorFactory.class);

    protected WaitStrategy getDefaultWaitStrategy(Settings settings) {
        String name = settings.getConsumerWaitStrategy();
        BuiltinWaitStrategyFactory factory = new BuiltinWaitStrategyFactory();
        if (Strings.isEmpty(name)) {
            name = "blocking";
        }
        if (factory.isBuiltin(name)) {
            return factory.get(name);
        }
        try {
            Class clazz = ClassLoaders.loadClass(name, Thread.currentThread().getContextClassLoader());
            return Reflects.<WaitStrategy>newInstance(clazz);
        } catch (Throwable ex) {
            logger.warn("error when load a class or create instance: {}", name);
        }
        return factory.get("blocking");
    }

    protected MessageTranslator newMessageTranslator(String className) {
        if (Strings.isEmpty(className)) {
            className = Reflects.getFQNClassName(DefaultMessageTranslator.class);
        }
        try {
            Class clazz = ClassLoaders.loadClass(className,  Thread.currentThread().getContextClassLoader());
            return Reflects.<MessageTranslator>newInstance(clazz);
        } catch (Throwable ex) {
            logger.warn("error when load a class or create instance: {}", className);
            return Reflects.newInstance(DefaultMessageTranslator.class);
        }
    }


    protected TopicAllocator getTopicAllocator(Settings settings) {
        String className = settings.getTopicAllocator();
        if (Strings.isEmpty(className)) {
            className = Reflects.getFQNClassName(DefaultTopicAllocator.class);
        }
        try {
            Class clazz = ClassLoaders.loadClass(className,  Thread.currentThread().getContextClassLoader());
            TopicAllocator ac = Reflects.<TopicAllocator>newInstance(clazz);
            ac = Preconditions.checkNotNull(ac, new Supplier<Object[], String>() {
                @Override
                public String get(Object[] args) {
                    return StringTemplates.formatWithPlaceholder("Can't create an instance for calss: {}", args[0]);
                }
            }, className);
            if (ac instanceof AbstractMultipleCandidateTopicAllocator) {
                List<String> topics = settings.getTopics();
                if (Emptys.isNotEmpty(topics)) {
                    ((AbstractMultipleCandidateTopicAllocator) ac).setCandidateTopics(topics);
                }
            }
            return ac;
        } catch (Throwable ex) {
            logger.warn("error when load a class or create instance: {}", className);
            return Reflects.newInstance(DefaultTopicAllocator.class);
        }
    }

    protected Executor getDefaultExecutor(Settings settings) {
        if (settings.getExecutor() != null) {
            return settings.getExecutor();
        }
        if (settings.isAsyncMode() || Collects.anyMatch(settings.getTopicConfigs(), new Predicate<MessageTopicConfiguration>() {
            @Override
            public boolean test(MessageTopicConfiguration configuration) {
                return configuration.getExecutor() == null;
            }
        })) {
            return new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new CommonThreadFactory("Auditor", true));
        }
        return null;
    }

    protected EventPublisher getEventPublisher(Settings settings) {
        return new SimpleEventPublisher();
    }

    protected void initBeforeFilterChain(AuditRequestFilterChain chain, Settings settings) {

    }

    protected void initAfterFilterChain(AuditRequestFilterChain chain, Settings settings) {

    }

    protected MessageTopicDispatcher getMessageTopicDispatcher(Settings settings) {
        return new MessageTopicDispatcher();
    }

    protected Producer<AuditEvent> getProducer(Settings settings) {
        return new SimpleProducer<AuditEvent>();
    }

    protected Function2 getAuditRequestFactory() {
        return new Function2() {
            @Override
            public Object apply(Object request, Object ctx) {
                AuditRequest wrappedRequest = new AuditRequest();
                wrappedRequest.setRequest(request);
                wrappedRequest.setRequestContext(ctx);
                return wrappedRequest;
            }
        };
    }

    public List<MessageTopicConfiguration> getTopicConfigurations(Settings settings) {
        return settings.getTopicConfigs();
    }

    @Override
    public Auditor get(Settings settings) {
        Auditor auditor = new Auditor();
        auditor.setAuditRequestFactory(getAuditRequestFactory());
        auditor.setAsyncAudit(settings.isAsyncMode());

        // event publisher
        EventPublisher eventPublisher = getEventPublisher(settings);

        // executor
        final Executor defaultExecutor = getDefaultExecutor(settings);

        final Executor consumerExecutor = new ExclusiveExecutor(new CommonThreadFactory("Consumer", false));

        // dispatcher
        final MessageTopicDispatcher dispatcher = getMessageTopicDispatcher(settings);
        dispatcher.setTopicEventPublisher(eventPublisher);

        // topics
        final WaitStrategy defaultWaitStrategy = getDefaultWaitStrategy(settings);
        List<MessageTopicConfiguration> topicConfigs = getTopicConfigurations(settings);
        Collects.forEach(topicConfigs, new Consumer<MessageTopicConfiguration>() {
            @Override
            public void accept(MessageTopicConfiguration topicConfig) {
                MessageTopic topic = new MessageTopic();
                topic.setName(topicConfig.getName());
                if (topicConfig.getRingBufferSize() < 2) {
                    topicConfig.setRingBufferSize(8096);
                }
                if (topicConfig.getExecutor() == null) {
                    topicConfig.setExecutor(consumerExecutor);
                }
                if (topicConfig.getWaitStrategy() == null) {
                    topicConfig.setWaitStrategy(defaultWaitStrategy);
                }
                MessageTranslator translator = topicConfig.getMessageTranslator();
                if (translator == null) {
                    translator = new DefaultMessageTranslator();
                }
                topicConfig.setMessageTranslator(translator);
                topic.setConfiguration(topicConfig);
                topic.init();
                dispatcher.registerTopic(topic);

            }
        });


        // topic allocator
        TopicAllocator topicAllocator = getTopicAllocator(settings);
        eventPublisher.addEventListener(TopicEvent.DOMAIN, topicAllocator);

        // producer
        Producer<AuditEvent> producer = getProducer(settings);
        producer.setMessageTopicDispatcher(dispatcher);
        producer.setTopicAllocator(topicAllocator);
        auditor.setProducer(producer);

        // executor
        if (auditor.getExecutor() == null) {
            auditor.setExecutor(defaultExecutor);
        }

        // before filter chain
        AuditRequestFilterChain beforeFilterChain = new AuditRequestFilterChain();
        initBeforeFilterChain(beforeFilterChain, settings);
        auditor.setBeforeExtractFilterChain(beforeFilterChain);

        // after filter chain
        AuditRequestFilterChain afterFilterChain = new AuditRequestFilterChain();
        initAfterFilterChain(afterFilterChain, settings);
        auditor.setAfterExtractFilterChain(afterFilterChain);
        return auditor;
    }
}
