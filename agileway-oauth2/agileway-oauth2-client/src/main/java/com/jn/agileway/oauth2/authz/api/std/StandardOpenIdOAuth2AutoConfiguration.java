package com.jn.agileway.oauth2.authz.api.std;

import com.bes.um3rd.utils.CustomExchangeStrategies;
import com.jn.agileway.oauth2.authz.OAuth2Properties;
import com.jn.agileway.oauth2.authz.api.IntrospectEndpointAuthTokenSupplier;
import com.jn.agileway.oauth2.authz.api.OAuth2ApiResponseConverter;
import com.jn.agileway.oauth2.authz.api.OAuth2ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.JdkClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.FormHttpMessageWriter;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.net.http.HttpClient;

@ConditionalOnMissingBean(OAuth2ApiService.class)
@Configuration
public class StandardOpenIdOAuth2AutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(StandardOpenIdOAuth2AutoConfiguration.class);

    @Bean("oauth2WebClient")
    public WebClient oauth2WebClient(OAuth2Properties oAuth2Properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        JdkClientHttpConnector clientConnector = new JdkClientHttpConnector(httpClient);
        clientConnector.setReadTimeout(oAuth2Properties.getReadTimeout());

        ClientCodecConfigurer clientCodecConfigurer = ClientCodecConfigurer.create();
        clientCodecConfigurer.registerDefaults(true);
        CustomExchangeStrategies exchangeStrategies = new CustomExchangeStrategies(clientCodecConfigurer.getReaders(), clientCodecConfigurer.getWriters());
        exchangeStrategies.messageWriters().add(0, new FormHttpMessageWriter());
        exchangeStrategies.messageWriters().stream().forEach(w -> {
            if (w instanceof LoggingCodecSupport) {
                ((LoggingCodecSupport) w).setEnableLoggingRequestDetails(true);
            }
        });
        exchangeStrategies.messageReaders().stream().forEach(r -> {
            if (r instanceof LoggingCodecSupport) {
                ((LoggingCodecSupport) r).setEnableLoggingRequestDetails(true);
            }
        });

        ExchangeFilterFunction errorFilterFunction = ExchangeFilterFunction.ofResponseProcessor(response -> {
            int statusCode = response.statusCode().value();
            if (statusCode >= 400) {
                String body = response.bodyToMono(String.class).block();
                logger.error("status-code: {}, content: {}", statusCode, body);
            }
            return Mono.just(response);
        });


        return WebClient.builder()
                .baseUrl(oAuth2Properties.getBaseUri())
                .clientConnector(clientConnector)
                .filter(errorFilterFunction)
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public StandardOpenIdOAuth2ApiResponseConverter standardOpenIdOAuth2ApiResponsePayloadHandler() {
        return new StandardOpenIdOAuth2ApiResponseConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "um3rd.oauth2.standard.introspect-endpoint-auth-type", havingValue = "Basic", matchIfMissing = true)
    public IntrospectEndpointAuthTokenSupplier introspectEndpointAuthTokenSupplier(OAuth2Properties oAuth2Properties) {
        return new BasicIntrospectEndpointAuthTokenSupplier(oAuth2Properties);
    }

    @Bean
    public OAuth2ApiService oauth2ApiService(@Qualifier("oauth2WebClient")
                                             WebClient webClient,
                                             OAuth2Properties oAuth2Properties,
                                             IntrospectEndpointAuthTokenSupplier introspectEndpointAuthTokenSupplier,
                                             OAuth2ApiResponseConverter apiResponsePayloadHandler) {
        Class<? extends StandardOpenIdOAuth2Api> apiInterface = oAuth2Properties.getStandard().getApiInterface();
        if (apiInterface == null) {
            apiInterface = StandardOpenIdOAuth2Api.class;
        }

        StandardOpenIdOAuth2Api api = HttpServiceProxyFactory.builder()
                .exchangeAdapter(WebClientAdapter.create(webClient))
                .build().createClient(apiInterface);
        StandardOpenIdOAuth2ApiService standardOAuth2ApiService = new StandardOpenIdOAuth2ApiService(api,
                oAuth2Properties,
                introspectEndpointAuthTokenSupplier,
                apiResponsePayloadHandler);
        return standardOAuth2ApiService;
    }

}
