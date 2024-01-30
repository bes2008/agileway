package com.jn.agileway.audit.spring.webmvc;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.Auditor;
import com.jn.agileway.audit.core.model.OperationResult;
import com.jn.langx.util.reflect.Reflects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 实际情况下，我们应该是去拦截 method invocation，也就是我们应该使用普通的 AOP method interceptor，
 * 但是这里为啥是采用了HttpInterceptor了呢？
 * <p>
 * 原因：
 * 1) Spring里有 @ControllerAdvice，@RestControllerAdvice
 * 大家通常使用他们来进行全局的返回值、异常处理等。
 * 这就导致了最终的 OperationResult 可能会获取的不准确，具体是来说是不能根据 http response status 进行最后的判断。
 * 2) 任意方法拦截器，是不一定能拿到 HttpServletRequest的，对于Web请求，必须要拿到HttpServletRequest对象才能进行其他数据的提取。
 * <p>
 * <p>
 * 但是这里作为入口，有一个不足：这里取不到运行时参数，因为这里调用时，Spring还没有组装运行时参数。
 * 所以需要一个AOP method interceptor来配合。例如 ControllerMethodInterceptor。
 */
public class AuditHttpHandlerInterceptor implements HandlerInterceptor {
    /**
     * Spring Boot WebMvc 环境下，会将 error 放到 request.attributes中
     */
    private static final String SPRING_BOOT_WEBMVC_ERROR_ATTRIBUTE = "org.springframework.boot.web.servlet.error.DefaultErrorAttributes.ERROR";

    /**
     * Spring Boot WebFlux 环境下，会将 error 放到 exchange 下
     */
    private static final String SPRING_BOOT_WEBFLUX_ERROR_ATTRIBUTE = "org.springframework.boot.web.reactive.error.DefaultErrorAttributes.ERROR";

    private Auditor<HttpServletRequest, Method> auditor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        if (handler instanceof HandlerMethod
                || Reflects.hasAnnotation(handler.getClass(), Controller.class)
                || Reflects.hasAnnotation(handler.getClass(), RestController.class)
                || Reflects.isSubClass(org.springframework.web.servlet.mvc.Controller.class, handler.getClass())) {
            AuditRequest<HttpServletRequest, Method> wrappedRequest = Auditor.getRequest();
            if (wrappedRequest != null) {
                OperationResult result = wrappedRequest.getResult();
                if (result == null) {
                    if (ex != null) {
                        result = OperationResult.FAIL;
                    } else {
                        Object errorObj = request.getAttribute(SPRING_BOOT_WEBMVC_ERROR_ATTRIBUTE);
                        if (errorObj == null && response.getStatus() < 400) {
                            result = OperationResult.SUCCESS;
                        } else {
                            result = OperationResult.FAIL;
                        }
                    }
                }
                wrappedRequest.setResult(result);
                auditor.finishAudit(wrappedRequest);
            }
        }
    }

    @Autowired
    public void setAuditor(Auditor<HttpServletRequest, Method> auditor) {
        this.auditor = auditor;
    }
}
