package com.jn.agileway.syslog.protocol;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Dates;
import com.jn.langx.util.Objs;

public class RFC3164SyslogTextGenerator implements SyslogTextGenerator {

    private static final String templateWithTag = "<{}>{} {} {}: {}";
    private static final String templateWithoutTag = "<{}>{} {} {}";

    private String datePattern = "MMM dd HH:mm:ss";

    @Override
    public void setDatePattern(String pattern) {
        this.datePattern = pattern;
    }

    @Override
    public String get(SyslogMessage syslogMessage) {
        String msg = null;
        String timestamp = Dates.format(syslogMessage.getTimestamp(), datePattern);

        if (Objs.isNotEmpty(syslogMessage.getTag())) {
            msg = StringTemplates.formatWithPlaceholder(templateWithTag, syslogMessage.getPriority(), timestamp, syslogMessage.getHostname(), syslogMessage.getTag(), syslogMessage.getContent());
        } else {
            msg = StringTemplates.formatWithPlaceholder(templateWithoutTag, syslogMessage.getPriority(), timestamp, syslogMessage.getHostname(), syslogMessage.getContent());
        }
        return msg;

    }
}
