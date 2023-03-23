package com.jn.agileway.syslog.protocol;


import com.jn.langx.util.enums.Enums;

public class Priority {
    private Priority() {
    }

    public static int getFacility(int priority) {
        return priority >> 3;
    }

    public static Severity getSeverity(int priority, int facility) {
        return Enums.ofCode(Severity.class,priority - (facility << 3));
    }

    public static int createPriority(int severity, int facility) {
        return (facility * 8) + severity;
    }
}
