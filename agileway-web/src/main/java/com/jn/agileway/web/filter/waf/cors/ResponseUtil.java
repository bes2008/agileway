package com.jn.agileway.web.filter.waf.cors;


import com.jn.agileway.web.request.parse.Vary;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;


class ResponseUtil {

    private static final String VARY_HEADER = "vary";
    private static final String VARY_ALL = "*";

    private ResponseUtil() {
        // Utility class. Hide default constructor.
    }




    public static void addVaryFieldName(HttpServletResponse response, String name) {
        addVaryFieldName(new ResponseAdapter(response), name);
    }


    private static void addVaryFieldName(Adapter adapter, String name) {

        Collection<String> varyHeaders = adapter.getHeaders(VARY_HEADER);

        // Short-cut if only * has been set
        if (varyHeaders.size() == 1 && varyHeaders.iterator().next().trim().equals(VARY_ALL)) {
            // No need to add an additional field
            return;
        }

        // Short-cut if no headers have been set
        if (varyHeaders.size() == 0) {
            adapter.addHeader(VARY_HEADER, name);
            return;
        }

        // Short-cut if "*" is added
        if (VARY_ALL.equals(name.trim())) {
            adapter.setHeader(VARY_HEADER, VARY_ALL);
            return;
        }

        // May be dealing with an application set header, or multiple headers.
        // Header names overlap so can't use String.contains(). Have to parse
        // the existing values, check if the new value is already present and
        // then add it if not. The good news is field names are tokens which
        // makes parsing simpler.
        Set<String> fieldNames = new HashSet<>();

        for (String varyHeader : varyHeaders) {
            StringReader input = new StringReader(varyHeader);
            try {
                Vary.parseVary(input, fieldNames);
            } catch (IOException ioe) {
                // Should never happen
            }
        }

        if (fieldNames.contains(VARY_ALL)) {
            // '*' has been added without removing other values. Optimise.
            adapter.setHeader(VARY_HEADER, VARY_ALL);
            return;
        }

        // Build single header to replace current multiple headers
        // Replace existing header(s) to ensure any invalid values are removed
        fieldNames.add(name);
        StringBuilder varyHeader = new StringBuilder();
        varyHeader.append(name);
        for (String fieldName : fieldNames) {
            varyHeader.append(',');
            varyHeader.append(fieldName);
        }
        adapter.setHeader(VARY_HEADER, varyHeader.toString());
    }


    private static interface Adapter {

        Collection<String> getHeaders(String name);

        void setHeader(String name, String value);

        void addHeader(String name, String value);
    }



    private static final class ResponseAdapter implements Adapter {
        private final HttpServletResponse response;

        public ResponseAdapter(HttpServletResponse response) {
            this.response = response;
        }

        @Override
        public Collection<String> getHeaders(String name) {
            return response.getHeaders(name);
        }

        @Override
        public void setHeader(String name, String value) {
            response.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            response.addHeader(name, value);
        }
    }
}