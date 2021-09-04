package com.jn.agileway.feign.supports.rpc;

import com.jn.agileway.feign.ErrorHandler;
import com.jn.agileway.feign.Feigns;
import feign.FeignException;
import feign.InvocationHandlerFactory;
import feign.MethodMetadata;
import feign.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import static feign.Util.checkNotNull;

/**
 * @since 2.6.0
 */
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
                if (ex.hasResponse()) {
                    return errorHandler.apply(ex, methodHandler);
                } else {
                    logger.error(ex.getMessage(), ex);
                }
            } catch (FeignException ex) {
                // 此时 Response 可能已被关闭，也可能是 null
                FeignRR feignRR = ClientWrapper.feignRRHolder.get();
                if (feignRR.getResponse() == null) {
                    logger.error(ex.getMessage(), ex);
                } else {
                    MethodMetadata metadata = Feigns.getMethodMetadata(methodHandler);
                    String methodKey = metadata.configKey();
                    FeignRpcException exception = new FeignRpcException(methodKey, feignRR.getResponse(), ex);
                    return errorHandler.apply(exception, methodHandler);
                }
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                ClientWrapper.feignRRHolder.reset();
            }
            return null;
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
