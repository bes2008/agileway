package com.jn.agileway.dmmq.core.allocator;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Function;

import java.util.List;

public class HashedTopicAllocator<M> extends AbstractMultipleCandidateTopicAllocator<M> {

    private Function<M, Integer> hasher;

    public void setHasher(Function<M, Integer> hasher) {
        this.hasher = hasher;
    }

    @Override
    public String apply(M message) {
        List<String> topics = Collects.asList(validTopics);
        if (topics.isEmpty()) {
            return null;
        }
        return topics.get(hasher.apply(message) % topics.size());
    }
}
