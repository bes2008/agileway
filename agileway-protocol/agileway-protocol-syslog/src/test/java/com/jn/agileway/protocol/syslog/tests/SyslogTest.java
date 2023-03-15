package com.jn.agileway.protocol.syslog.tests;

import com.jn.agileway.protocol.syslog.RFC3164MessageParser;
import com.jn.agileway.protocol.syslog.SyslogMessage;
import org.junit.Test;

public class SyslogTest {
    @Test
    public void test3164(){
        String log = "<34>Oct 11 22:14:15 mymachine su: 'su root' failed for lonvick on /dev/pts/8";
        SyslogMessage syslogMessage = new RFC3164MessageParser().parse(log);
        System.out.println(syslogMessage);
    }
}
