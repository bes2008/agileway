package com.jn.agileway.redis.core.script;

import com.jn.langx.configuration.InputStreamConfigurationParser;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.io.Channels;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.struct.Holder;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public class RedisLuaScriptParser implements InputStreamConfigurationParser<RedisLuaScript> {
    private Charset charset = Charsets.UTF_8;
    private static final String RETURN_VALUE_DECLARE_FLAG = "-- @return ";

    @Override
    public void setEncoding(String charset) {
        if (Strings.isNotBlank(charset)) {
            this.charset = Charsets.getCharset(charset);
        }
    }


    @Override
    public RedisLuaScript parse(InputStream inputStream) {
        RedisLuaScript redisScript = new RedisLuaScript();
        Class returnType = parseReturnType(inputStream);
        if (returnType != null) {
            redisScript.setResultType(returnType);
        }
        return redisScript;
    }

    private Class parseReturnType(InputStream inputStream) {
        final Holder<Class> returnTypeHolder = new Holder<Class>();
        Channels.readLines(inputStream, this.charset, new Predicate2<Integer, String>() {
            @Override
            public boolean test(Integer index, String line) {
                return Strings.isNotBlank(line) && Strings.startsWith(line, RETURN_VALUE_DECLARE_FLAG);
            }
        }, new Consumer2<Integer, String>() {
            @Override
            public void accept(Integer index, String line) {
                String comment = line.substring(RETURN_VALUE_DECLARE_FLAG.length());
                comment = Strings.trimOrEmpty(comment);
                if (Strings.isEmpty(comment)) {
                    return;
                }
                List<String> segments = Pipeline.of(comment.split(" ,;"))
                        .filter(Functions.<String>notEmptyPredicate())
                        .clearNulls()
                        .asList();
                if (!segments.isEmpty()) {
                    String returnTypeString = segments.get(0);
                    if (Emptys.isNotEmpty(returnTypeString)) {
                        if (isBooleanType(returnTypeString)) {
                            returnTypeHolder.set(Boolean.class);
                        } else if (isLongType(returnTypeString)) {
                            returnTypeHolder.set(Long.class);
                        } else if (isListType(returnTypeString)) {
                            returnTypeHolder.set(List.class);
                        } else if(isObjectType(returnTypeString)){
                            // returnTypeHolder不能设置Object类型。
                            // 如果是Object,那么会选择成ReturnType.MULTI(list对应的类型).所以用此内部类来标记用来选择是ReturnType.VALUE
                            /** {@link org.springframework.data.redis.connection.ReturnType#fromJavaType(Class)}*/
                            returnTypeHolder.set(ValueReturnType.class);
                        }
                    }
                }
            }
        }, new Predicate2<Integer, String>() {
            @Override
            public boolean test(Integer index, String line) {
                return !returnTypeHolder.isEmpty();
            }
        });
        return returnTypeHolder.get();
    }

    private boolean isBooleanType(final String string) {
        return Pipeline.of("java.lang.Boolean", "boolean", "bool").anyMatch(new Predicate<String>() {
            @Override
            public boolean test(String type) {
                return Strings.equalsIgnoreCase(string, type);
            }
        });
    }

    private boolean isLongType(final String string) {
        return Pipeline.of("java.lang.Long", "long").anyMatch(new Predicate<String>() {
            @Override
            public boolean test(String type) {
                return Strings.equalsIgnoreCase(string, type);
            }
        });
    }

    private boolean isListType(final String string) {
        return Pipeline.of("java.util.List", "list", "table").anyMatch(new Predicate<String>() {
            @Override
            public boolean test(String type) {
                return Strings.equalsIgnoreCase(string, type);
            }
        });
    }

    private boolean isObjectType(final String string) {
        return Pipeline.of("java.lang.Object", "object").anyMatch(new Predicate<String>() {
            @Override
            public boolean test(String type) {
                return Strings.equalsIgnoreCase(string, type);
            }
        });
    }

    class ValueReturnType {

    }
}
