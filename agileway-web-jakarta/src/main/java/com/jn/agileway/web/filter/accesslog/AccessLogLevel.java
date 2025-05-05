package com.jn.agileway.web.filter.accesslog;

public enum AccessLogLevel {
    NONE, // 不记录
    BASIC, // url, protocol , method
    HEADERS, // 在BASIC的基础上包含 请求headers
    BODY, // 在 HEADER的基础上，加上 请求体
}
