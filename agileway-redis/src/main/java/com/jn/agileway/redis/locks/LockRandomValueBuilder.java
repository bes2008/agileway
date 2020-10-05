package com.jn.agileway.redis.locks;

import com.jn.langx.Builder;
import com.jn.langx.util.Platform;
import com.jn.langx.util.concurrent.threadlocal.GlobalThreadLocalMap;
import com.jn.langx.util.net.Nets;

import java.net.InetAddress;

public class LockRandomValueBuilder implements Builder<String> {
    private static final String randomValuePrefix;

    static {
        String mac;
        String ip = "localhost";
        String pid;
        InetAddress inetAddress = Nets.getCurrentAddress();
        if (inetAddress != null) {
            ip = Nets.toAddressString(inetAddress);
        }
        mac = Nets.getFirstValidMac();
        pid = Platform.processId;

        randomValuePrefix = ip + "_" + mac + "_" + pid;
    }

    public String build() {
        long random = GlobalThreadLocalMap.getRandom().nextLong();
        return randomValuePrefix + "_" + System.currentTimeMillis() + "_" + random;
    }

}
