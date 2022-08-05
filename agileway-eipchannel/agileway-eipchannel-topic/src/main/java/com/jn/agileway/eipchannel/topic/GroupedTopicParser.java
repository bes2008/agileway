package com.jn.agileway.eipchannel.topic;

import com.jn.langx.Parser;

public interface GroupedTopicParser extends Parser<String, GroupedTopic> {
    @Override
    GroupedTopic parse(String unifiedName);
}
