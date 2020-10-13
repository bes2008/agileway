package com.jn.agileway.dmmq.core.allocator;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.random.ThreadLocalRandom;

public class RandomTopicAllocator<M> extends AbstractMultipleCandidateTopicAllocator<M> {

    @Override
    public String apply(M input) {
        if (validTopics.isEmpty()) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(0, validTopics.size());
        return Collects.asList(validTopics).get(index);
    }


}
