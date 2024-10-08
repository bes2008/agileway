package com.jn.agileway.syslog.protocol;

import com.jn.langx.util.Strings;
import com.jn.langx.util.datetime.DateTimeParsedResult;
import com.jn.langx.util.regexp.RegexpMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * https://learn.microsoft.com/en-us/azure/sentinel/connect-common-event-format
 */
public class CEFOverSyslogMessageParser extends MessageParser {
    private static final Logger log = LoggerFactory.getLogger(CEFOverSyslogMessageParser.class);
    private static final String CEF_PREFIX_PATTERN = "^(<(?<priority>\\d+)>)?(?<date>([a-zA-Z]{3}\\s+\\d+\\s+\\d+:\\d+:\\d+)|([0-9T:.Z-]+))\\s+(?<host>\\S+)\\s+CEF:(?<version>\\d+)\\|(?<data>.*)$";
    private static final String CEF_MAIN_PATTERN = "(?<!\\\\)\\|";
    private static final String PATTERN_EXTENSION = "(\\w+)=";

    private final ThreadLocal<RegexpMatcher> matcherCEFPrefix;
    private final ThreadLocal<RegexpMatcher> matcherCEFMain;
    private final ThreadLocal<RegexpMatcher> matcherCEFExtension;


    public CEFOverSyslogMessageParser() {
        this.matcherCEFPrefix = initMatcher(CEF_PREFIX_PATTERN);
        this.matcherCEFMain = initMatcher(CEF_MAIN_PATTERN);
        this.matcherCEFExtension = initMatcher(PATTERN_EXTENSION);
    }

    List<String> splitToList(String data) {
        List<String> result = new ArrayList<>(10);
        final RegexpMatcher matcherData = this.matcherCEFMain.get().reset(data);

        int start = 0;
        int end = 0;
        while (matcherData.find()) {
            end = matcherData.end();
            String part = data.substring(start, end - 1);
            start = end;
            result.add(part);
        }

        if (data.length() > end) {
            result.add(data.substring(end));
        }

        return result;
    }

    @Override
    public SyslogMessage parse(String rawMessage) {
        log.trace("parse() - rawMessage = '{}'", rawMessage);
        final RegexpMatcher matcherPrefix = this.matcherCEFPrefix.get().reset(rawMessage);

        if (!matcherPrefix.find()) {
            log.trace("parse() - Could not match message. request = '{}'", rawMessage);
            return null;
        }

        log.trace("parse() - Parsed message as CEF.");
        final String groupPriority = matcherPrefix.group("priority");
        final String groupDate = matcherPrefix.group("date");
        final String groupHost = matcherPrefix.group("host");
        final String groupCEFVersion = matcherPrefix.group("version");
        final String groupData = matcherPrefix.group("data");


        final Integer priority = Strings.isEmpty(groupPriority) ? null : Integer.parseInt(groupPriority);

        final Integer facility = priority == null ? null : Priority.getFacility(priority);
        final Severity severity = priority == null ? null : Priority.getSeverity(priority, facility);
        final DateTimeParsedResult date = parseDate(groupDate);
        final Integer cefVersion = Integer.parseInt(groupCEFVersion);

        final List<String> parts = splitToList(groupData);

        SyslogMessage syslogMessage = new SyslogMessage();
        syslogMessage.setType(MessageType.CEF);
        syslogMessage.setRawMessage(rawMessage);
        syslogMessage.setTimestamp(date.getTimestamp());
        syslogMessage.setVersion(cefVersion);
        syslogMessage.setHostname(groupHost);
        syslogMessage.setSeverity(severity);
        syslogMessage.setFacility(facility);


        int index = 0;
        for (String token : parts) {
            token = token.replace("\\|", "|");
            log.trace("parse() - index={}, token='{}'", index, token);

            switch (index) {
                case 0:
                    syslogMessage.setDeviceVendor(token);
                    break;
                case 1:
                    syslogMessage.setDeviceProduct(token);
                    break;
                case 2:
                    syslogMessage.setDeviceVersion(token);
                    break;
                case 3:
                    syslogMessage.setDeviceEventClassId(token);
                    break;
                case 4:
                    syslogMessage.setName(token);
                    break;
                case 5:
                    // syslogMessage.setSeverity(token);
                    // ignore
                    break;
                case 6:
                    Map<String, String> extension = parseExtension(token);
                    syslogMessage.setExtension(extension);
                    break;
                default:
                    break;
            }

            index++;
        }

        return syslogMessage;
    }

    private Map<String, String> parseExtension(String token) {
        log.trace("parseExtension() - token = '{}'", token);
        final Map<String, String> result = new LinkedHashMap<>();
        if (null == token || token.isEmpty()) {
            return result;
        }

        RegexpMatcher matcher = this.matcherCEFExtension.get().reset(token);

        String key = null;
        String value;
        int lastEnd = -1, lastStart = -1;

        while (matcher.find()) {
            log.trace("parseExtension() - matcher.start() = {}, matcher.end() = {}", matcher.start(), matcher.end());

            if (lastEnd > -1) {
                value = token.substring(lastEnd, matcher.start()).trim();
                result.put(key, value);
                log.trace("parseExtension() - key='{}' value='{}'", key, value);
            }

            key = matcher.group(1);
            lastStart = matcher.start();
            lastEnd = matcher.end();
        }

        if (lastStart > -1 && !result.containsKey(key)) {
            value = token.substring(lastEnd).trim();
            result.put(key, value);
            log.trace("parseExtension() - key='{}' value='{}'", key, value);
        }

        return result;
    }
}
