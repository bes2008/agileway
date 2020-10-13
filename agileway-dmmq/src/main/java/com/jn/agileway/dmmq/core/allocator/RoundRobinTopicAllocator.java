package com.jn.agileway.dmmq.core.allocator;

import com.jn.langx.util.collection.Collects;

import java.util.List;

public class RoundRobinTopicAllocator<M> extends AbstractMultipleCandidateTopicAllocator<M> {

    private volatile int nextIndex = 0;

    @Override
    public String apply(M input) {
        int i = nextIndex;
        if (nextIndex >= validTopics.size()) {
            i = 0;
            nextIndex = i;
        } else {
            nextIndex = i + 1;
        }
        List<String> list = Collects.asList(validTopics);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > i) {
            return list.get(i);
        }
        return null;
    }
}
