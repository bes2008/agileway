package com.jn.agileway.protocol.syslog;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rfc3164SyslogParser {
    private static final Logger logger  = Loggers.getLogger(Rfc3164SyslogParser.class);
    public static final String FACILITY = "FACILITY";

    public static final String SEVERITY = "SEVERITY";

    public static final String TIMESTAMP = "TIMESTAMP";

    public static final String HOST = "HOST";

    public static final String TAG = "TAG";

    public static final String MESSAGE = "MESSAGE";

    public static final String UNDECODED = "UNDECODED";

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd HH:mm:ss");

    private final Pattern pattern = Pattern.compile("<([^>]+)>(.{15}) ([^ ]+) ([a-zA-Z0-9]{0,32})(.*)", Pattern.DOTALL);

    protected Map<String, ?> transformPayload(Object payload) {
        boolean isByteArray = payload instanceof byte[];
        boolean isString = payload instanceof String;
        Preconditions.checkTrue(isByteArray || isString, "payload must be String or byte[]");
        if (isByteArray) {
            return this.transform((byte[]) payload);
        }
        else {
            return this.transform((String) payload);
        }
    }

    private Map<String, ?> transform(byte[] payloadBytes) {
        return transform(new String(payloadBytes, StandardCharsets.UTF_8));
    }

    private Map<String, ?> transform(String payload) {
        Map<String, Object> map = new LinkedHashMap<>();
        Matcher matcher = this.pattern.matcher(payload);
        if (matcher.matches()) {
            parseMatcherToMap(payload, matcher, map);
        }
        else {
            logger.debug("Could not decode: " + payload);
            map.put(UNDECODED, payload);
        }
        return map;
    }

    private void parseMatcherToMap(Object payload, Matcher matcher, Map<String, Object> map) {
        try {
            String facilityString = matcher.group(1); // NOSONAR
            int facility = Integer.parseInt(facilityString);
            int severity = facility & 0x7; // NOSONAR
            facility = facility >> 3; // NOSONAR
            map.put(FACILITY, facility);
            map.put(SEVERITY, severity);
            String timestamp = matcher.group(2); // NOSONAR
            parseTimestampToMap(timestamp, map);
            map.put(HOST, matcher.group(3)); // NOSONAR
            String tag = matcher.group(4); // NOSONAR
            if (Strings.isNotBlank(tag)) {
                map.put(TAG, tag);
            }
            map.put(MESSAGE, matcher.group(5)); // NOSONAR
        }
        catch (Exception ex) {
            logger.debug( "Could not decode: " + payload,ex);
            map.clear();
            map.put(UNDECODED, payload);
        }
    }

    private void parseTimestampToMap(String timestamp, Map<String, Object> map) {
        try {
            LocalDate localDate = this.dateTimeFormatter.parse(timestamp, LocalDate::from);
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            calendar.setTime(Date.valueOf(localDate));
            /*
             * syslog date doesn't include a year so we
             * need to insert the current year - adjusted
             * if necessary if close to midnight on Dec 31.
             */
            if (month == Calendar.DECEMBER && calendar.get(Calendar.MONTH) == Calendar.JANUARY) {
                calendar.set(Calendar.YEAR, year + 1);
            }
            else if (month == Calendar.JANUARY && calendar.get(Calendar.MONTH) == Calendar.FEBRUARY) {
                calendar.set(Calendar.YEAR, year - 1);
            }
            else {
                calendar.set(Calendar.YEAR, year);
            }
            map.put(TIMESTAMP, calendar.getTime());
        }
        catch (@SuppressWarnings("unused") Exception e) {
            map.put(TIMESTAMP, timestamp);
        }
    }
}
