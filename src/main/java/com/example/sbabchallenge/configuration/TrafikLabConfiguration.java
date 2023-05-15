package com.example.sbabchallenge.configuration;

import com.example.sbabchallenge.model.TrafiklabException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class TrafikLabConfiguration {
    @Bean(name="TrafikLabObjectMapper")
    public ObjectMapper objectMapper(){
        return new Jackson2ObjectMapperBuilder()
                .failOnUnknownProperties(false)
                .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .build();
    }

    @Bean("TrafikLabAPIEndpoint")
    public URI apiEndpoint(@Value("${apiEndpoint:https://api.sl.se/api2}") String apiEndpointEnvVariable) throws URISyntaxException {
        return new URI(apiEndpointEnvVariable);
    }

    @Bean("TrafikLabAPIKey")
    public String apiKey(@Value("${apiKey}") String apiKeyEnvVariable) {
        return apiKeyEnvVariable;
    }

    @Bean("TrafikLabRestTemplate")
    public RestTemplate RestTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                throw new TrafiklabException(response.getStatusCode());
            }
        });
        return restTemplate;
    }

}
