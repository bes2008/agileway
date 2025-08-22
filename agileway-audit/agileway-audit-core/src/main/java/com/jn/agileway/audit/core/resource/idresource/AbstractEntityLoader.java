package com.jn.agileway.audit.core.resource.idresource;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.model.ResourceDefinition;
import com.jn.langx.lifecycle.Destroyable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.logging.Level;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public abstract class AbstractEntityLoader<E> implements EntityLoader<E>, Destroyable {
    private ExecutorService executor;

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    protected abstract Logger getLogger();

    @Override
    public List<E> load(final AuditRequest request, final ResourceDefinition resourceDefinition, final List<Serializable> ids) {
        MapAccessor mapAccessor = resourceDefinition.getDefinitionAccessor();

        // 总数
        int size = ids.size();
        // 一批加载多少个id
        int _batchSize = getBatchSize(mapAccessor, ids);
        if (_batchSize < 1) {
            _batchSize = 1;
        }
        final int batchSize = _batchSize;

        // 是否批量执行
        int taskCount = size / batchSize + (size % batchSize != 0 ? 1 : 0);
        // 是否并行执行
        boolean _isParallel = false;
        if (taskCount > 1) {
            _isParallel = isParallel(mapAccessor, ids);
        }
        final boolean isParallel = _isParallel;

        // 下面是执行的逻辑
        final Vector<E> entitiesVector = new Vector<E>();
        final List<CompletableFuture> futures = Collects.emptyArrayList();
        Collects.forEach(Collects.asList(Arrs.range(0, ids.size(), batchSize)), new Consumer<Integer>() {
            @Override
            public void accept(Integer offset) {
                final List<Serializable> partitionIds = Pipeline.of(ids).skip(offset).limit(batchSize).asList();

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<E> partition = loadInternal(request, resourceDefinition, partitionIds);
                            entitiesVector.addAll(partition);
                        } catch (Throwable ex) {
                            Throwables.log(getLogger(),
                                    Level.ERROR,
                                    StringTemplates.formatWithPlaceholder("error occur when load entities by {} loader , ids: {}, error: {}", getName(), Strings.join(",", ids), ex.getMessage()),
                                    ex);
                        }
                    }
                };
                if (isParallel) {
                    futures.add(CompletableFuture.runAsync(task, executor));
                } else {
                    task.run();
                    futures.add(CompletableFuture.completedFuture(null));
                }

            }
        });
        CompletableFuture.allOf(Collects.toArray(futures, CompletableFuture[].class)).join();
        return Collects.newArrayList(entitiesVector);
    }

    /**
     * 每一批的大小
     */
    protected int getBatchSize(MapAccessor mapAccessor, List<Serializable> ids) {
        return mapAccessor.getInteger("batchSize", 1);
    }

    /**
     * 是否采用并行执行，充分利用多CPU执行。如果返回 true，则必须自行保证 executor != null;
     */
    protected boolean isParallel(MapAccessor mapAccessor, List<Serializable> ids) {
        return mapAccessor.getBoolean("parallel", executor != null);
    }

    /**
     * 一次性取一批
     */
    protected abstract List<E> loadInternal(AuditRequest request, ResourceDefinition resourceDefinition, List<Serializable> partitionIds);

    @Override
    public void destroy() {
        this.executor.shutdown();
    }
}
