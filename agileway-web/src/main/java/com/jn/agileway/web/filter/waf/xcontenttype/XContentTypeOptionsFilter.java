package com.jn.agileway.web.filter.waf.xcontenttype;

import com.jn.agileway.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 如果服务器发送响应头 "X-Content-Type-Options: nosniff"，
 * 则 浏览器在处理 <script></script> 和 <style> 元素时，会拒绝包含错误的 MIME 类型的响应。
 * 这是一种安全功能，有助于防止基于 MIME 类型混淆的攻击。
 */
public class XContentTypeOptionsFilter extends OncePerRequestFilter {
    private XContentTypeOptionsProperties properties = new XContentTypeOptionsProperties();

    public void setProperties(XContentTypeOptionsProperties properties) {
        this.properties = properties;
    }


    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (properties.isEnabled()) {
            if (request instanceof HttpServletRequest) {
                ((HttpServletResponse) response).setHeader("X-Content-Type-Options", "nosniff");
            }
        }
        doFilter(request, response, chain);
    }
}
