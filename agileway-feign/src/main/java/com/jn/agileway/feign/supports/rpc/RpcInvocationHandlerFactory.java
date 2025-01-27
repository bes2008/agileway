package com.jn.agileway.feign.supports.rpc;

import com.jn.agileway.feign.ErrorHandler;
import com.jn.agileway.feign.Feigns;
import com.jn.langx.util.Preconditions;
import feign.InvocationHandlerFactory;
import feign.MethodMetadata;
import feign.Target;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @since 2.6.0
 */
public class RpcInvocationHandlerFactory implements InvocationHandlerFactory {

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
            this.target = Preconditions.checkNotNullArgument(target, "target");
            this.dispatch = Preconditions.checkNotNull(dispatch, "dispatch for {}", target);
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
            } catch (Throwable ex) {
                // 此时 Response 可能已被关闭或者 根本就没有连接成功，也可能是 null
                FeignRR feignRR = ClientWrapper.feignRRHolder.get();
                MethodMetadata metadata = Feigns.getMethodMetadata(methodHandler);
                String methodKey = metadata.configKey();
                FeignRpcException exception = new FeignRpcException(methodKey, feignRR.getResponse(), ex);
                return errorHandler.apply(exception, methodHandler);
            } finally {
                ClientWrapper.feignRRHolder.reset();
            }
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
