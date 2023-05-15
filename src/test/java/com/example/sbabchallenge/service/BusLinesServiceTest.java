package com.example.sbabchallenge.service;

import com.example.sbabchallenge.integration.TrafiklabIntegration;
import com.example.sbabchallenge.model.BusLineDTO;
import com.example.sbabchallenge.model.BusLinePoint;
import com.example.sbabchallenge.model.BusLineStopDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusLinesServiceTest {

    @Mock
    TrafiklabIntegration trafiklabIntegration;

    BusLinesService busLinesService;

    final BusLinePoint BUS_LINE_POINT_EXAMPLE_L1_01 = new BusLinePoint("1", "1", "01");
    final BusLinePoint BUS_LINE_POINT_EXAMPLE_L1_02 = new BusLinePoint("1", "1", "02");

    @BeforeEach
    void setUp() {
        trafiklabIntegration = Mockito.mock(TrafiklabIntegration.class);
        busLinesService = new BusLinesService(trafiklabIntegration);
    }

    @Test
    void convertToDTO() {
        Map<String, List<BusLinePoint>> busLinePointMap = new HashMap<>();
        busLinePointMap.put("1", List.of(BUS_LINE_POINT_EXAMPLE_L1_01, BUS_LINE_POINT_EXAMPLE_L1_02));
        var testEntry = busLinePointMap.entrySet().stream().findFirst().orElseThrow();

        Map<String, String> stopNames = new HashMap<>();
        String Centralen = "Centralen";
        stopNames.put(BUS_LINE_POINT_EXAMPLE_L1_01.stopId(), Centralen);
        BusLineDTO busLineDTO = busLinesService.convertToDTO(testEntry, stopNames);

        assertEquals(BUS_LINE_POINT_EXAMPLE_L1_01.lineNumber(), busLineDTO.busNumber());
        assertEquals(2, busLineDTO.stops().size());

        //String id, String name, String direction
        var expectedBusLineDTO= List.of(new BusLineStopDTO(BUS_LINE_POINT_EXAMPLE_L1_01.stopId(),
                        Centralen,
                BUS_LINE_POINT_EXAMPLE_L1_02.directionCode()),
                new BusLineStopDTO(BUS_LINE_POINT_EXAMPLE_L1_02.stopId(),
                        "",
                        BUS_LINE_POINT_EXAMPLE_L1_02.directionCode()
                        )
                );
        assertTrue(busLineDTO.stops().containsAll(expectedBusLineDTO));
    }

    @Test
    void getBusLinesWithMostBusStopsNoRows() {
        Mockito.when(trafiklabIntegration.getTrafiklabLineData())
                .thenReturn(new ArrayList<>());
        Mockito.when(trafiklabIntegration.getStopNames())
                .thenReturn(new HashMap<>());
        List<BusLineDTO> b = busLinesService.getBusLinesWithMostBusStops(1);
        assertEquals(0 , busLinesService.getBusLinesWithMostBusStops(10).size());
    }

    @Test
    void getBusLinesWithMostBusStops() {

        Mockito.when(trafiklabIntegration.getTrafiklabLineData())
                .thenReturn(List.of(BUS_LINE_POINT_EXAMPLE_L1_01));

        Map<String, String> stopNames = new HashMap<>();
        String Centralen = "Centralen";
        stopNames.put(BUS_LINE_POINT_EXAMPLE_L1_01.stopId(), Centralen);

        Mockito.when(trafiklabIntegration.getStopNames())
                .thenReturn(stopNames);

        List<BusLineDTO> result = busLinesService.getBusLinesWithMostBusStops(1);
        assertEquals(1 , result.size());
        var dto = result.stream().findFirst().orElseThrow();

        var expectedDTO = new BusLineDTO(BUS_LINE_POINT_EXAMPLE_L1_01.lineNumber(),
                List.of(new BusLineStopDTO(
                        BUS_LINE_POINT_EXAMPLE_L1_01.stopId(),
                        Centralen, BUS_LINE_POINT_EXAMPLE_L1_01.directionCode())));
        assertEquals(expectedDTO,  dto);

    }

}