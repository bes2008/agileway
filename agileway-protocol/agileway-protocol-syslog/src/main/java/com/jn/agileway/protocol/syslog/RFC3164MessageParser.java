package com.jn.agileway.protocol.syslog;

import com.jn.agileway.protocol.syslog.model.ImmutableSyslogMessage;
import com.jn.agileway.protocol.syslog.model.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.regex.Matcher;

public class RFC3164MessageParser {
  private static final Logger log = LoggerFactory.getLogger(RFC3164MessageParser.class);
  private static final String PATTERN = "^(<(?<priority>\\d+)>)?(?<date>([a-zA-Z]{3}\\s+\\d+\\s+\\d+:\\d+:\\d+)|([0-9T:.Z-]+))\\s+(?<host>\\S+)\\s+((?<tag>[^\\[\\s\\]]+)(\\[(?<procid>\\d+)\\])?:)*\\s*(?<message>.+)$";
  private final ThreadLocal<Matcher> matcherThreadLocal;

  public RFC3164MessageParser() {
    this.matcherThreadLocal = initMatcher(PATTERN);
  }

  public ImmutableSyslogMessage parse(String rawMessage) {
    final Matcher matcher = matcherThreadLocal.get().reset(rawMessage);

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
    final String processId = (groupProcId == null || groupProcId.isEmpty()) ? null : groupProcId;
    final Integer priority = (groupPriority == null || groupPriority.isEmpty()) ? null : Integer.parseInt(groupPriority);
    final Integer facility = null == priority ? null : Priority.facility(priority);
    final Integer level = null == priority ? null : Priority.level(priority, facility);
    final LocalDateTime date = parseDate(groupDate);


    return ImmutableSyslogMessage.builder()
        .type(MessageType.RFC3164)
        .rawMessage(rawMessage)
        .remoteAddress(request.remoteAddress())
        .date(date)
        .host(groupHost)
        .level(level)
        .facility(facility)
        .message(groupMessage)
        .tag(groupTag)
        .processId(processId)
        .build();

  }
}
