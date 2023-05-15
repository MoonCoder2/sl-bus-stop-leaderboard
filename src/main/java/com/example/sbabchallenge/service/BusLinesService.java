package com.example.sbabchallenge.service;

import com.example.sbabchallenge.integration.TrafiklabIntegration;
import com.example.sbabchallenge.model.BusLineDTO;
import com.example.sbabchallenge.model.BusLinePoint;
import com.example.sbabchallenge.model.BusLineStopDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BusLinesService {
    private final TrafiklabIntegration trafiklabIntegration;

    public BusLinesService(TrafiklabIntegration trafiklabIntegration) {
        this.trafiklabIntegration = trafiklabIntegration;
    }

    public List<BusLineDTO> getBusLinesWithMostBusStops(int numberOfRowsToReturn) {
        List<BusLinePoint> list = trafiklabIntegration.getTrafiklabLineData();
        Map<String, String> stopNames= trafiklabIntegration.getStopNames();

        Map<String, List<BusLinePoint>> busLines= mapLineNumberToBusLinePoints(list);

        List<Map.Entry<String, List<BusLinePoint>>> busLinesList = new ArrayList<>(busLines.entrySet());
        busLinesList.sort((o1, o2) -> Integer.compare(o2.getValue().size(), o1.getValue().size()));


        return busLinesList.stream()
                .limit(numberOfRowsToReturn)
                .map(entry -> convertToDTO(entry, stopNames))
                .toList();
    }

    protected BusLineDTO convertToDTO(Map.Entry<String, List<BusLinePoint>> entry, Map<String, String> stopNames) {
        return new BusLineDTO(entry.getKey(), entry.getValue().stream()
                .map(stop -> new BusLineStopDTO(stop.stopId(), stopNames.getOrDefault(stop.stopId(),""),
                        stop.directionCode()))
                .toList());
    }


    protected Map<String, List<BusLinePoint>> mapLineNumberToBusLinePoints(List<BusLinePoint> busLinePoints) {
        Map<String, List<BusLinePoint>> map = new HashMap<>();
        for (BusLinePoint busLinePoint: busLinePoints) {
            List<BusLinePoint> points = map.getOrDefault(busLinePoint.lineNumber(), new ArrayList<>());
            points.add(busLinePoint);
            map.putIfAbsent(busLinePoint.lineNumber(), points);
        }
        return map;
    }





}
