package com.jn.agileway.jersey.validator;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.RR;
import com.jn.agileway.http.rr.RRLocal;
import com.jn.agileway.jaxrs.rest.JaxrsGlobalRestResponseBodyHandler;
import org.glassfish.jersey.server.model.Invocable;

import javax.validation.ConstraintViolationException;

public class JerseyGlobalRestResultValidator extends JerseyValidatorProxy {

    private JaxrsGlobalRestResponseBodyHandler responseBodyHandler;

    public void setResponseBodyHandler(JaxrsGlobalRestResponseBodyHandler responseBodyHandler) {
        this.responseBodyHandler = responseBodyHandler;
    }

    @Override
    public void validateResult(Object resource, Invocable resourceMethod, Object result) throws ConstraintViolationException {
        RR rr = RRLocal.get();
        if (rr != null && responseBodyHandler!=null) {
            HttpRequest request = rr.getRequest();
            responseBodyHandler.judgeIsSupportedAction(request, resourceMethod.getHandlingMethod(), result);
        }
        super.validateResult(resource, resourceMethod, result);
    }


}
