package com.jn.agileway.kafka.topic;

import com.jn.langx.Nameable;

public class GroupedTopic implements Nameable {
    /**
     * topic name
     */
    private String topic;

    /**
     * 全局唯一名
     *
     */
    private String name;

    /**
     * 所属组
     */
    private String group;
    /**
     * 位于 哪个集群
     */
    private String located;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getLocated() {
        return located;
    }

    public void setLocated(String located) {
        this.located = located;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
