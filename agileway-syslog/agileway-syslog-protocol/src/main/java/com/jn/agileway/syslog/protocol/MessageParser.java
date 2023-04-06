package com.jn.agileway.syslog.protocol;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.datetime.DateTimeParsedResult;
import com.jn.langx.util.datetime.DateTimeParser;
import com.jn.langx.util.datetime.parser.CandidatePatternsDateTimeParser;
import com.jn.langx.util.datetime.parser.DateParsedResult;
import com.jn.langx.util.regexp.Regexp;
import com.jn.langx.util.regexp.RegexpMatcher;
import com.jn.langx.util.regexp.Regexps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class MessageParser {
    private static final Logger log = LoggerFactory.getLogger(MessageParser.class);
    private static final String NULL_TOKEN = "-";
    private final ThreadLocal<RegexpMatcher> matcherStructuredData;
    private final ThreadLocal<RegexpMatcher> matcherKeyValue;

    protected DateTimeParser dateTimeParser;


    public MessageParser() {
        this(TimeZone.getDefault());
    }

    public MessageParser(TimeZone timeZone) {
        this.matcherStructuredData = initMatcher("\\[([^\\]]+)\\]");
        this.matcherKeyValue = initMatcher("(?<key>\\S+)=\"(?<value>[^\"]+)\"|(?<id>\\S+)");
        // 这个是 基于 RFC3164 的格式，如果使用RFC 5424 ，需要调用set方法自定义
        this.setDateTimeParser(new CandidatePatternsDateTimeParser(Collects.asList("MMM dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"), Collects.asList(timeZone, TimeZone.getTimeZone("UTC")), Collects.asList(Locale.getDefault(), Locale.US)));
    }

    public void setDateTimeParser(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    /**
     * Method is used to parse an incoming syslog message.
     *
     * @return Object to pass along the pipeline. Null if could not be parsed.
     */
    public abstract SyslogMessage parse(String line);

    protected final ThreadLocal<RegexpMatcher> initMatcher(String pattern) {
        return initMatcher(pattern, 0);
    }

    protected final ThreadLocal<RegexpMatcher> initMatcher(String pattern, int flags) {
        final Regexp p = Regexps.createRegexp(pattern, flags);
        return new MatcherInheritableThreadLocal(p);
    }

    protected String nullableString(String groupText) {
        return NULL_TOKEN.equals(groupText) ? null : groupText;
    }

    protected DateTimeParsedResult parseDate(String date) {
        final String cleanDate = date.replaceAll("\\s+", " ");
        DateTimeParsedResult parsedResult = this.dateTimeParser.parse(cleanDate);

        Calendar calendar = Calendar.getInstance(parsedResult.getTimeZone());
        calendar.setTime(new Date(parsedResult.getTimestamp()));
        int year = calendar.get(Calendar.YEAR);
        if (year <= 1970) {
            int month = calendar.get(Calendar.MONTH) + 1;
            Calendar now = Calendar.getInstance();
            int currentMonth = now.get(Calendar.MONTH) + 1;
            int theYear = now.get(Calendar.YEAR);
            if (currentMonth < month) {
                theYear = theYear - 1;
            }
            calendar.set(Calendar.YEAR, theYear);

            // reset parsedResult

            DateParsedResult r = new DateParsedResult(calendar.getTime(), parsedResult.getTimeZone(), parsedResult.getLocale());
            r.setOriginText(parsedResult.getOriginText());
            r.setPattern(parsedResult.getPattern());
            parsedResult = r;
        }

        return parsedResult;
    }

    protected List<StructuredElement> parseStructuredData(String structuredData) {
        log.trace("parseStructuredData() - structuredData = '{}'", structuredData);
        final RegexpMatcher matcher = matcherStructuredData.get().reset(structuredData);
        final List<StructuredElement> result = new ArrayList<StructuredElement>();
        while (matcher.find()) {
            final String input = matcher.group(1);
            log.trace("parseStructuredData() - input = '{}'", input);
            StructuredElement sd = new StructuredElement();

            final RegexpMatcher kvpMatcher = matcherKeyValue.get().reset(input);
            while (kvpMatcher.find()) {
                final String key = kvpMatcher.group("key");
                final String value = kvpMatcher.group("value");
                final String id = kvpMatcher.group("id");

                if (null != id && !id.isEmpty()) {
                    log.trace("parseStructuredData() - id='{}'", id);
                    sd.setId(id);
                } else {
                    log.trace("parseStructuredData() - key='{}' value='{}'", key, value);
                    sd.addParameter(key, value);
                }
            }
            result.add(sd);
        }
        return result;
    }


    static class MatcherInheritableThreadLocal extends InheritableThreadLocal<RegexpMatcher> {
        private final Regexp regexp;

        MatcherInheritableThreadLocal(Regexp pattern) {
            this.regexp = pattern;
        }

        @Override
        protected RegexpMatcher initialValue() {
            return this.regexp.matcher("");
        }
    }

}
