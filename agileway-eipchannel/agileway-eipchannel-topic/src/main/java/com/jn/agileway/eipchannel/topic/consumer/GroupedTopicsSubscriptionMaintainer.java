package com.jn.agileway.eipchannel.topic.consumer;

import com.jn.langx.Named;

import java.util.Collection;
import java.util.List;

/**
 * 一个 consumer 对应一个 GroupedTopicsSubscriptionMaintainer
 * @param <GroupedTopicsConsumer>
 */
public interface GroupedTopicsSubscriptionMaintainer<GroupedTopicsConsumer> extends Named {
    GroupedTopicsConsumer getConsumer();
    void setConsumer(GroupedTopicsConsumer consumer);
    void setLastSubscribeTime(long lastSubscribeTime);
    long getLastSubscribeTime();



    void removeTopics(String group, Collection<String> topics) ;
    void addTopics(String group,Collection<String> topics);
    void replaceTopics(String group,Collection<String> topics);

    /*************************************************************************************************
     * 期望的 topic 相关方法
     *************************************************************************************************/

    /**
     * @return 获取期望的topics
     */
    List<String> getExpectedTopics();

    /**
     * @return 获取期望的topic总数
     */
    int getExpectedTopicCount();

    /*************************************************************************************************
     * 正在消费的 topic 相关方法
     *************************************************************************************************/

    /**
     * @return 获取正在消费的 topic 列表
     */
    List<String> getActualTopics();
    /**
     * @return 获取正在消费的topic总数
     */
    int getActualTopicCount();

    /**
     * 实际正在消费的 topic中，是否有某个组下的。
     */
    boolean actualContainsGroup(String group);



}
