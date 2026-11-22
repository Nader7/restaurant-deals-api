package io.github.nader7.dealsapi.controller;

import io.github.nader7.dealsapi.dto.RestaurantDealsData;
import io.github.nader7.dealsapi.exception.RestaurantDataUnavailableException;
import io.github.nader7.dealsapi.mapper.RestaurantDealsMapper;
import io.github.nader7.dealsapi.model.Restaurant;
import io.github.nader7.dealsapi.service.RestaurantDealsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.Resource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DealsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private RestaurantDealsProvider restaurantDealsProvider;

    @Value("classpath:ActiveDealsResponse.json")
    private Resource activeDealsResponse;

    @Value("classpath:DealsApiResponse.json")
    private Resource dealsApiResponse;

    @Value("classpath:PeakTimeResponse.json")
    private Resource peakTimeResponse;

    @Value("classpath:PeakTimeResponseNoDeals.json")
    private Resource peakTimeResponseNoDeals;

    @Autowired
    private RestaurantDealsMapper restaurantDealsMapper;

    private List<Restaurant> restaurantDeals;

    @BeforeEach
    void beforeEach() throws Exception {
        RestaurantDealsData data = jsonMapper.readValue(dealsApiResponse.getInputStream(), RestaurantDealsData.class);

        restaurantDeals = data.restaurants().stream()
                .map(restaurantDealsMapper::toRestaurant)
                .flatMap(Optional::stream)
                .toList();
    }

    @Test
    void getDeals() throws Exception {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurantDeals);

        mockMvc.perform(get("/v1/deals").param("timeOfDay", "3:00pm"))
                .andExpect(status().isOk())
                .andExpect(content().json(activeDealsResponse.getContentAsString(StandardCharsets.UTF_8), JsonCompareMode.STRICT));
    }

    @Test
    void getDealsInvalidTimeOfDay() throws Exception {
        mockMvc.perform(get("/v1/deals").param("timeOfDay", "a"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDealsMissingTimeOfDay() throws Exception {
        mockMvc.perform(get("/v1/deals"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDealsRestaurantDataUnavailable() throws Exception {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenThrow(new RestaurantDataUnavailableException("Failed to get restaurant data"));

        mockMvc.perform(get("/v1/deals").param("timeOfDay", "3:00pm"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getPeakTime() throws Exception {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurantDeals);

        mockMvc.perform(get("/v1/peak-time"))
                .andExpect(status().isOk())
                .andExpect(content().json(peakTimeResponse.getContentAsString(StandardCharsets.UTF_8), JsonCompareMode.STRICT));
    }

    @Test
    void getPeakTimeNoActiveDeals() throws Exception {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/v1/peak-time"))
                .andExpect(status().isOk())
                .andExpect(content().json(peakTimeResponseNoDeals.getContentAsString(StandardCharsets.UTF_8), JsonCompareMode.STRICT));
    }

    @Test
    void getPeakTimeRestaurantDataUnavailable() throws Exception {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenThrow(new RestaurantDataUnavailableException("Failed to get restaurant data"));

        mockMvc.perform(get("/v1/peak-time"))
                .andExpect(status().isBadGateway());
    }
}