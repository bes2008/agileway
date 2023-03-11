package com.jn.agileway.protocol.syslog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MessageParser {
    private static final Logger log = LoggerFactory.getLogger(MessageParser.class);
    private static final String NULL_TOKEN = "-";
    protected final List<DateTimeFormatter> dateFormats;
    private final ThreadLocal<Matcher> matcherStructuredData;
    private final ThreadLocal<Matcher> matcherKeyValue;
    private final ZoneId zoneId;

    public MessageParser() {
        this(ZoneId.of("UTC"));
    }

    public MessageParser(ZoneId zoneId) {
        this.zoneId = zoneId;

        this.dateFormats = Arrays.asList(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME,

                //This supports
                new DateTimeFormatterBuilder()
                        .appendPattern("MMM d")
                        .optionalStart()
                        .appendPattern("[ yyyy]")
                        .parseDefaulting(ChronoField.YEAR_OF_ERA, 1)
                        .optionalEnd()
                        .appendPattern(" HH:mm:ss")
                        .toFormatter()
        );

        this.matcherStructuredData = initMatcher("\\[([^\\]]+)\\]");
        this.matcherKeyValue = initMatcher("(?<key>\\S+)=\"(?<value>[^\"]+)\"|(?<id>\\S+)");
    }

    /**
     * Method is used to parse an incoming syslog message.
     *
     * @return Object to pass along the pipeline. Null if could not be parsed.
     */
    public abstract SyslogMessage parse(String line);

    protected final ThreadLocal<Matcher> initMatcher(String pattern) {
        return initMatcher(pattern, 0);
    }

    protected final ThreadLocal<Matcher> initMatcher(String pattern, int flags) {
        final Pattern p = Pattern.compile(pattern, flags);
        return new MatcherInheritableThreadLocal(p);
    }

    protected String nullableString(String groupText) {
        return NULL_TOKEN.equals(groupText) ? null : groupText;
    }

    protected LocalDateTime parseDate(String date) {
        final String cleanDate = date.replaceAll("\\s+", " ");
        LocalDateTime result = null;

        for (DateTimeFormatter formatter : this.dateFormats) {
            try {
                final TemporalAccessor temporal = formatter.parseBest(
                        cleanDate,
                        OffsetDateTime::from,
                        LocalDateTime::from
                );

                if (temporal instanceof LocalDateTime) {
                    result = ((LocalDateTime) temporal);
                } else {
                    result = ((OffsetDateTime) temporal).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
                }
        /*
        The parser will output dates that do not have a year. If this happens we default the year
        to 1 AD which I'm pretty sure there were no computers. This means that the sender was a lazy
        ass and didn't sent a date. This is easy to detect so we set it to the current date.
         */

                if (result.getLong(ChronoField.YEAR_OF_ERA) == 1) {
                    result = result.withYear(LocalDateTime.now(this.zoneId).getYear());
                }
                break;
            } catch (java.time.DateTimeException e) {
                log.trace("parseDate() - Could not parse '{}' with '{}'", cleanDate, formatter.toString());
            }
        }
        if (null == result) {
            log.error("Could not parse date '{}'", cleanDate);
        }
        return result;
    }

    protected List<StructuredData> parseStructuredData(String structuredData) {
        log.trace("parseStructuredData() - structuredData = '{}'", structuredData);
        final Matcher matcher = matcherStructuredData.get().reset(structuredData);
        final List<StructuredData> result = new ArrayList<>();
        while (matcher.find()) {
            final String input = matcher.group(1);
            log.trace("parseStructuredData() - input = '{}'", input);
            StructuredData sd = new StructuredData();

            final Matcher kvpMatcher = matcherKeyValue.get().reset(input);
            while (kvpMatcher.find()) {
                final String key = kvpMatcher.group("key");
                final String value = kvpMatcher.group("value");
                final String id = kvpMatcher.group("id");

                if (null != id && !id.isEmpty()) {
                    log.trace("parseStructuredData() - id='{}'", id);
                    sd.setId(id);
                } else {
                    log.trace("parseStructuredData() - key='{}' value='{}'", key, value);
                    sd.addStructuredDataElement(key, value);
                }
            }
            result.add(sd);
        }
        return result;
    }


    static class MatcherInheritableThreadLocal extends InheritableThreadLocal<Matcher> {
        private final Pattern pattern;

        MatcherInheritableThreadLocal(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        protected Matcher initialValue() {
            return this.pattern.matcher("");
        }
    }

}
