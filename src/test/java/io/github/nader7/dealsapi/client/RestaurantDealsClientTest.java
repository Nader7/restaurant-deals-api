package io.github.nader7.dealsapi.client;

import io.github.nader7.dealsapi.exception.RestaurantDataUnavailableException;
import io.github.nader7.dealsapi.model.Restaurant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest(properties = "restaurant-deals.data-url=http://test.test/restaurants")
@AutoConfigureMockRestServiceServer
class RestaurantDealsClientTest {

    @Autowired
    private RestaurantDealsClient restaurantDealsClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Value("${restaurant-deals.data-url}")
    private String restaurantDataUrl;

    @Value("classpath:DealsApiResponse.json")
    private Resource dealsApiResponse;

    @Value("classpath:DealsApiResponseExtraFields.json")
    private Resource dealsApiResponseExtraFields;

    @Test
    void getRestaurantDeals() {
        mockServer.expect(requestTo(restaurantDataUrl)).andRespond(withSuccess(dealsApiResponse, MediaType.APPLICATION_JSON));

        Assertions.assertEquals(6, restaurantDealsClient.getRestaurantDeals().size());
    }

    @Test
    void getRestaurantDealsExtraFields() {
        mockServer.expect(requestTo(restaurantDataUrl)).andRespond(withSuccess(dealsApiResponseExtraFields, MediaType.APPLICATION_JSON));

        List<Restaurant> restaurants = restaurantDealsClient.getRestaurantDeals();

        Assertions.assertEquals(6, restaurants.size());
        Assertions.assertEquals("Masala Kitchen", restaurants.getFirst().name());
        Assertions.assertEquals(LocalTime.of(14, 0), restaurants.get(3).deals().getFirst().start()); // checks Kekou start time
    }

    @Test
    void getRestaurantDealsServerError() {
        mockServer.expect(requestTo(restaurantDataUrl)).andRespond(withServerError());

        Assertions.assertThrows(RestaurantDataUnavailableException.class, () -> restaurantDealsClient.getRestaurantDeals());
    }

    @Test
    void getRestaurantDealsNotFound() {
        mockServer.expect(requestTo(restaurantDataUrl)).andRespond(withResourceNotFound());

        Assertions.assertThrows(RestaurantDataUnavailableException.class, () -> restaurantDealsClient.getRestaurantDeals());
    }

    @Test
    void getRestaurantDealsMissingData() {
        mockServer.expect(requestTo(restaurantDataUrl)).andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        Assertions.assertThrows(RestaurantDataUnavailableException.class, () -> restaurantDealsClient.getRestaurantDeals());
    }

    @Test
    void getRestaurantDealsEmptyRestaurants() {
        mockServer.expect(requestTo(restaurantDataUrl)).andRespond(withSuccess("{\"restaurants\": []}", MediaType.APPLICATION_JSON));

        Assertions.assertEquals(List.of(), restaurantDealsClient.getRestaurantDeals());
    }
}