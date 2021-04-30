package com.jn.agileway.web.security;


import com.jn.agileway.web.servlet.Servlets;
import com.jn.langx.http.HttpStatus;
import com.jn.langx.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    protected static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    private String errorPage;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (response.isCommitted()) {
            logger.trace("Did not write to response since already committed");
            return;
        }
        if (this.errorPage == null) {
            logger.debug("Responding with 403 status code");
            response.sendError(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase());
            return;
        }
        // Put exception into request scope (perhaps of use to a view)
        request.setAttribute(Servlets.ACCESS_DENIED_403, accessDeniedException);
        // Set the 403 status code.
        response.setStatus(HttpStatus.FORBIDDEN.value());
        // forward to error page.
        if (logger.isDebugEnabled()) {
            logger.debug("Forwarding to {} with status code 403", this.errorPage);
        }
        request.getRequestDispatcher(this.errorPage).forward(request, response);
    }

    /**
     * The error page to use. Must begin with a "/" and is interpreted relative to the
     * current context root.
     * @param errorPage the dispatcher path to display
     * @throws IllegalArgumentException if the argument doesn't comply with the above
     * limitations
     */
    public void setErrorPage(String errorPage) {
        Preconditions.checkTrue(errorPage == null || errorPage.startsWith("/"), "errorPage must begin with '/'");
        this.errorPage = errorPage;
    }

}
