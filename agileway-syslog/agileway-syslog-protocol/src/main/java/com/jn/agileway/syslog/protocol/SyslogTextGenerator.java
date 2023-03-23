package com.jn.agileway.syslog.protocol;


import com.jn.langx.util.function.Supplier;

public interface SyslogTextGenerator extends Supplier<SyslogMessage, String> {
    @Override
    String get(SyslogMessage syslogMessage);
}
