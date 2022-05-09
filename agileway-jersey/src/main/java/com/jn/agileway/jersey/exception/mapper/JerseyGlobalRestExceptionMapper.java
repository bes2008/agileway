package com.jn.agileway.jersey.exception.mapper;

import org.glassfish.jersey.spi.ExtendedExceptionMapper;

import javax.ws.rs.core.Response;

public class JerseyGlobalRestExceptionMapper implements ExtendedExceptionMapper {
    @Override
    public boolean isMappable(Throwable exception) {
        return false;
    }

    @Override
    public Response toResponse(Throwable exception) {
        return null;
    }
}
