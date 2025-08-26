package com.jn.agileway.shell.command;

import com.jn.langx.util.Preconditions;

/**
 * 可用性方法的执行结果
 */
public class Availability {
    /**
     * 是否可用
     */
    private boolean available;
    /**
     * 不可用的原因
     */
    private String reason;

    private Availability(boolean available, String reason) {
        this.available = available;
        if(!available) {
            this.reason = Preconditions.checkNotEmpty(reason);
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public String getReason() {
        return reason;
    }

    public static Availability available(){
        return new Availability(true, null);
    }

    public static Availability unavailable(String reason){
        return new Availability(false, reason);
    }
}
