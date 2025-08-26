package com.jn.agileway.httpclient.util;

import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

import java.util.List;

public enum ContentEncoding implements CommonEnum {
    /**
     * 表示采用 Lempel-Ziv coding（LZ77）压缩算法，以及 32 位 CRC 校验的编码方式。
     */
    GZIP("gzip", 1, "x-gzip"),
    /**
     * 采用 zlib 结构（在 RFC 1950 中规定）和 deflate 压缩算法（在 RFC 1951 中规定)。
     */
    DEFLATE("deflate", 2),
    /**
     * 采用 Lempel-Ziv-Welch（LZW）压缩算法。
     */
    COMPRESS("compress", 3),
    /**
     * 采用 Brotli 算法结构（在 RFC 7932 中规定）的格式。
     */
    BR("br", 4),
    /**
     * 采用 Zstandard 算法结构（在 RFC 8878 中规定）的格式。
     */
    ZSTD("zstd", 5);


    private EnumDelegate delegate;
    private final List<String> alias;

    ContentEncoding(String name, int code, String... alias) {
        this.delegate = new EnumDelegate(code, name, name);
        this.alias = Lists.immutableList(alias);
    }


    @Override
    public int getCode() {
        return this.delegate.getCode();
    }

    @Override
    public String getDisplayText() {
        return this.delegate.getDisplayText();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    public static ContentEncoding ofName(String name) {
        ContentEncoding contentEncoding = Enums.ofName(ContentEncoding.class, name);
        if (contentEncoding != null) {
            return contentEncoding;
        }
        for (ContentEncoding encoding : values()) {
            if (encoding.alias.contains(name)) {
                return encoding;
            }
        }
        return null;
    }
}
