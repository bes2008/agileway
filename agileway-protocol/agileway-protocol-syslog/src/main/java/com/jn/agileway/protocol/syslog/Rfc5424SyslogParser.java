package com.jn.agileway.protocol.syslog;


import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Parse for RFC 5424 syslog messages; when used with TCP, requires the use
 * of a {@code RFC6587SyslogDeserializer} which decodes the framing.
 *
 *
 * @since 4.1.0
 *
 */
public class Rfc5424SyslogParser {

    protected static final char NILVALUE = '-';

    protected static final char SPACE = ' ';

    protected boolean retainOriginal;


    /**
     * Construct a default parser; do not retain the original message content unless there
     * is an error.
     */
    public Rfc5424SyslogParser() {
        this(false);
    }

    /**
     * @param retainOriginal when true, include the original message content intact in the
     * map.
     */
    public Rfc5424SyslogParser(boolean retainOriginal) {
        this.retainOriginal = retainOriginal;
    }

    public Map<String, ?> parse(String line, int octetCount, boolean shortRead) {
        Map<String, Object> map = new LinkedHashMap<>();
        Reader r = new Reader(line);

        try {
            if (shortRead) {
                int n = line.length() - 1;
                while (n >= 0 && line.charAt(n) == 0x00) {
                    n--;
                }
                line = line.substring(0, n);
                throw new IllegalStateException("Insufficient data; expected " + octetCount + " got " + (n + 1));
            }
            r.expect('<');
            int pri = r.readInt();
            r.expect('>');

            int version = r.readInt();
            r.expect(SPACE);

            Object timestamp = getTimestamp(r);

            String host = r.getIdentifier();
            String app = r.getIdentifier();
            String procId = r.getIdentifier();
            String msgId = r.getIdentifier();

            Object structuredData = getStructuredData(r);

            String message;
            if (r.is(SPACE)) {
                r.getc();
                message = r.rest();
            }
            else {
                message = "";
            }

            int severity = pri & 0x7; // NOSONAR magic number
            int facility = pri >> 3; // NOSONAR magic number
            map.put(SyslogHeaders.FACILITY, facility);
            map.put(SyslogHeaders.SEVERITY, severity);
            map.put(SyslogHeaders.SEVERITY_TEXT, Severity.parseInt(severity).label());
            map.put(SyslogHeaders.VERSION, version);
            map.put(SyslogHeaders.MESSAGE, message);
            map.put(SyslogHeaders.DECODE_ERRORS, "false");

            if(Objs.isNotNull(timestamp)){
                map.put(SyslogHeaders.TIMESTAMP, timestamp);
            }
            if(Objs.isNotEmpty(host)){
                map.put(SyslogHeaders.HOST, host);
            }
            if(Objs.isNotEmpty(app)){
                map.put(SyslogHeaders.APP_NAME, app);
            }
            if(Objs.isNotEmpty(procId)){
                map.put(SyslogHeaders.PROCID, procId);
            }
            if(Objs.isNotEmpty(msgId)){
                map.put(SyslogHeaders.MSGID, msgId);
            }
            if(Objs.isNotEmpty(structuredData)){
                map.put(SyslogHeaders.STRUCTURED_DATA, structuredData);
            }
            if(this.retainOriginal && Objs.isNotEmpty(line)){
                map.put(SyslogHeaders.UNDECODED, line);
            }
        }
        catch (IllegalStateException | StringIndexOutOfBoundsException ex) {
            map.put(SyslogHeaders.DECODE_ERRORS, "true");
            map.put(SyslogHeaders.ERRORS,
                    (ex instanceof StringIndexOutOfBoundsException ? "Unexpected end of message: " : "") // NOSONAR
                            + ex.getMessage());
            map.put(SyslogHeaders.UNDECODED, line);
        }
        return map;
    }

    /**
     * Default implementation returns the date as a String (if present).
     * @param r the reader.
     * @return the timestamp.
     */
    protected Object getTimestamp(Reader r) {

        int c = r.getc();

        if (c == NILVALUE) {
            return null;
        }

        if (!Character.isDigit(c)) {
            throw new IllegalStateException("Year expected @" + r.getIndex());
        }

        StringBuilder dateBuilder = new StringBuilder();
        dateBuilder.append((char) c);
        while ((c = r.getc()) != SPACE) {
            dateBuilder.append((char) c);
        }

        return dateBuilder.toString();
    }

    private Object getStructuredData(Reader r) {
        if (r.is(NILVALUE)) {
            r.getc();
            return null;
        }
        return parseStructuredDataElements(r);
    }

    /**
     * Default implementation returns a list of structured data elements with
     * no internal parsing.
     * @param r the reader.
     * @return the structured data.
     */
    protected Object parseStructuredDataElements(Reader r) {
        List<String> fragments = new ArrayList<>();
        while (r.is('[')) {
            r.mark();
            r.skipTo(']');
            fragments.add(r.getMarkedSegment());
        }
        return fragments;
    }

    protected static class Reader {

        private final String line;

        private int idx;

        private int mark;

        public Reader(String l) {
            this.line = l;
        }

        public int getIndex() {
            return this.idx;
        }

        public void mark() {
            this.mark = this.idx;
        }

        public String getMarkedSegment() {
            Preconditions.checkState(this.mark <= this.idx, "mark is greater than this.idx");
            return this.line.substring(this.mark, this.idx);
        }

        public int current() {
            return this.line.charAt(this.idx);
        }

        public int prev() {
            return this.line.charAt(this.idx - 1);
        }

        public int getc() {
            return this.line.charAt(this.idx++);
        }

        public int peek() {
            return this.line.charAt(this.idx + 1);
        }

        public void ungetc() {
            this.idx--;
        }

        public int getInt() {
            int c = getc();
            if (!Character.isDigit(c)) {
                ungetc();
                return -1;
            }

            return c - '0';
        }

        /**
         * Read characters building an int until a non-digit is found
         * @return int
         */
        public int readInt() {
            int val = 0;
            while (isDigit()) {
                val = (val * 10) + getInt(); // NOSONAR magic number
            }
            return val;
        }

        public double readFraction() {
            int val = 0;
            int order = 0;
            while (isDigit()) {
                val = (val * 10) + getInt(); // NOSONAR magic number
                order *= 10; // NOSONAR magic number
            }
            return (double) val / order;

        }

        public boolean is(char c) {
            return this.line.charAt(this.idx) == c;
        }

        public boolean was(char c) {
            return this.line.charAt(this.idx - 1) == c;
        }

        public boolean isDigit() {
            return Character.isDigit(this.line.charAt(this.idx));
        }

        public void expect(char c) {
            if (this.line.charAt(this.idx++) != c) {
                throw new IllegalStateException("Expected '" + c + "' @" + this.idx);
            }
        }

        public void skipTo(char searchChar) {
            while (!is(searchChar) || was('\\')) {
                getc();
            }
            getc();
        }

        public String rest() {
            return this.line.substring(this.idx);
        }

        public String getIdentifier() {
            StringBuilder sb = new StringBuilder();
            int c;
            while (true) {
                c = getc();
                if (c >= 33 && c <= 127) { // NOSONAR magic number
                    sb.append((char) c);
                }
                else {
                    break;
                }
            }
            return sb.toString();
        }

    }

    protected enum Severity {

        DEBUG(7, "DEBUG"),

        INFO(6, "INFO"),

        NOTICE(5, "NOTICE"),

        WARN(4, "WARN"),

        ERROR(3, "ERRORS"),

        CRITICAL(2, "CRITICAL"),

        ALERT(1, "ALERT"),

        EMERGENCY(0, "EMERGENCY"),

        UNDEFINED(-1, "UNDEFINED");

        private final int level;

        private final String label;

        Severity(int level, String label) {
            this.level = level;
            this.label = label;
        }

        public int level() {
            return this.level;
        }

        public String label() {
            return this.label;
        }

        public static Severity parseInt(int syslogSeverity) {
            if (syslogSeverity == 7) { // NOSONAR magic number
                return DEBUG;
            }
            if (syslogSeverity == 6) { // NOSONAR magic number
                return INFO;
            }
            if (syslogSeverity == 5) { // NOSONAR magic number
                return NOTICE;
            }
            if (syslogSeverity == 4) { // NOSONAR magic number
                return WARN;
            }
            if (syslogSeverity == 3) { // NOSONAR magic number
                return ERROR;
            }
            if (syslogSeverity == 2) {
                return CRITICAL;
            }
            if (syslogSeverity == 1) {
                return ALERT;
            }
            if (syslogSeverity == 0) {
                return EMERGENCY;
            }
            return UNDEFINED;
        }

    }


}
