package com.example;

import com.jn.agileway.http.rest.GlobalRestResponseBodyContext;
import com.jn.agileway.http.rest.GlobalRestResponseBodyHandlerConfiguration;
import com.jn.agileway.http.rest.GlobalRestResponseBodyHandlerConfigurationBuilder;
import com.jn.agileway.http.rest.GlobalRestResponseBodyHandlerProperties;
import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessorRegistry;
import com.jn.agileway.jaxrs.rest.JaxrsGloablRestResponseInterceptor;
import com.jn.agileway.jaxrs.rest.JaxrsGlobalRestResponseBodyHandler;
import com.jn.agileway.jaxrs.rr.requestmapping.JaxrsRequestMappingAccessorParser;
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
        GlobalRestResponseBodyContext context = new GlobalRestResponseBodyContext();
        GlobalRestResponseBodyHandlerConfiguration configuration = new GlobalRestResponseBodyHandlerConfigurationBuilder().setProperties(responseBodyHandlerProperties).build();
        context.setConfiguration(configuration);
        context.init();

        JaxrsGlobalRestResponseBodyHandler handler = new JaxrsGlobalRestResponseBodyHandler();
        handler.setRequestMappingAccessorRegistry(mappingAccessorRegistry);
        handler.setContext(context);

        JaxrsGloablRestResponseInterceptor interceptor = new JaxrsGloablRestResponseInterceptor();
        interceptor.setResponseBodyHandler(handler);
        rc.register(interceptor);

        JerseyGlobalRestResultValidator validator = new JerseyGlobalRestResultValidator();
        validator.setResponseBodyHandler(handler);
        rc.register(validator);
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

