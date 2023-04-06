package com.jn.agileway.syslog.protocol;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Dates;
import com.jn.langx.util.Objs;

public class RFC3164SyslogTextGenerator implements SyslogTextGenerator {

    private static final String templateWithTag = "<{}>{} {} {}: {}";
    private static final String templateWithoutTag = "<{}>{} {} {}";
    private boolean iso8601Enabled = false;

    public boolean isIso8601Enabled() {
        return iso8601Enabled;
    }

    public void setIso8601Enabled(boolean iso8601Enabled) {
        this.iso8601Enabled = iso8601Enabled;
    }

    @Override
    public String get(SyslogMessage syslogMessage) {
        String msg = null;
        String timestamp = iso8601Enabled ? Dates.format(syslogMessage.getTimestamp(), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") : Dates.format(syslogMessage.getTimestamp(), "MMM dd HH:mm:ss");

        if (Objs.isNotEmpty(syslogMessage.getTag())) {
            msg = StringTemplates.formatWithPlaceholder(templateWithTag, syslogMessage.getPriority(), timestamp, syslogMessage.getHostname(), syslogMessage.getTag(), syslogMessage.getContent());
        } else {
            msg = StringTemplates.formatWithPlaceholder(templateWithoutTag, syslogMessage.getPriority(), timestamp, syslogMessage.getHostname(), syslogMessage.getContent());
        }
        return msg;

    }
}
