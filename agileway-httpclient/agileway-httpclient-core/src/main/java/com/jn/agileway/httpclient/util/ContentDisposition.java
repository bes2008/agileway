package com.jn.agileway.httpclient.util;

import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;

import java.net.URLEncoder;

/**
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Content-Disposition">Content-Disposition</a>
 */
public class ContentDisposition {
    /**
     * 它的值有三个：
     * inline：表示浏览器应该显示资源，而不是下载它
     * attachment：表示浏览器应该下载资源，而不是显示它
     * form-data：表示浏览器应该将资源作为表单数据发送给服务器
     */
    String value;

    /**
     * 接下来是参数
     */
    private String filename;
    private String fieldName;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * 用作 http 文件上传时
     */
    public static ContentDisposition ofFormData(@NotEmpty String fieldName, @Nullable String filename) {
        ContentDisposition contentDisposition = new ContentDisposition();
        contentDisposition.setFieldName(fieldName);
        contentDisposition.setFilename(filename);
        contentDisposition.setValue("form-data");
        return contentDisposition;
    }

    public String asString() {
        StringBuilder builder = new StringBuilder(128);
        builder.append("Content-Disposition: ").append(value);

        if (Strings.equals(value, "form-data")) {
            if (Strings.isNotEmpty(fieldName)) {
                builder.append("; name=\"").append(fieldName).append("\"");
            }
            if (Strings.isNotEmpty(filename)) {
                try {
                    builder.append("; filename=\"").append(URLEncoder.encode(filename, Charsets.US_ASCII.name())).append("\"");
                } catch (Exception ex) {
                    // ignore it
                }
            }
        }
        return builder.toString();
    }
}
