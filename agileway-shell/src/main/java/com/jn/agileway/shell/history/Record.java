package com.jn.agileway.shell.history;

import com.jn.langx.util.Strings;

public class Record {
    private String datetime;
    private String cmdline;

    Record(String datetime, String cmdline) {
        this.datetime = datetime;
        this.cmdline = cmdline;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getCmdline() {
        return cmdline;
    }

    @Override
    public String toString() {
        return this.datetime + "|" + this.cmdline;
    }

    public static Record of(String line) {
        int index = Strings.indexOf(line, '|');
        if (index < 0 || index == line.length() - 1) {
            throw new IllegalArgumentException();
        }
        String datetime = Strings.substring(line, 0, index);
        String cmdline = Strings.substring(line, index + 1);
        return new Record(datetime, cmdline);
    }
}
