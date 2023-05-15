package com.example.sbabchallenge.integration;

import com.example.sbabchallenge.model.BusLinePoint;
import com.example.sbabchallenge.model.TrafiklabParseException;
import com.example.sbabchallenge.model.TrafiklabValidationException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrafiklabIntegrationTest {
    private final URI API_ENDPOINT = new URI("http://test.com");
    private final String API_KEY="KEY";
    private final ObjectMapper OBJECT_MAPPER;
    @Mock
    private RestTemplate restTemplate;
    private TrafiklabIntegration trafiklabIntegration;
    TrafiklabIntegrationTest() throws URISyntaxException {
        this.OBJECT_MAPPER = new Jackson2ObjectMapperBuilder()
                .failOnUnknownProperties(false)
                .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .build();
    }


    @BeforeEach
    void setUp() {
        restTemplate= Mockito.mock(RestTemplate.class);
        trafiklabIntegration  = new TrafiklabIntegration(OBJECT_MAPPER, API_ENDPOINT, API_KEY,  restTemplate);
    }



    @Test
    void getURI() {
        String model = "testModel";
        assertEquals(API_ENDPOINT+"/linedata.json?key="+API_KEY+"&model="+model+"&DefaultTransportModeCode=BUS",
                trafiklabIntegration.getURI(model).toString());
    }
    @Test
    void getStopNamesEmptyResult() {
        String jsonBody = """
                {
                    "StatusCode": 0,
                    "Message": null,
                    "ExecutionTime": 378,
                    "ResponseData": {
                        "Version": "2023-05-15 00:12",
                        "Type": "StopPoint",
                        "Result": []
                    }
                }    
                """;
        String model = "stop";
        URI uri = trafiklabIntegration.getURI(model);
        Mockito.when(restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY, String.class))
                        .thenReturn(new ResponseEntity<>(jsonBody,
                                HttpStatus.OK));

        Map<String, String> stopNames =  trafiklabIntegration.getStopNames();
        assertEquals(0, stopNames.size());
    }
    @Test
    void getStopNamesMissingResult() {
        String jsonBody = """
                {
                    "StatusCode": 0,
                    "Message": null,
                    "ExecutionTime": 378,
                    "ResponseData": {
                        "Version": "2023-05-15 00:12",
                        "Type": "StopPoint"
                    }
                }    
                """;
        String model = "stop";
        URI uri = trafiklabIntegration.getURI(model);
        Mockito.when(restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY, String.class))
                .thenReturn(new ResponseEntity<>(jsonBody,
                        HttpStatus.OK));

        assertThrows(TrafiklabValidationException.class, () -> {
            trafiklabIntegration.getStopNames();
        });

    }

    @Test
    void getStopNamesOneResult() {
        String jsonBody = """
                {
                    "StatusCode": 0,
                    "Message": null,
                    "ExecutionTime": 378,
                    "ResponseData": {
                        "Version": "2023-05-15 00:12",
                        "Type": "StopPoint",
                        "Result": [
                        {
                            "StopPointNumber": "10001",
                            "StopPointName": "Stadshagsplan",
                            "StopAreaNumber": "10001",
                            "LocationNorthingCoordinate": "59.3373571967995",
                            "LocationEastingCoordinate": "18.0214674159693",
                            "ZoneShortName": "A",
                            "StopAreaTypeCode": "BUSTERM",
                            "LastModifiedUtcDateTime": "2022-10-28 00:00:00.000",
                            "ExistsFromDate": "2022-10-28 00:00:00.000"       
                        }]
                    }
                }    
                """;
        String model = "stop";
        URI uri = trafiklabIntegration.getURI(model);
        Mockito.when(restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY, String.class))
                .thenReturn(new ResponseEntity<>(jsonBody,
                        HttpStatus.OK));

        Map<String, String> stopNames =  trafiklabIntegration.getStopNames();
        assertEquals(1, stopNames.size());
        assertTrue(stopNames.containsKey("10001"));
        assertEquals("Stadshagsplan", stopNames.get("10001"));
    }

    @Test
    void getTrafiklabLineData() {
        String jsonBody = """
        {
        "StatusCode": 0,
        "Message": null,
        "ExecutionTime": 396,
        "ResponseData": {
            "Version": "2023-05-12 00:11",
            "Type": "JourneyPatternPointOnLine",
            "Result": [
                {
                    "LineNumber": "1",
                    "DirectionCode": "2",
                    "JourneyPatternPointNumber": "10008",
                    "LastModifiedUtcDateTime": "2022-02-15 00:00:00.000",
                    "ExistsFromDate": "2022-02-15 00:00:00.000"
                }
                ]
            }
        }
         """;
        String model = "JourneyPatternPointOfLine";
        URI uri = trafiklabIntegration.getURI(model);
        Mockito.when(restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY, String.class))
                .thenReturn(new ResponseEntity<>(jsonBody,
                        HttpStatus.OK));

        List<BusLinePoint> busLinePoints =  trafiklabIntegration.getTrafiklabLineData();
        assertEquals(1, busLinePoints.size());
        BusLinePoint busLinePoint = busLinePoints.get(0);
        assertEquals("1", busLinePoint.lineNumber());
        assertEquals("2", busLinePoint.directionCode());
        assertEquals("10008", busLinePoint.stopId());
    }

    @Test
    void getTrafiklabLineDataInvalidJSON() {
        String jsonBody = "a,b";
        String model = "JourneyPatternPointOfLine";
        URI uri = trafiklabIntegration.getURI(model);
        Mockito.when(restTemplate.exchange(uri, HttpMethod.GET, HttpEntity.EMPTY, String.class))
                .thenReturn(new ResponseEntity<>(jsonBody,
                        HttpStatus.OK));

        assertThrows(TrafiklabParseException.class, () -> {
            trafiklabIntegration.getTrafiklabLineData();
        });
    }

}