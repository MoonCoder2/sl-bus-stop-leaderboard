package com.example.sbabchallenge.integration;

import com.example.sbabchallenge.model.BusLinePoint;
import com.example.sbabchallenge.model.TrafiklabParseException;
import com.example.sbabchallenge.model.TrafiklabValidationException;
import com.example.sbabchallenge.model.trafiklab.BusLineData;
import com.example.sbabchallenge.model.trafiklab.ResultRow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TrafiklabIntegration {
    private final ObjectMapper objectMapper;
    private final URI apiEndpoint;
    private final String apiKey;
    private final RestTemplate restTemplate;

    public TrafiklabIntegration(@Qualifier("TrafikLabObjectMapper") ObjectMapper objectMapper,
                                @Qualifier("TrafikLabAPIEndpoint") URI apiEndpoint,
                                @Qualifier("TrafikLabAPIKey") String apiKey,
                                @Qualifier("TrafikLabRestTemplate") RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.apiEndpoint = apiEndpoint;
        this.apiKey = apiKey;
        this.restTemplate = restTemplate;
    }

    public List<BusLinePoint> getTrafiklabLineData() {
        String BUS_API_MODEL = "JourneyPatternPointOfLine";
        BusLineData busLineData = fetch(BUS_API_MODEL);
        return busLineData.responseData().result().stream()
                .map(point -> new BusLinePoint(point.LineNumber(),point.DirectionCode(),point.JourneyPatternPointNumber()))
                .toList();
    }

    public Map<String, String> getStopNames() {
        String STOP_API_MODEL = "stop";
        BusLineData busLineData = fetch(STOP_API_MODEL);
        return busLineData.responseData().result().stream().collect(Collectors.toMap(
                ResultRow::StopPointNumber, ResultRow::StopPointName));
    }


    protected URI getURI(String model){
        String DEFAULT_TRANSPORT_MODE_CODE = "BUS";
        String ENDPOINT_NAME = "linedata.json";
        return UriComponentsBuilder.fromUri(apiEndpoint)
                .pathSegment(ENDPOINT_NAME)
                .queryParam("key",apiKey)
                .queryParam("model", model)
                .queryParam("DefaultTransportModeCode", DEFAULT_TRANSPORT_MODE_CODE)
                .build()
                .toUri();
    }
    private BusLineData fetch(String model) {
        ResponseEntity<String> response = restTemplate.exchange(getURI(model),
                HttpMethod.GET, HttpEntity.EMPTY, String.class);
        String json = response.getBody();
        try {
            return validateAndReturn(objectMapper.readValue(json, BusLineData.class));
        } catch (JsonProcessingException e) {
            throw new TrafiklabParseException(json, e.getMessage(), e);
        }
    }
    private BusLineData validateAndReturn(BusLineData busLineData) {
        if (Objects.isNull(busLineData)||
                Objects.isNull(busLineData.responseData()) ||
                Objects.isNull(busLineData.responseData().result())) {
            throw new TrafiklabValidationException();
        }
        return busLineData;
    }
}
