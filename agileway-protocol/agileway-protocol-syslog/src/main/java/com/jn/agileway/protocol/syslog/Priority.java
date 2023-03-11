package com.jn.agileway.protocol.syslog;

public class Priority {
    private Priority() {
    }

    public static int facility(int priority) {
        return priority >> 3;
    }

    public static int level(int priority, int facility) {
        return priority - (facility << 3);
    }

    public static int priority(int level, int facility) {
        return (facility * 8) + level;
    }
}
