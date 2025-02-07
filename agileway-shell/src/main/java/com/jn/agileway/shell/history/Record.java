package com.jn.agileway.shell.history;

import com.jn.langx.util.Strings;

/**
 * Record类用于表示一个包含时间和命令行的记录
 */
public class Record {
    // 记录的时间
    private String datetime;
    // 记录的命令行
    private String cmdline;

    /**
     * 构造一个Record对象
     *
     * @param datetime 时间字符串
     * @param cmdline  命令行字符串
     */
    Record(String datetime, String cmdline) {
        this.datetime = datetime;
        this.cmdline = cmdline;
    }

    /**
     * 获取记录的时间
     *
     * @return 时间字符串
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * 获取记录的命令行
     *
     * @return 命令行字符串
     */
    public String getCmdline() {
        return cmdline;
    }

    /**
     * 重写toString方法，用于以字符串形式表示Record对象
     *
     * @return 表示Record对象的字符串，格式为datetime|cmdline
     */
    @Override
    public String toString() {
        return this.datetime + "|" + this.cmdline;
    }

    /**
     * 从给定的字符串创建一个Record对象
     * 字符串应该以datetime和cmdline的形式存在，并且用'|'分隔
     *
     * @param line 包含datetime和cmdline的字符串
     * @return 新创建的Record对象
     * @throws IllegalArgumentException 如果字符串格式不正确，则抛出此异常
     */
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
