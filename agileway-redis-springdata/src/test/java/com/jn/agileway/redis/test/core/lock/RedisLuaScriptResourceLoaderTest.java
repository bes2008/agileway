package com.jn.agileway.redis.test.core.lock;

import com.jn.agileway.redis.core.script.BuiltinLuaScriptLocationProvider;
import com.jn.agileway.redis.core.script.RedisLuaScript;
import com.jn.agileway.redis.core.script.RedisLuaScriptParser;
import com.jn.agileway.redis.core.script.RedisLuaScriptResourceLoader;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.ReturnType;

public class RedisLuaScriptResourceLoaderTest {

    @Test
    public void testReturnTypeToValue(){
        RedisLuaScriptResourceLoader loader = new RedisLuaScriptResourceLoader();
        loader.setParser(new RedisLuaScriptParser());
        BuiltinLuaScriptLocationProvider builtinLuaScriptLocationProvider = new BuiltinLuaScriptLocationProvider();
        builtinLuaScriptLocationProvider.init();
        loader.setResourceLocationProvider(builtinLuaScriptLocationProvider);
        RedisLuaScript setValueAndExpire = loader.load("SetValueAndExpire");
        Class resultType = setValueAndExpire.getResultType();
        ReturnType returnType = ReturnType.fromJavaType(resultType);
        System.out.println(returnType);
    }
}
