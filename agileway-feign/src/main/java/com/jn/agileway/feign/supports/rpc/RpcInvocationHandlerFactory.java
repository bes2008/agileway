package com.jn.agileway.feign.supports.rpc;

import com.jn.agileway.feign.ErrorHandler;
import com.jn.langx.util.reflect.Reflects;
import feign.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import static feign.Util.checkNotNull;

public class RpcInvocationHandlerFactory implements InvocationHandlerFactory {
    private static final Logger logger = LoggerFactory.getLogger(RpcInvocationHandlerFactory.class);

    private ErrorHandler errorHandler;

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        return new RpcInvocationHandler(target, dispatch);
    }

    class RpcInvocationHandler implements InvocationHandler {
        private final Target target;
        private final Map<Method, MethodHandler> dispatch;

        RpcInvocationHandler(Target target, Map<Method, MethodHandler> dispatch) {
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
                return result;
            } catch (FeignRpcException ex) {
                return errorHandler.apply(ex, methodHandler);
            } catch (FeignException ex) {
                String message = ex.getMessage();
                String errorStatusMessageFlag = "; content:";
                int errorStatusIndex = message.indexOf(errorStatusMessageFlag);
                if (errorStatusIndex > 0) {
                    String responseBody = message.substring(errorStatusIndex + errorStatusMessageFlag.length());
                  //  Response response = Response.builder().request();
                } else {
                    logger.error("error occur when execute {}", Reflects.getMethodString(method), ex);
                }
            }
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RpcInvocationHandler) {
                RpcInvocationHandler other = (RpcInvocationHandler) obj;
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
