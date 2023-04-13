package com.jn.agileway.syslog.protocol;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Dates;
import com.jn.langx.util.collection.Maps;

import java.util.Map;

public class RFC5424SyslogTextGenerator implements SyslogTextGenerator {
    private static final String TEMPLATE = "<${priority}>${version} ${timestamp} ${hostname} ${appName} ${procId} ${msgId} ${structuredData} ${content}";

    private String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    private StructuredDataFormatter structuredDataFormatter;

    @Override
    public void setDatePattern(String pattern) {
        this.datePattern = pattern;
    }

    public void setStructuredDataFormatter(StructuredDataFormatter structuredDataFormatter) {
        this.structuredDataFormatter = structuredDataFormatter;
    }

    @Override
    public String get(SyslogMessage syslogMessage) {
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("priority", syslogMessage.getPriority());
        map.put("version", syslogMessage.getVersion());
        String timestamp = Dates.format(syslogMessage.getTimestamp(), datePattern);
        map.put("timestamp", timestamp);
        map.put("hostname", syslogMessage.getHostname());
        map.put("appName", syslogMessage.getAppName());
        map.put("procId", syslogMessage.getProcId());
        map.put("msgId",syslogMessage.getMsgId());
        String structuredData = structuredDataFormatter.format(syslogMessage.getStructuredData());
        map.put("structuredData", structuredData);
        map.put("content", syslogMessage.getContent());
        String str = StringTemplates.formatWithMap(TEMPLATE, map);
        return str;
    }
}
