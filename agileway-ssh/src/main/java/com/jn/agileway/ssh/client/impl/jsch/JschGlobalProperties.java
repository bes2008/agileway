package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.JSch;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Function0;

import java.util.Hashtable;
import java.util.List;

/**
 * @see com.jcraft.jsch.JSch#setConfig(Hashtable)
 */
public class JschGlobalProperties implements Function0 {

    private int maxAuthTries = 6;
    /**
     * 在session#connect后，内部会自动的进行 host 检查
     * <p>
     * 可选值：
     * <pre>
     *     ask: 要进行检查，并且会通过 UserInfo 来进行提示询问
     *     yes: 要进行检查
     *     no: 不进行检查
     * </pre>
     */
    private String strictHostKeyChecking = "no";


    public void setMaxAuthTries(int maxAuthTries) {
        if (maxAuthTries > 0) {
            this.maxAuthTries = maxAuthTries;
        }
    }

    private static final List<String> validValues_strictHostKeyChecking = Collects.newArrayList("yes", "ask", "no");

    public void setStrictHostKeyChecking(String strictHostKeyChecking) {
        if (Strings.isNotBlank(strictHostKeyChecking)) {
            strictHostKeyChecking = strictHostKeyChecking.toLowerCase();
            if (validValues_strictHostKeyChecking.contains(strictHostKeyChecking)) {
                this.strictHostKeyChecking = strictHostKeyChecking;
            }
        }
    }

    public void apply() {
        JSch.setConfig("StrictHostKeyChecking", strictHostKeyChecking);
        JSch.setConfig("MaxAuthTries", "" + maxAuthTries);
    }

}

