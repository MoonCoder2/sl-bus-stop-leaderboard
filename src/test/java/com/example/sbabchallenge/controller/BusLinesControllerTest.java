package com.example.sbabchallenge.controller;

import com.example.sbabchallenge.model.TrafiklabException;
import com.example.sbabchallenge.model.TrafiklabValidationException;
import com.example.sbabchallenge.service.BusLinesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BusLinesController.class)
class BusLinesControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    BusLinesService busLinesService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    void getBusLinesWithMostBusStops() throws Exception {
        Mockito.when(busLinesService.getBusLinesWithMostBusStops(10)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/bus-lines/most-bus-stops")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header()
                        .string("Cache-Control","max-age=30, must-revalidate, no-transform"));
    }

    @Test
    void getBusLinesWithMostBusStopsErrorHandling() throws Exception {
        Mockito.when(busLinesService.getBusLinesWithMostBusStops(10))
                .thenThrow(new TrafiklabException(HttpStatus.TOO_MANY_REQUESTS));

        mockMvc.perform(get("/api/bus-lines/most-bus-stops")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Trafiklab did not return 2XX status, status=429 TOO_MANY_REQUESTS"));
    }

    @Test
    void getBusLinesWithMostBusStopsValidationException() throws Exception {
        Mockito.when(busLinesService.getBusLinesWithMostBusStops(10))
                .thenThrow(new TrafiklabValidationException());

        mockMvc.perform(get("/api/bus-lines/most-bus-stops")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Could not parse response from Trafiklab"));
    }
    //
}