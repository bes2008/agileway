package com.jn.agileway.protocol.syslog;

import com.jn.langx.exception.SyntaxException;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(RFC3164MessageParser.class);
    private static final String PATTERN = "^(<(?<priority>\\d+)>)?(?<date>([a-zA-Z]{3}\\s+\\d+\\s+\\d+:\\d+:\\d+)|([0-9T:.Z-]+))\\s+(?<host>\\S+)\\s+((?<tag>[^\\[\\s\\]]+)(\\[(?<procid>\\d+)\\])?:)*\\s*(?<content>.+)$";
    private final ThreadLocal<RegexpMatcher> matcherThreadLocal;

    public RFC3164MessageParser() {
        this.matcherThreadLocal = initMatcher(PATTERN);
    }


    public SyslogMessage parse(String rawMessage) {
        final RegexpMatcher matcher = matcherThreadLocal.get().reset(rawMessage);

        if (!matcher.find()) {
            LOGGER.trace("parse() - Could not match message: {}", rawMessage);
            return null;
        }

        LOGGER.trace("parse() - Parsed message as RFC 3164");
        final String groupPriority = matcher.group("priority");
        final String groupDate = matcher.group("date");
        final String groupHost = matcher.group("host");

        if(Strings.isEmpty(groupPriority)){
            throw new SyntaxException("invalid syslog-3164 message, the priority is missing");
        }


        if(Strings.isEmpty(groupDate)){
            throw new SyntaxException("invalid syslog-3164 message, the timestamp is missing");
        }
        if(Strings.isEmpty(groupHost)){
            throw new SyntaxException("invalid syslog-3164 message, the host is missing");
        }


        SyslogMessage syslogMessage = new SyslogMessage();
        syslogMessage.setType(MessageType.RFC3164);
        syslogMessage.setRawMessage(rawMessage);


        final Integer priority = Strings.isEmpty(groupPriority) ? null : Integer.parseInt(groupPriority);
        final Integer facility = Priority.getFacility(priority);
        final Severity severity = Priority.getSeverity(priority, facility);
        syslogMessage.setSeverity(severity);
        syslogMessage.setFacility(facility);

        DateTimeParsedResult parsedResult = parseDate(groupDate);
        syslogMessage.setTimestamp(parsedResult.getTimestamp());

        syslogMessage.setHostname(groupHost);

        final String groupTag = matcher.group("tag");
        final String groupMessage = matcher.group("content");
        syslogMessage.setContent(groupMessage);
        syslogMessage.setTag(groupTag);

        final String groupProcId = matcher.group("procid");
        final String processId = Strings.isEmpty(groupProcId) ? null : groupProcId;
        syslogMessage.setProcId(processId);
        return syslogMessage;
    }
}
