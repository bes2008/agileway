package com.jn.agileway.eipchannel.tests;

import com.jn.langx.event.DomainEvent;
import com.jn.langx.text.StringTemplates;

public class UserEvent extends DomainEvent<String> {
    private UserEventType userEventType;

    public UserEvent(String domain, String userId) {
        super(domain, userId);
    }

    public void setUserEventType(UserEventType userEventType) {
        this.userEventType = userEventType;
    }

    public UserEventType getUserEventType() {
        return userEventType;
    }

    @Override
    public String toString() {
        return StringTemplates.formatWithPlaceholder("event-type: {}, event-source: {}", getUserEventType(), getSource());
    }
}
