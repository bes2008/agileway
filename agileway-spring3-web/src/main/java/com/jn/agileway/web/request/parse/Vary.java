package com.jn.agileway.web.request.parse;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.Set;

public class Vary {
    private Vary() {
        // Utility class. Hide default constructor.
    }


    public static void parseVary(StringReader input, Set<String> result) throws IOException {

        do {
            String fieldName = HttpParser.readToken(input);
            if (fieldName == null) {
                // Invalid field-name, skip to the next one
                HttpParser.skipUntil(input, 0, ',');
                continue;
            }

            if (fieldName.length() == 0) {
                // No more data to read
                break;
            }

            SkipResult skipResult = HttpParser.skipConstant(input, ",");
            if (skipResult == SkipResult.EOF) {
                // EOF
                result.add(fieldName.toLowerCase(Locale.ENGLISH));
                break;
            } else if (skipResult == SkipResult.FOUND) {
                result.add(fieldName.toLowerCase(Locale.ENGLISH));
                continue;
            } else {
                // Not a token - ignore it
                HttpParser.skipUntil(input, 0, ',');
                continue;
            }
        } while (true);
    }
}
