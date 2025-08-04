package com.jn.agileway.oauth2.authz.api;

import java.util.function.Supplier;

/**
 * 提供endpoint的认证信息
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7662#section-2.1">Introspection Request</a>
 */
public interface IntrospectEndpointAuthTokenSupplier extends Supplier<String> {
    String get();
}
