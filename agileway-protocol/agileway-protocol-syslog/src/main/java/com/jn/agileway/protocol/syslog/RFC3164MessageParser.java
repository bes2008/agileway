package com.jn.agileway.protocol.syslog;

import com.jn.langx.util.Strings;
import com.jn.langx.util.datetime.DateTimeParsedResult;
import com.jn.langx.util.regexp.RegexpMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * https://www.rfc-editor.org/rfc/rfc3164
 *
 * 时间戳格式必须是：
 * MMM dd hh:mm:ss
 */
public class RFC3164MessageParser extends MessageParser {
    private static final Logger log = LoggerFactory.getLogger(RFC3164MessageParser.class);
    private static final String PATTERN = "^(<(?<priority>\\d+)>)?(?<date>([a-zA-Z]{3}\\s+\\d+\\s+\\d+:\\d+:\\d+)|([0-9T:.Z-]+))\\s+(?<host>\\S+)\\s+((?<tag>[^\\[\\s\\]]+)(\\[(?<procid>\\d+)\\])?:)*\\s*(?<message>.+)$";
    private final ThreadLocal<RegexpMatcher> matcherThreadLocal;

    public RFC3164MessageParser() {
        this.matcherThreadLocal = initMatcher(PATTERN);
    }


    public SyslogMessage parse(String rawMessage) {
        final RegexpMatcher matcher = matcherThreadLocal.get().reset(rawMessage);

        if (!matcher.find()) {
            log.trace("parse() - Could not match message: {}", rawMessage);
            return null;
        }

        log.trace("parse() - Parsed message as RFC 3164");
        final String groupPriority = matcher.group("priority");
        final String groupDate = matcher.group("date");
        final String groupHost = matcher.group("host");
        final String groupMessage = matcher.group("message");
        final String groupTag = matcher.group("tag");
        final String groupProcId = matcher.group("procid");
        final String processId = Strings.isEmpty(groupProcId) ? null : groupProcId;
        final Integer priority = Strings.isEmpty(groupPriority) ? null : Integer.parseInt(groupPriority);
        final Integer facility = null == priority ? null : Priority.facility(priority);
        final Integer level = null == priority ? null : Priority.level(priority, facility);

        SyslogMessage syslogMessage = new SyslogMessage();
        syslogMessage.setType(MessageType.RFC3164);
        syslogMessage.setRawMessage(rawMessage);
        DateTimeParsedResult parsedResult = parseDate(groupDate);
        syslogMessage.setTimestamp(parsedResult.getTimestamp());
        syslogMessage.setHost(groupHost);
        syslogMessage.setLevel(level);
        syslogMessage.setFacility(facility);
        syslogMessage.setMessage(groupMessage);
        syslogMessage.setTag(groupTag);
        syslogMessage.setProcessId(processId);
        return syslogMessage;
    }
}
