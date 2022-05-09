package com.example;

import com.jn.agileway.http.rest.*;
import com.jn.agileway.http.rest.exceptionhandler.ThrowableHandler;
import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessorRegistry;
import com.jn.agileway.jaxrs.rest.JaxrsGlobalRestExceptionHandler;
import com.jn.agileway.jaxrs.rest.JaxrsGlobalRestResponseInterceptor;
import com.jn.agileway.jaxrs.rest.JaxrsGlobalRestResponseBodyHandler;
import com.jn.agileway.jaxrs.rr.requestmapping.JaxrsRequestMappingAccessorParser;
import com.jn.agileway.jersey.exception.mapper.JerseyGlobalRestExceptionMapper;
import com.jn.agileway.jersey.validator.JerseyGlobalRestResultValidator;
import com.jn.langx.util.collection.Collects;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * Main class.
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        List<String> restResourcePackages = Collects.newArrayList("com.example");
        final ResourceConfig rc = new ResourceConfig().packages(Collects.toArray(restResourcePackages, String[].class));

        RequestMappingAccessorRegistry mappingAccessorRegistry = new RequestMappingAccessorRegistry();
        mappingAccessorRegistry.addParser(new JaxrsRequestMappingAccessorParser());
        mappingAccessorRegistry.init();

        GlobalRestResponseBodyHandlerProperties responseBodyHandlerProperties = new GlobalRestResponseBodyHandlerProperties();
        responseBodyHandlerProperties.setBasePackages(restResourcePackages);

        GlobalRestExceptionHandlerProperties exceptionHandlerProperties = new GlobalRestExceptionHandlerProperties();
        exceptionHandlerProperties.setLogStack(true);

        GlobalRestResponseBodyContext context = new GlobalRestResponseBodyContext();
        GlobalRestResponseBodyHandlerConfiguration configuration = new GlobalRestResponseBodyHandlerConfigurationBuilder().setProperties(responseBodyHandlerProperties).build();
        context.setConfiguration(configuration);
        context.setExceptionHandlerProperties(exceptionHandlerProperties);
        context.init();

        JaxrsGlobalRestResponseBodyHandler handler = new JaxrsGlobalRestResponseBodyHandler();
        handler.setRequestMappingAccessorRegistry(mappingAccessorRegistry);
        handler.setContext(context);

        JaxrsGlobalRestResponseInterceptor interceptor = new JaxrsGlobalRestResponseInterceptor();
        interceptor.setResponseBodyHandler(handler);
        rc.register(interceptor);

        JerseyGlobalRestResultValidator validator = new JerseyGlobalRestResultValidator();
        validator.setResponseBodyHandler(handler);
        rc.register(validator);

        GlobalRestExceptionHandlerRegistry exceptionHandlerRegistry = new GlobalRestExceptionHandlerRegistry();
        exceptionHandlerRegistry.init();
        exceptionHandlerRegistry.register(new ThrowableHandler());

        JaxrsGlobalRestExceptionHandler globalRestExceptionHandler = new JaxrsGlobalRestExceptionHandler();
        globalRestExceptionHandler.setContext(context);
        globalRestExceptionHandler.setExceptionHandlerRegistry(exceptionHandlerRegistry);

        JerseyGlobalRestExceptionMapper jerseyGlobalRestExceptionMapper = new JerseyGlobalRestExceptionMapper();
        jerseyGlobalRestExceptionMapper.setExceptionHandler(globalRestExceptionHandler);
        rc.register(jerseyGlobalRestExceptionMapper);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with endpoints available at "
                + "%s%nHit Ctrl-C to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

