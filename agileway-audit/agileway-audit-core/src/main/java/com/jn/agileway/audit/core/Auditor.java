package com.jn.agileway.audit.core;

import com.jn.agileway.audit.core.model.AuditEvent;
import com.jn.agileway.dmmq.core.MessageTopicDispatcher;
import com.jn.agileway.dmmq.core.MessageTopicDispatcherAware;
import com.jn.agileway.dmmq.core.Producer;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.Destroyable;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.concurrent.promise.Promises;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Function2;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.struct.ThreadLocalHolder;
import org.slf4j.Logger;

import java.util.Stack;
import java.util.concurrent.Executor;

public class Auditor<AuditedRequest, AuditedRequestContext> implements Initializable, Destroyable, MessageTopicDispatcherAware {
    private static Logger logger = Loggers.getLogger(Auditor.class);
    private static ThreadLocalHolder<Stack<AuditRequest>> auditRequestHolder = new ThreadLocalHolder<Stack<AuditRequest>>();
    private static ThreadLocalHolder<Promise> asyncTaskHolder = new ThreadLocalHolder<Promise>();

    /**
     * 用于在audit event 提取之前进行过滤
     */
    @NonNull
    private AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> beforeExtractFilterChain;

    /**
     * 用于在audit event 提取之后进行过滤
     */
    @NonNull
    private AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> afterExtractFilterChain;

    /**
     * audit event 提取器
     */
    @NonNull
    private AuditEventExtractor<AuditedRequest, AuditedRequestContext> auditEventExtractor;

    /**
     * 在finishAudit方法执行时，通过producer来把audit event 发布到响应的topic里
     */
    @NonNull
    private Producer<AuditEvent> producer;
    /**
     * 用于async执行的audit
     */
    @Nullable
    private Executor executor;
    /**
     * 异步执行开关
     */
    private boolean asyncAudit = true;

    /**
     * 用于创建自定义的AuditRequest 对象
     */
    private Function2<AuditedRequest, AuditedRequestContext, AuditRequest<AuditedRequest, AuditedRequestContext>> auditRequestFactory;

    @Override
    public void destroy() {
        executor = null;
    }

    @Override
    public void init() throws InitializationException {

    }

    /**
     * 判断request是否要进行异步审计
     *
     * @param request
     * @return
     */
    public boolean isAsyncAudit(AuditedRequest request) {
        if (asyncAudit && executor != null && request != null) {
            try {
                Class httpServletRequest = ClassLoaders.loadClass("javax.servlet.http.HttpServletRequest", Thread.currentThread().getContextClassLoader());
                if (httpServletRequest != null) {
                    if (Reflects.isSubClassOrEquals(httpServletRequest, request.getClass())) {
                        return false;
                    }
                }
                return true;
            } catch (ClassNotFoundException ex) {
                return true;
            }
        }
        return false;
    }

    public void setAsyncAudit(boolean asyncAudit) {
        this.asyncAudit = asyncAudit;
    }


    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public Executor getExecutor() {
        return executor;
    }


    public void setProducer(Producer<AuditEvent> producer) {
        this.producer = producer;
    }


    public Producer<AuditEvent> getProducer() {
        return producer;
    }

    /**
     * 该方法应用于 非AOP情况下， 即不需要在某个方法前、后执行的情况。
     *
     * @param request the request
     * @param ctx     the request context
     */
    public void doAudit(AuditedRequest request, AuditedRequestContext ctx) {
        if (isAsyncAudit(request)) {
            finishAsyncAudit(startAsyncAudit(request, ctx));
        } else {
            finishSyncAudit(startSyncAudit(request, ctx));
        }
    }

    /**
     * 开始审计，该方法要在请求执行之前执行，内部会调用 startAsyncAudit或者 startSyncAudit
     *
     * @param request
     * @param ctx
     * @return
     */
    public AuditRequest<AuditedRequest, AuditedRequestContext> startAudit(final AuditedRequest request, final AuditedRequestContext ctx) {
        if (isAsyncAudit(request)) {
            return startAsyncAudit(request, ctx);
        } else {
            return startSyncAudit(request, ctx);
        }
    }

    /**
     * 开始审计，该方法要在请求执行之前执行
     *
     * @param request
     * @param ctx
     * @return
     */
    public AuditRequest<AuditedRequest, AuditedRequestContext> startAsyncAudit(final AuditedRequest request, final AuditedRequestContext ctx) {
        final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = createAuditRequest(request, ctx);
        cacheRequest(wrappedRequest);
        Loggers.debug(logger, "start async audit {}", wrappedRequest);
        wrappedRequest.setStartTime(System.currentTimeMillis());
        final ClassLoader mThreadClassLoader = Thread.currentThread().getContextClassLoader();
        asyncTaskHolder.set(Promises.runAsync(this.executor, new Runnable() {
            @Override
            public void run() {
                ClassLoaders.doAction(mThreadClassLoader, new Function<Object, Object>() {
                    @Override
                    public Object apply(Object input) {
                        startAuditInternal(wrappedRequest);
                        return null;
                    }
                }, null);
            }
        }));
        return wrappedRequest;
    }

    /**
     * 开始审计，该方法要在请求执行之前执行
     *
     * @param request
     * @param ctx
     * @return
     */
    public AuditRequest<AuditedRequest, AuditedRequestContext> startSyncAudit(final AuditedRequest request, final AuditedRequestContext ctx) {
        final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = createAuditRequest(request, ctx);
        Loggers.debug(logger, "start sync audit {}", wrappedRequest);
        wrappedRequest.setStartTime(System.currentTimeMillis());
        startAuditInternal(wrappedRequest);
        cacheRequest(wrappedRequest);
        return wrappedRequest;
    }


    private AuditRequest<AuditedRequest, AuditedRequestContext> startAuditInternal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        boolean auditIt = true;
        // may be too long time
        if (Emptys.isNotEmpty(beforeExtractFilterChain)) {
            auditIt = beforeExtractFilterChain.accept(wrappedRequest);
        }
        if (!auditIt) {
            wrappedRequest.setAuditIt(auditIt);
            return wrappedRequest;
        }
        AuditEvent auditEvent = auditEventExtractor.get(wrappedRequest);
        wrappedRequest.setAuditEvent(auditEvent);
        if (Emptys.isNotEmpty(afterExtractFilterChain)) {
            auditIt = afterExtractFilterChain.accept(wrappedRequest);
        }
        if (auditIt) {
            wrappedRequest.setAuditEvent(auditEvent);
            wrappedRequest.setAuditIt(auditIt);
        }
        return auditIt ? wrappedRequest : null;
    }


    /**
     * 完成审计，该方法要在请求执行之后执行，该方法内部会调用finishSyncAudit或者finishAsyncAudit
     *
     * @param wrappedRequest
     * @return
     */
    public void finishAudit(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        if (isAsyncAudit(wrappedRequest.getRequest())) {
            finishAsyncAudit(wrappedRequest);
        } else {
            finishSyncAudit(wrappedRequest);
        }
    }

    /**
     * 完成审计，该方法要在请求执行之后执行
     *
     * @param wrappedRequest
     * @return
     */
    public void finishSyncAudit(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        try {
            wrappedRequest.setEndTime(System.currentTimeMillis());
            finishAuditInternal(wrappedRequest);
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            Loggers.debug(logger, "finish sync audit {}", wrappedRequest);
            removeRequest();
        }
    }

    /**
     * 完成审计，该方法要在请求执行之后执行
     *
     * @param wrappedRequest
     * @return
     */
    public void finishAsyncAudit(final AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        wrappedRequest.setEndTime(System.currentTimeMillis());
        Loggers.debug(logger, "finish sync audit {}", wrappedRequest);
        Promise future = asyncTaskHolder.get();
        if (future != null) {
            future.then(new Runnable() {
                @Override
                public void run() {
                    finishAuditInternal(wrappedRequest);
                }
            });
        }
        asyncTaskHolder.reset();
        removeRequest();
    }

    private void finishAuditInternal(AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest) {
        if (wrappedRequest != null) {
            AuditEvent event = wrappedRequest.getAuditEvent();
            if (wrappedRequest.isAuditIt() && event != null) {
                if (event.getService() == null) {
                    logger.warn("Can't find an valid service for request: {}", wrappedRequest);
                    return;
                }
                if (event.getPrincipal() == null) {
                    logger.warn("Can't find an valid principal for request: {}", wrappedRequest);
                    return;
                }
                if (event.getOperation() == null) {
                    logger.warn("Can't find an valid operation for request: {}", wrappedRequest);
                    return;
                }
                event.setStartTime(wrappedRequest.getStartTime());
                event.setEndTime(wrappedRequest.getEndTime());
                event.setDuration(wrappedRequest.getEndTime() - wrappedRequest.getStartTime());
                if (wrappedRequest.getResult() != null) {
                    event.getOperation().setResult(wrappedRequest.getResult());
                }
                producer.publish(wrappedRequest.getTopic(), event);
            }
        }
    }

    @Override
    public MessageTopicDispatcher getMessageTopicDispatcher() {
        return producer.getMessageTopicDispatcher();
    }

    @Override
    public void setMessageTopicDispatcher(MessageTopicDispatcher dispatcher) {
        producer.setMessageTopicDispatcher(dispatcher);
    }

    public AuditRequest<AuditedRequest, AuditedRequestContext> createAuditRequest(AuditedRequest auditedRequest, AuditedRequestContext ctx) {
        AuditRequest<AuditedRequest, AuditedRequestContext> wrappedRequest = null;
        if (auditRequestFactory != null) {
            wrappedRequest = auditRequestFactory.apply(auditedRequest, ctx);
        }
        if (wrappedRequest == null) {
            wrappedRequest = new AuditRequest<AuditedRequest, AuditedRequestContext>();
            wrappedRequest.setRequest(auditedRequest);
            wrappedRequest.setRequestContext(ctx);
        }
        return wrappedRequest;
    }

    public Function2<AuditedRequest, AuditedRequestContext, AuditRequest<AuditedRequest, AuditedRequestContext>> getAuditRequestFactory() {
        return auditRequestFactory;
    }

    public void setAuditRequestFactory(Function2<AuditedRequest, AuditedRequestContext, AuditRequest<AuditedRequest, AuditedRequestContext>> auditRequestFactory) {
        this.auditRequestFactory = auditRequestFactory;
    }

    public AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> getBeforeExtractFilterChain() {
        return beforeExtractFilterChain;
    }

    public void setBeforeExtractFilterChain(AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> beforeExtractFilterChain) {
        this.beforeExtractFilterChain = beforeExtractFilterChain;
    }

    public AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> getAfterExtractFilterChain() {
        return afterExtractFilterChain;
    }

    public void setAfterExtractFilterChain(AuditRequestFilterChain<AuditedRequest, AuditedRequestContext> afterExtractFilterChain) {
        this.afterExtractFilterChain = afterExtractFilterChain;
    }

    public AuditEventExtractor<AuditedRequest, AuditedRequestContext> getAuditEventExtractor() {
        return auditEventExtractor;
    }

    public void setAuditEventExtractor(AuditEventExtractor<AuditedRequest, AuditedRequestContext> auditEventExtractor) {
        this.auditEventExtractor = auditEventExtractor;
    }

    /**************************************************************
     *
     *  AuditRequest Cache
     *
     ***************************************************************/

    public static void cacheRequest(AuditRequest wrappedRequest) {
        Stack<AuditRequest> cache = auditRequestHolder.get();
        if (cache == null) {
            cache = new Stack<AuditRequest>();
            auditRequestHolder.set(cache);
        }
        cache.push(wrappedRequest);
    }

    public static AuditRequest getRequest() {
        return getOrRemoveRequest(false);
    }

    public static AuditRequest removeRequest() {
        return getOrRemoveRequest(true);
    }

    private static AuditRequest getOrRemoveRequest(boolean remove) {
        Stack<AuditRequest> cache = auditRequestHolder.get();
        if (Objs.isNotEmpty(cache)) {
            if (remove) {
                return cache.pop();
            } else {
                return cache.peek();
            }
        } else {
            return null;
        }
    }

    public static <AuditedRequest> void removeByOriginalRequest(AuditedRequest originalRequest) {
        AuditRequest wrapped = getOrRemoveRequest(false);
        if (wrapped != null) {
            if (wrapped.getRequest() == originalRequest) {
                getOrRemoveRequest(true);
            }
        }
    }
}
