package com.jn.agileway.syslog.protocol.tests;

import com.jn.agileway.syslog.protocol.RFC3164MessageParser;
import com.jn.agileway.syslog.protocol.RFC3164SyslogTextGenerator;
import com.jn.agileway.syslog.protocol.RFC5424MessageParser;
import com.jn.agileway.syslog.protocol.SyslogMessage;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.datetime.parser.CandidatePatternsDateTimeParser;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.TimeZone;

public class SyslogTest {
    @Test
    public void test3164() {
        RFC3164MessageParser parser = new RFC3164MessageParser();
        RFC3164SyslogTextGenerator generator = new RFC3164SyslogTextGenerator();
        System.out.println("====================================================");
        String log = "<34>Oct 11 22:14:15 mymachine su: 'su root' failed for lonvick on /dev/pts/8";
        SyslogMessage syslogMessage = parser.parse(log);
        System.out.println(syslogMessage);
        System.out.println("-------");
        System.out.println(generator.get(syslogMessage));
        System.out.println("====================================================");

        log = "<34>Oct 11 22:14:15 mymachine 'su root' failed for lonvick on /dev/pts/8";
        syslogMessage = parser.parse(log);
        System.out.println(syslogMessage);
        System.out.println("-------");
        System.out.println(generator.get(syslogMessage));
        System.out.println("====================================================");
        log = "<34>Oct 11 22:14:15 mymachine su[3]: 'su root' failed for lonvick on /dev/pts/8";
        syslogMessage = parser.parse(log);
        System.out.println(syslogMessage);
        System.out.println("-------");
        System.out.println(generator.get(syslogMessage));
        System.out.println("====================================================");
    }

    @Test
    public void test5424() {
        RFC5424MessageParser parser = new RFC5424MessageParser();
        parser.setDateTimeParser(new CandidatePatternsDateTimeParser(Collects.asList("yyyy-MM-dd'T'HH:mm:ss.SSSX"), Collects.asList(TimeZone.getDefault()), Collects.asList(Locale.getDefault(), Locale.US)));
        System.out.println("====================================================");
        String log = "<165>1 2003-08-24T05:14:15.000003-07:00 192.0.2.1 myproc 8710 - - %% It's time to make the do-nuts.";
        SyslogMessage syslogMessage = parser.parse(log);
        System.out.println(syslogMessage);
        System.out.println("====================================================");

    }
}
