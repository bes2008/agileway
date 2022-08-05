package com.jn.agileway.eipchannel.topic;

import com.jn.langx.Nameable;

public class GroupedTopic implements Nameable {
    /**
     * topic name
     */
    private String topic;

    /**
     * 全局唯一名
     */
    private String name;

    /**
     * 所属逻辑组
     */
    private String group;
    /**
     * 集群的地址
     */
    private String clusterAddress;

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

    public String getClusterAddress() {
        return clusterAddress;
    }

    public void setClusterAddress(String clusterAddress) {
        this.clusterAddress = clusterAddress;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
