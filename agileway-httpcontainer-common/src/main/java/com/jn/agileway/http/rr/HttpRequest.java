package com.jn.agileway.http.rr;

import java.util.Collection;
import java.util.Locale;

public interface HttpRequest<D> {
    D getContainerRequest();

    String getRemoteAddr();

    String getRemoteHost();

    String getMethod();

    String getHeader(String name);

    Collection<String> getHeaderNames();

    Collection<String> getHeaders(String name);

    Collection<String> getAttributeNames();

    Object getAttribute(String name);

    void setAttribute(String name, Object value);

    /**
     * Returns the preferred <code>Locale</code> that the client will
     * accept content in, based on the Accept-Language header.
     * If the client request doesn't provide an Accept-Language header,
     * this method returns the default locale for the server.
     *
     * @return the preferred <code>Locale</code> for the client
     */
    Locale getLocale();

    /**
     * Returns the part of this request's URL from the protocol
     * name up to the query string in the first line of the HTTP request.
     * The web container does not decode this String.
     * For example:
     *
     *
     *
     * <table summary="Examples of Returned Values">
     * <tr align=left><th>First line of HTTP request      </th>
     * <th>     Returned Value</th>
     * <tr><td>POST /some/path.html HTTP/1.1<td><td>/some/path.html
     * <tr><td>GET http://foo.bar/a.html HTTP/1.0
     * <td><td>/a.html
     * <tr><td>HEAD /xyz?a=b HTTP/1.1<td><td>/xyz
     * </table>
     *
     * <p>To reconstruct an URL with a scheme and host, use
     *
     * @return a <code>String</code> containing
     * the part of the URL from the
     * protocol name up to the query string
     */

    String getRequestURI();

    /**
     * Reconstructs the URL the client used to make the request.
     * The returned URL contains a protocol, server name, port
     * number, and server path, but it does not include query
     * string parameters.
     *
     * <p>If this request has been forwarded using
     * the server path in the
     * reconstructed URL must reflect the path used to obtain the
     * RequestDispatcher, and not the server path specified by the client.
     *
     * <p>Because this method returns a <code>StringBuffer</code>,
     * not a string, you can modify the URL easily, for example,
     * to append query parameters.
     *
     * <p>This method is useful for creating redirect messages
     * and for reporting errors.
     */
    String getRequestURL();

    /**
     * Reconstructs the URL the client used to make the request.
     * The returned URL contains a protocol, server name, port
     * number, and context path, but it does not include query
     * string parameters.
     *
     * <p>If this request has been forwarded using
     * the server path in the
     * reconstructed URL must reflect the path used to obtain the
     * RequestDispatcher, and not the server path specified by the client.
     *
     * <p>Because this method returns a <code>StringBuffer</code>,
     * not a string, you can modify the URL easily, for example,
     * to append query parameters.
     *
     * <p>This method is useful for creating redirect messages
     * and for reporting errors.
     */
    String getBaseURL();

    /**
     * Reconstructs the URL the client used to make the request.
     * The returned URL not contains a protocol, server name, port
     * number, and context path, and it does not include query
     * string parameters.
     *
     *
     * getBaseURL() + getPath() = getRequestURL()
     *
     * <p>If this request has been forwarded using
     * the server path in the
     * reconstructed URL must reflect the path used to obtain the
     * RequestDispatcher, and not the server path specified by the client.
     *
     * <p>Because this method returns a <code>StringBuffer</code>,
     * not a string, you can modify the URL easily, for example,
     * to append query parameters.
     *
     * <p>This method is useful for creating redirect messages
     * and for reporting errors.
     */
    String getPath();

}
