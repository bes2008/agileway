package com.jn.agileway.feign.supports.adaptable;

import com.jn.agileway.feign.supports.rpc.FeignRPCException;
import com.jn.easyjson.core.JSONFactory;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;
import feign.*;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;

import static feign.Util.checkNotNull;

public class AdaptableInvocationHandlerFactory implements InvocationHandlerFactory {
    private static final Logger logger = LoggerFactory.getLogger(AdaptableInvocationHandlerFactory.class);

    private JSONFactory jsonFactory;
    private ErrorDecoder errorDecoder;
    /**
     * 如果项目中，没有对返回值进行统一处理，则可以设置为 Object.class
     */
    private Class unifiedResponseClass = RestRespBody.class;

    public void setUnifiedResponseClass(Class unifiedResponseClass) {
        if (unifiedResponseClass != null) {
            this.unifiedResponseClass = unifiedResponseClass;
        }
    }

    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    @Override
    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        Preconditions.checkNotNull(jsonFactory, "the json factory is null");
        return new UnifiedResponseInvocationHandler(target, dispatch);
    }

    class UnifiedResponseInvocationHandler implements InvocationHandler {
        private final Target target;
        private final Map<Method, MethodHandler> dispatch;

        UnifiedResponseInvocationHandler(Target target, Map<Method, MethodHandler> dispatch) {
            this.target = checkNotNull(target, "target");
            this.dispatch = checkNotNull(dispatch, "dispatch for %s", target);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("equals".equals(method.getName())) {
                try {
                    Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                    return equals(otherHandler);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            } else if ("hashCode".equals(method.getName())) {
                return hashCode();
            } else if ("toString".equals(method.getName())) {
                return toString();
            }
            Object result = null;
            MethodHandler methodHandler = dispatch.get(method);
            try {
                result = methodHandler.invoke(args);
                if (result == null) {
                    return null;
                }
                if (Reflects.isSubClassOrEquals(unifiedResponseClass, result.getClass())) {
                    return result;
                }
                if (result instanceof Response) {
                    return result;
                }
            } catch (FeignRPCException ex) {
                Type type = ((MethodMetadata) Reflects.getAnyFieldValue(methodHandler, "metadata", true, false)).returnType();
                return jsonFactory.get().fromJson(ex.getResponseBody(), type);

            } catch (FeignException ex) {
                String message = ex.getMessage();
                String errorStatusMessageFlag = "; content:";
                int errorStatusIndex = message.indexOf(errorStatusMessageFlag);
                if (errorStatusIndex > 0) {
                    String responseBody = message.substring(errorStatusIndex + errorStatusMessageFlag.length());
                    Type type = ((MethodMetadata) Reflects.getAnyFieldValue(methodHandler, "metadata", true, false)).returnType();
                    return jsonFactory.get().fromJson(responseBody, type);
                } else {
                    logger.error("error occur when execute {}", Reflects.getMethodString(method), ex);
                }
            }
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof UnifiedResponseInvocationHandler) {
                UnifiedResponseInvocationHandler other = (UnifiedResponseInvocationHandler) obj;
                return target.equals(other.target);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return target.hashCode();
        }

        @Override
        public String toString() {
            return target.toString();
        }
    }
}
