package com.jn.agileway.dmmq.core;

public interface MessageTopicDispatcherAware {
    MessageTopicDispatcher getMessageTopicDispatcher();
    void setMessageTopicDispatcher(MessageTopicDispatcher dispatcher);
}
