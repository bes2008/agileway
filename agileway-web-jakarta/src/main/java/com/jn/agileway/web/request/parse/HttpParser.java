package com.jn.agileway.web.request.parse;


import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;


/**
 * HTTP header value parser implementation. Parsing HTTP headers as per RFC2616
 * is not always as simple as it first appears. For headers that only use tokens
 * the simple approach will normally be sufficient. However, for the other
 * headers, while simple code meets 99.9% of cases, there are often some edge
 * cases that make things far more complicated.
 *
 * The purpose of this parser is to let the parser worry about the edge cases.
 * It provides tolerant (where safe to do so) parsing of HTTP header values
 * assuming that wrapped header lines have already been unwrapped. (The Tomcat
 * header processing code does the unwrapping.)
 *
 */
public class HttpParser {


    private static final Logger log = Loggers.getLogger(HttpParser.class);

    private static final int ARRAY_SIZE = 128;

    private static final boolean[] IS_CONTROL = new boolean[ARRAY_SIZE];
    private static final boolean[] IS_SEPARATOR = new boolean[ARRAY_SIZE];
    private static final boolean[] IS_TOKEN = new boolean[ARRAY_SIZE];
    private static final boolean[] IS_HEX = new boolean[ARRAY_SIZE];
    private static final boolean[] IS_HTTP_PROTOCOL = new boolean[ARRAY_SIZE];
    private static final boolean[] IS_ALPHA = new boolean[ARRAY_SIZE];
    private static final boolean[] IS_NUMERIC = new boolean[ARRAY_SIZE];
    private static final boolean[] REQUEST_TARGET_ALLOW = new boolean[ARRAY_SIZE];
    private static final boolean[] IS_UNRESERVED = new boolean[ARRAY_SIZE];
    private static final boolean[] IS_SUBDELIM = new boolean[ARRAY_SIZE];
    private static final boolean[] IS_USERINFO = new boolean[ARRAY_SIZE];
    private static final boolean[] IS_RELAXABLE = new boolean[ARRAY_SIZE];

    private static final HttpParser DEFAULT;


    static {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            // Control> 0-31, 127
            if (i < 32 || i == 127) {
                IS_CONTROL[i] = true;
            }

            // Separator
            if (    i == '(' || i == ')' || i == '<' || i == '>'  || i == '@'  ||
                    i == ',' || i == ';' || i == ':' || i == '\\' || i == '\"' ||
                    i == '/' || i == '[' || i == ']' || i == '?'  || i == '='  ||
                    i == '{' || i == '}' || i == ' ' || i == '\t') {
                IS_SEPARATOR[i] = true;
            }

            // Token: Anything 0-127 that is not a control and not a separator
            if (!IS_CONTROL[i] && !IS_SEPARATOR[i] && i < 128) {
                IS_TOKEN[i] = true;
            }

            // Hex: 0-9, a-f, A-F
            if ((i >= '0' && i <='9') || (i >= 'a' && i <= 'f') || (i >= 'A' && i <= 'F')) {
                IS_HEX[i] = true;
            }

            // Not valid for HTTP protocol
            // "HTTP/" DIGIT "." DIGIT
            if (i == 'H' || i == 'T' || i == 'P' || i == '/' || i == '.' || (i >= '0' && i <= '9')) {
                IS_HTTP_PROTOCOL[i] = true;
            }

            if (i >= '0' && i <= '9') {
                IS_NUMERIC[i] = true;
            }

            if (i >= 'a' && i <= 'z' || i >= 'A' && i <= 'Z') {
                IS_ALPHA[i] = true;
            }

            if (IS_ALPHA[i] || IS_NUMERIC[i] || i == '-' || i == '.' || i == '_' || i == '~') {
                IS_UNRESERVED[i] = true;
            }

            if (i == '!' || i == '$' || i == '&' || i == '\'' || i == '(' || i == ')' || i == '*' ||
                    i == '+' || i == ',' || i == ';' || i == '=') {
                IS_SUBDELIM[i] = true;
            }

            // userinfo    = *( unreserved / pct-encoded / sub-delims / ":" )
            if (IS_UNRESERVED[i] || i == '%' || IS_SUBDELIM[i] || i == ':') {
                IS_USERINFO[i] = true;
            }

            // The characters that are normally not permitted for which the
            // restrictions may be relaxed when used in the path and/or query
            // string
            if (i == '\"' || i == '<' || i == '>' || i == '[' || i == '\\' || i == ']' ||
                    i == '^' || i == '`'  || i == '{' || i == '|' || i == '}') {
                IS_RELAXABLE[i] = true;
            }
        }

        String prop = System.getProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow");
        if (prop != null) {
            for (int i = 0; i < prop.length(); i++) {
                char c = prop.charAt(i);
                if (c == '{' || c == '}' || c == '|') {
                    REQUEST_TARGET_ALLOW[c] = true;
                } else {
                    String error = "Character [{0}] is not allowed and will continue to be rejected.";
                    StringTemplates.formatWithIndex(error, Character.valueOf(c));
                    log.warn(error);
                }
            }
        }

        DEFAULT = new HttpParser(null, null);
    }


    private final boolean[] IS_NOT_REQUEST_TARGET = new boolean[ARRAY_SIZE];
    private final boolean[] IS_ABSOLUTEPATH_RELAXED = new boolean[ARRAY_SIZE];
    private final boolean[] IS_QUERY_RELAXED = new boolean[ARRAY_SIZE];


    public HttpParser(String relaxedPathChars, String relaxedQueryChars) {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            // Not valid for request target.
            // Combination of multiple rules from RFC7230 and RFC 3986. Must be
            // ASCII, no controls plus a few additional characters excluded
            if (IS_CONTROL[i] || i > 127 ||
                    i == ' ' || i == '\"' || i == '#' || i == '<' || i == '>' || i == '\\' ||
                    i == '^' || i == '`'  || i == '{' || i == '|' || i == '}') {
                if (!REQUEST_TARGET_ALLOW[i]) {
                    IS_NOT_REQUEST_TARGET[i] = true;
                }
            }

            /*
             * absolute-path  = 1*( "/" segment )
             * segment        = *pchar
             * pchar          = unreserved / pct-encoded / sub-delims / ":" / "@"
             *
             * Note pchar allows everything userinfo allows plus "@"
             */
            if (IS_USERINFO[i] || i == '@' || i == '/' || REQUEST_TARGET_ALLOW[i]) {
                IS_ABSOLUTEPATH_RELAXED[i] = true;
            }

            /*
             * query          = *( pchar / "/" / "?" )
             *
             * Note query allows everything absolute-path allows plus "?"
             */
            if (IS_ABSOLUTEPATH_RELAXED[i] || i == '?' || REQUEST_TARGET_ALLOW[i]) {
                IS_QUERY_RELAXED[i] = true;
            }
        }

        relax(IS_ABSOLUTEPATH_RELAXED, relaxedPathChars);
        relax(IS_QUERY_RELAXED, relaxedQueryChars);
    }


    public boolean isNotRequestTargetRelaxed(int c) {
        // Fast for valid request target characters, slower for some incorrect
        // ones
        try {
            return IS_NOT_REQUEST_TARGET[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return true;
        }
    }


    public boolean isAbsolutePathRelaxed(int c) {
        // Fast for valid user info characters, slower for some incorrect
        // ones
        try {
            return IS_ABSOLUTEPATH_RELAXED[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }


    public boolean isQueryRelaxed(int c) {
        // Fast for valid user info characters, slower for some incorrect
        // ones
        try {
            return IS_QUERY_RELAXED[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }


    public static String unquote(String input) {
        if (input == null || input.length() < 2) {
            return input;
        }

        int start;
        int end;

        // Skip surrounding quotes if there are any
        if (input.charAt(0) == '"') {
            start = 1;
            end = input.length() - 1;
        } else {
            start = 0;
            end = input.length();
        }

        StringBuilder result = new StringBuilder();
        for (int i = start ; i < end; i++) {
            char c = input.charAt(i);
            if (input.charAt(i) == '\\') {
                i++;
                result.append(input.charAt(i));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }


    public static boolean isToken(int c) {
        // Fast for correct values, slower for incorrect ones
        try {
            return IS_TOKEN[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }


    public static boolean isHex(int c) {
        // Fast for correct values, slower for some incorrect ones
        try {
            return IS_HEX[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }


    public static boolean isNotRequestTarget(int c) {
        return DEFAULT.isNotRequestTargetRelaxed(c);
    }


    public static boolean isHttpProtocol(int c) {
        // Fast for valid HTTP protocol characters, slower for some incorrect
        // ones
        try {
            return IS_HTTP_PROTOCOL[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }


    public static boolean isAlpha(int c) {
        // Fast for valid alpha characters, slower for some incorrect
        // ones
        try {
            return IS_ALPHA[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }


    public static boolean isNumeric(int c) {
        // Fast for valid numeric characters, slower for some incorrect
        // ones
        try {
            return IS_NUMERIC[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }


    public static boolean isUserInfo(int c) {
        // Fast for valid user info characters, slower for some incorrect
        // ones
        try {
            return IS_USERINFO[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }


    private static boolean isRelaxable(int c) {
        // Fast for valid user info characters, slower for some incorrect
        // ones
        try {
            return IS_RELAXABLE[c];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }


    public static boolean isAbsolutePath(int c) {
        return DEFAULT.isAbsolutePathRelaxed(c);
    }


    public static boolean isQuery(int c) {
        return DEFAULT.isQueryRelaxed(c);
    }


    // Skip any LWS and position to read the next character. The next character
    // is returned as being able to 'peek()' it allows a small optimisation in
    // some cases.
    static int skipLws(Reader input) throws IOException {

        input.mark(1);
        int c = input.read();

        while (c == 32 || c == 9 || c == 10 || c == 13) {
            input.mark(1);
            c = input.read();
        }

        input.reset();
        return c;
    }

    static SkipResult skipConstant(Reader input, String constant) throws IOException {
        int len = constant.length();

        skipLws(input);
        input.mark(len);
        int c = input.read();

        for (int i = 0; i < len; i++) {
            if (i == 0 && c == -1) {
                return SkipResult.EOF;
            }
            if (c != constant.charAt(i)) {
                input.reset();
                return SkipResult.NOT_FOUND;
            }
            if (i != (len - 1)) {
                c = input.read();
            }
        }
        return SkipResult.FOUND;
    }

    /**
     * @return  the token if one was found, the empty string if no data was
     *          available to read or <code>null</code> if data other than a
     *          token was found
     */
    static String readToken(Reader input) throws IOException {
        StringBuilder result = new StringBuilder();

        skipLws(input);
        input.mark(1);
        int c = input.read();

        while (c != -1 && isToken(c)) {
            result.append((char) c);
            input.mark(1);
            c = input.read();
        }
        // Use mark(1)/reset() rather than skip(-1) since skip() is a NOP
        // once the end of the String has been reached.
        input.reset();

        if (c != -1 && result.length() == 0) {
            return null;
        } else {
            return result.toString();
        }
    }

    /**
     * @return  the digits if any were found, the empty string if no data was
     *          found or if data other than digits was found
     */
    static String readDigits(Reader input) throws IOException {
        StringBuilder result = new StringBuilder();

        skipLws(input);
        input.mark(1);
        int c = input.read();

        while (c != -1 && isNumeric(c)) {
            result.append((char) c);
            input.mark(1);
            c = input.read();
        }
        // Use mark(1)/reset() rather than skip(-1) since skip() is a NOP
        // once the end of the String has been reached.
        input.reset();

        return result.toString();
    }

    /**
     * @return  the number if digits were found, -1 if no data was found
     *          or if data other than digits was found
     */
    static long readLong(Reader input) throws IOException {
        String digits = readDigits(input);

        if (digits.length() == 0) {
            return -1;
        }

        return Long.parseLong(digits);
    }

    /**
     * @return the quoted string if one was found, null if data other than a
     *         quoted string was found or null if the end of data was reached
     *         before the quoted string was terminated
     */
    static String readQuotedString(Reader input, boolean returnQuoted) throws IOException {

        skipLws(input);
        int c = input.read();

        if (c != '"') {
            return null;
        }

        StringBuilder result = new StringBuilder();
        if (returnQuoted) {
            result.append('\"');
        }
        c = input.read();

        while (c != '"') {
            if (c == -1) {
                return null;
            } else if (c == '\\') {
                c = input.read();
                if (returnQuoted) {
                    result.append('\\');
                }
                result.append((char) c);
            } else {
                result.append((char) c);
            }
            c = input.read();
        }
        if (returnQuoted) {
            result.append('\"');
        }

        return result.toString();
    }

    static String readTokenOrQuotedString(Reader input, boolean returnQuoted)
            throws IOException {

        // Peek at next character to enable correct method to be called
        int c = skipLws(input);

        if (c == '"') {
            return readQuotedString(input, returnQuoted);
        } else {
            return readToken(input);
        }
    }

    /**
     * Token can be read unambiguously with or without surrounding quotes so
     * this parsing method for token permits optional surrounding double quotes.
     * This is not defined in any RFC. It is a special case to handle data from
     * buggy clients (known buggy clients for DIGEST auth include Microsoft IE 8
     * &amp; 9, Apple Safari for OSX and iOS) that add quotes to values that
     * should be tokens.
     *
     * @return the token if one was found, null if data other than a token or
     *         quoted token was found or null if the end of data was reached
     *         before a quoted token was terminated
     */
    static String readQuotedToken(Reader input) throws IOException {

        StringBuilder result = new StringBuilder();
        boolean quoted = false;

        skipLws(input);
        input.mark(1);
        int c = input.read();

        if (c == '"') {
            quoted = true;
        } else if (c == -1 || !isToken(c)) {
            return null;
        } else {
            result.append((char) c);
        }
        input.mark(1);
        c = input.read();

        while (c != -1 && isToken(c)) {
            result.append((char) c);
            input.mark(1);
            c = input.read();
        }

        if (quoted) {
            if (c != '"') {
                return null;
            }
        } else {
            // Use mark(1)/reset() rather than skip(-1) since skip() is a NOP
            // once the end of the String has been reached.
            input.reset();
        }

        if (c != -1 && result.length() == 0) {
            return null;
        } else {
            return result.toString();
        }
    }

    /**
     * LHEX can be read unambiguously with or without surrounding quotes so this
     * parsing method for LHEX permits optional surrounding double quotes. Some
     * buggy clients (libwww-perl for DIGEST auth) are known to send quoted LHEX
     * when the specification requires just LHEX.
     *
     * <p>
     * LHEX are, literally, lower-case hexadecimal digits. This implementation
     * allows for upper-case digits as well, converting the returned value to
     * lower-case.
     *
     * @return  the sequence of LHEX (minus any surrounding quotes) if any was
     *          found, or <code>null</code> if data other LHEX was found
     */
    static String readLhex(Reader input) throws IOException {

        StringBuilder result = new StringBuilder();
        boolean quoted = false;

        skipLws(input);
        input.mark(1);
        int c = input.read();

        if (c == '"') {
            quoted = true;
        } else if (c == -1 || !isHex(c)) {
            return null;
        } else {
            if ('A' <= c && c <= 'F') {
                c -= ('A' - 'a');
            }
            result.append((char) c);
        }
        input.mark(1);
        c = input.read();

        while (c != -1 && isHex(c)) {
            if ('A' <= c && c <= 'F') {
                c -= ('A' - 'a');
            }
            result.append((char) c);
            input.mark(1);
            c = input.read();
        }

        if (quoted) {
            if (c != '"') {
                return null;
            }
        } else {
            // Use mark(1)/reset() rather than skip(-1) since skip() is a NOP
            // once the end of the String has been reached.
            input.reset();
        }

        if (c != -1 && result.length() == 0) {
            return null;
        } else {
            return result.toString();
        }
    }

    static double readWeight(Reader input, char delimiter) throws IOException {
        skipLws(input);
        int c = input.read();
        if (c == -1 || c == delimiter) {
            // No q value just whitespace
            return 1;
        } else if (c != 'q') {
            // Malformed. Use quality of zero so it is dropped.
            skipUntil(input, c, delimiter);
            return 0;
        }
        // RFC 7231 does not allow whitespace here but be tolerant
        skipLws(input);
        c = input.read();
        if (c != '=') {
            // Malformed. Use quality of zero so it is dropped.
            skipUntil(input, c, delimiter);
            return 0;
        }

        // RFC 7231 does not allow whitespace here but be tolerant
        skipLws(input);
        c = input.read();

        // Should be no more than 3 decimal places
        StringBuilder value = new StringBuilder(5);
        int decimalPlacesRead = -1;

        if (c == '0' || c == '1') {
            value.append((char) c);
            c = input.read();

            while (true) {
                if (decimalPlacesRead == -1 && c == '.') {
                    value.append('.');
                    decimalPlacesRead = 0;
                } else if (decimalPlacesRead > -1 && c >= '0' && c <= '9') {
                    if (decimalPlacesRead < 3) {
                        value.append((char) c);
                        decimalPlacesRead++;
                    }
                } else {
                    break;
                }
                c = input.read();
            }
        } else {
            // Malformed. Use quality of zero so it is dropped and skip until
            // EOF or the next delimiter
            skipUntil(input, c, delimiter);
            return 0;
        }

        if (c == 9 || c == 32) {
            skipLws(input);
            c = input.read();
        }

        // Must be at delimiter or EOF
        if (c != delimiter && c != -1) {
            // Malformed. Use quality of zero so it is dropped and skip until
            // EOF or the next delimiter
            skipUntil(input, c, delimiter);
            return 0;
        }

        double result = Double.parseDouble(value.toString());
        if (result > 1) {
            return 0;
        }
        return result;
    }


    /**
     * @return If inIPv6 is false, the position of ':' that separates the host
     *         from the port or -1 if it is not present. If inIPv6 is true, the
     *         number of characters read
     */
    static int readHostIPv4(Reader reader, boolean inIPv6) throws IOException {
        int octet = -1;
        int octetCount = 1;
        int c;
        int pos = 0;

        // readAheadLimit doesn't matter as all the readers passed to this
        // method buffer the entire content.
        reader.mark(1);
        do {
            c = reader.read();
            if (c == '.') {
                if (octet > -1 && octet < 256) {
                    // Valid
                    octetCount++;
                    octet = -1;
                } else if (inIPv6 || octet == -1) {
                    String error = "Invalid octet [{0}]. The valid range for IPv4 octets is 0 to 255.";
                    error = StringTemplates.formatWithIndex(error,Integer.toString(octet));
                    throw new IllegalArgumentException(error);
                } else {
                    // Might not be an IPv4 address. Could be a host / FQDN with
                    // a fully numeric component.
                    reader.reset();
                    return readHostDomainName(reader);
                }
            } else if (isNumeric(c)) {
                if (octet == -1) {
                    octet = c - '0';
                } else if (octet == 0) {
                    // Leading zero in non-zero octet. Not valid (ambiguous).
                    if (inIPv6) {
                        throw new IllegalArgumentException("An non-zero IPv4 octet may not contain a leading zero.");
                    } else {
                        // Could be a host/FQDN
                        reader.reset();
                        return readHostDomainName(reader);
                    }
                } else {
                    octet = octet * 10 + c - '0';
                }
            } else if (c == ':') {
                break;
            } else if (c == -1) {
                if (inIPv6) {
                    throw new IllegalArgumentException("The IPv6 address is missing a closing bracket.");
                } else {
                    pos = -1;
                    break;
                }
            } else if (c == ']') {
                if (inIPv6) {
                    pos++;
                    break;
                } else {
                    throw new IllegalArgumentException("A closing bracket ']' was found in a non-IPv6 host name.");
                }
            } else if (!inIPv6 && (isAlpha(c) || c == '-')) {
                // Go back to the start and parse as a host / FQDN
                reader.reset();
                return readHostDomainName(reader);
            } else {
                String error = "The character [{0}] is never valid in an IPv4 address.";
                error = StringTemplates.formatWithIndex(error, Character.toString((char) c));
                throw new IllegalArgumentException(error);
            }
            pos++;
        } while (true);

        if (octetCount != 4 || octet < 0 || octet > 255) {
            // Might not be an IPv4 address. Could be a host name or a FQDN with
            // fully numeric components. Go back to the start and parse as a
            // host / FQDN.
            reader.reset();
            return readHostDomainName(reader);
        }

        return pos;
    }


    /**
     * @return The position of ':' that separates the host from the port or -1
     *         if it is not present
     */
    static int readHostIPv6(Reader reader) throws IOException {
        // Must start with '['
        int c = reader.read();
        if (c != '[') {
            throw new IllegalArgumentException("The IPv6 address is missing an opening bracket.");
        }

        int h16Count = 0;
        int h16Size = 0;
        int pos = 1;
        boolean parsedDoubleColon = false;
        int precedingColonsCount = 0;

        do {
            c = reader.read();
            if (h16Count == 0 && precedingColonsCount == 1 && c != ':') {
                // Can't start with a single :
                throw new IllegalArgumentException("An IPv6 address may not start with a single ':'.");
            }
            if (HttpParser.isHex(c)) {
                if (h16Size == 0) {
                    // Start of a new h16 block
                    precedingColonsCount = 0;
                    h16Count++;
                }
                h16Size++;
                if (h16Size > 4) {
                    throw new IllegalArgumentException("Invalid hextet. A hextet must consist of 4 or less hex characters.");
                }
            } else if (c == ':') {
                if (precedingColonsCount >=2 ) {
                    // ::: is not allowed
                    throw new IllegalArgumentException("An IPv6 address may not contain more than 2 sequential colon characters.");
                } else {
                    if(precedingColonsCount == 1) {
                        // End of ::
                        if (parsedDoubleColon ) {
                            // Only allowed one :: sequence
                            throw new IllegalArgumentException("An IPv6 address may only contain a single '::' sequence.");
                        }
                        parsedDoubleColon = true;
                        // :: represents at least one h16 block
                        h16Count++;
                    }
                    precedingColonsCount++;
                    // mark if the next symbol is hex before the actual read
                    reader.mark(4);
                }
                h16Size = 0;
            } else if (c == ']') {
                if (precedingColonsCount == 1) {
                    // Can't end on a single ':'
                    throw new IllegalArgumentException("An IPv6 address may not end with a single ':'.");
                }
                pos++;
                break;
            } else if (c == '.') {
                if (h16Count == 7 || h16Count < 7 && parsedDoubleColon) {
                    reader.reset();
                    pos -= h16Size;
                    pos += readHostIPv4(reader, true);
                    h16Count++;
                    break;
                } else {
                    throw new IllegalArgumentException("The IPv6 address contains an embedded IPv4 address at an invalid location.");
                }
            } else {

                throw new IllegalArgumentException(StringTemplates.formatWithIndex("The character [{0}] is never valid in an IPv6 address.", Character.toString((char) c)));
            }
            pos++;
        } while (true);

        if (h16Count > 8) {
            String error = "The IPv6 address contains [{0}] hextets but a valid IPv6 address may not have more than 8.";
            error = StringTemplates.formatWithIndex(error, Integer.toString(h16Count));
            throw new IllegalArgumentException(error);
        } else if (h16Count != 8 && !parsedDoubleColon) {
            String error = "An IPv6 address must consist of 8 hextets but this address contains [{0}] hextets and no ''::'' sequence to represent one or more zero hextets.";
            error = StringTemplates.formatWithIndex(error, Integer.toString(h16Count));
            throw new IllegalArgumentException(error);
        }

        c = reader.read();
        if (c == ':') {
            return pos;
        } else {
            if(c == -1) {
                return -1;
            }
            String error = "The character [{0}] is not permitted to follow an IPv6 address in a host name";
            error = StringTemplates.formatWithIndex(error, Character.toString((char) c));
            throw new IllegalArgumentException(error);
        }
    }

    /**
     * @return The position of ':' that separates the host from the port or -1
     *         if it is not present
     */
    static int readHostDomainName(Reader reader) throws IOException {
        DomainParseState state = DomainParseState.NEW;
        int pos = 0;

        while (state.mayContinue()) {
            state = state.next(reader.read());
            pos++;
        }

        if (DomainParseState.COLON == state) {
            // State identifies the state of the previous character
            return pos - 1;
        } else {
            return -1;
        }
    }


    /**
     * Skips all characters until EOF or the specified target is found. Normally
     * used to skip invalid input until the next separator.
     */
    static SkipResult skipUntil(Reader input, int c, char target) throws IOException {
        while (c != -1 && c != target) {
            c = input.read();
        }
        if (c == -1) {
            return SkipResult.EOF;
        } else {
            return SkipResult.FOUND;
        }
    }


    private void relax(boolean[] flags, String relaxedChars) {
        if (relaxedChars != null && relaxedChars.length() > 0) {
            char[] chars = relaxedChars.toCharArray();
            for (char c : chars) {
                if (isRelaxable(c)) {
                    flags[c] = true;
                    IS_NOT_REQUEST_TARGET[c] = false;
                }
            }
        }
    }


    private enum DomainParseState {
        NEW(     true, false, false, false, " at the start of"),
        ALPHA(   true,  true,  true,  true, " after a letter in"),
        NUMERIC( true,  true,  true,  true, " after a number in"),
        PERIOD(  true, false, false,  true, " after a period in"),
        HYPHEN(  true,  true, false, false, " after a hypen in"),
        COLON(  false, false, false, false, " after a colon in"),
        END(    false, false, false, false, " at the end of");

        private final boolean mayContinue;
        private final boolean allowsHyphen;
        private final boolean allowsPeriod;
        private final boolean allowsEnd;
        private final String errorLocation;

        private DomainParseState(boolean mayContinue, boolean allowsHyphen, boolean allowsPeriod,
                                 boolean allowsEnd, String errorLocation) {
            this.mayContinue = mayContinue;
            this.allowsHyphen = allowsHyphen;
            this.allowsPeriod = allowsPeriod;
            this.allowsEnd = allowsEnd;
            this.errorLocation = errorLocation;
        }

        public boolean mayContinue() {
            return mayContinue;
        }

        public DomainParseState next(int c) {
            if (c == -1) {
                if (allowsEnd) {
                    return END;
                } else {
                    String error = "The state [{0}] is not valid for the end of a segment.";
                    error = StringTemplates.formatWithIndex(error, this.name());
                    throw new IllegalArgumentException(error);
                }
            } else if (HttpParser.isAlpha(c)) {
                return ALPHA;
            } else if (HttpParser.isNumeric(c)) {
                return NUMERIC;
            } else if (c == '.') {
                if (allowsPeriod) {
                    return PERIOD;
                } else {
                    String error = "The character [{0}] is not valid{1} a domain name.";
                    error = StringTemplates.formatWithIndex(error,  Character.toString((char) c), errorLocation);
                    throw new IllegalArgumentException(error);
                }
            } else if (c == ':') {
                if (allowsEnd) {
                    return COLON;
                } else {
                    String error = "The character [{0}] is not valid{1} a domain name.";
                    error = StringTemplates.formatWithIndex(error,  Character.toString((char) c), errorLocation);
                    throw new IllegalArgumentException(error);
                }
            } else if (c == '-') {
                if (allowsHyphen) {
                    return HYPHEN;
                } else {
                    String error = "The character [{0}] is not valid{1} a domain name.";
                    error = StringTemplates.formatWithIndex(error,  Character.toString((char) c), errorLocation);
                    throw new IllegalArgumentException(error);
                }
            } else {
                String error = "The character [{0}] is never valid in a domain name.";
                error = StringTemplates.formatWithIndex(error,  Character.toString((char) c));
                throw new IllegalArgumentException(error);
            }
        }
    }
}
