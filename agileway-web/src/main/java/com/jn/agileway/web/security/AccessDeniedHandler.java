package com.jn.agileway.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <code>AccessDeniedException</code>.
 *
 * @author Ben Alex
 */
public interface AccessDeniedHandler {

    /**
     * Handles an access denied failure.
     * @param request that resulted in an <code>AccessDeniedException</code>
     * @param response so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     * @throws IOException in the event of an IOException
     * @throws ServletException in the event of a ServletException
     */
    void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException;

}