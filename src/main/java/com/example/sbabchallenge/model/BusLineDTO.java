package com.example.sbabchallenge.model;

import java.util.List;

public record BusLineDTO(String busNumber, List<BusLineStopDTO> stops) {
}
