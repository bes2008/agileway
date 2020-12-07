package com.jn.agileway.spring.messagestorage;

import com.jn.langx.text.i18n.AbstractI18nMessageStorage;
import com.jn.langx.util.Emptys;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class SpringMessageStorage extends AbstractI18nMessageStorage {
    private MessageSource messageSource;

    public SpringMessageStorage() {
    }

    public SpringMessageStorage(MessageSource messageSource) {
        setMessageSource(messageSource);
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected String getMessageInternal(Locale locale, ClassLoader classLoader, String key, Object... objects) {
        String message = messageSource.getMessage(key, objects, locale);
        if (Emptys.isEmpty(message) && getParent() != null) {
            message = getParent().getMessage(locale, classLoader, key, objects);
        }
        return message;
    }
}
