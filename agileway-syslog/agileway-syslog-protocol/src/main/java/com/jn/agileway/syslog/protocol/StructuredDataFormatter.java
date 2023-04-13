package com.jn.agileway.syslog.protocol;

import com.jn.langx.Formatter;

import java.util.List;

public interface StructuredDataFormatter extends Formatter<List<StructuredElement>,String> {
    @Override
    String format(List<StructuredElement> input, Object... args) ;
}
