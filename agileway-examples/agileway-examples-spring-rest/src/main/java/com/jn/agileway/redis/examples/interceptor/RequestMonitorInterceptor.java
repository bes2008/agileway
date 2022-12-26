package com.jn.agileway.redis.examples.interceptor;


import com.jn.agileway.metrics.core.MetricName;
import com.jn.agileway.metrics.core.Metrics;
import com.jn.agileway.metrics.core.meter.Meter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestMonitorInterceptor extends HandlerInterceptorAdapter {
    MetricName metricName = Metrics.name("http", "qps").tag("instance", "ins-1");
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Meter meter = Metrics.getMeter("test", metricName);


        return super.preHandle(request, response, handler);
    }
}
