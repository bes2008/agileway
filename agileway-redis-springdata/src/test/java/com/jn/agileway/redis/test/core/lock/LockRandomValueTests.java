package com.jn.agileway.redis.test.core.lock;

import com.jn.agileway.redis.locks.LockRandomValueBuilder;
import org.junit.Test;

public class LockRandomValueTests {
    @Test
    public void test() {
        LockRandomValueBuilder builder = new LockRandomValueBuilder();
        int i = 100;
        while (i > 0) {
            i--;
            System.out.println(builder.build());
        }
    }

}