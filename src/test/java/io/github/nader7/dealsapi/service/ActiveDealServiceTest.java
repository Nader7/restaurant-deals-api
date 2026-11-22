package io.github.nader7.dealsapi.service;

import io.github.nader7.dealsapi.dto.RestaurantDealsData;
import io.github.nader7.dealsapi.exception.RestaurantDataUnavailableException;
import io.github.nader7.dealsapi.mapper.RestaurantDealsMapper;
import io.github.nader7.dealsapi.model.Deal;
import io.github.nader7.dealsapi.model.Restaurant;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class ActiveDealServiceTest {

    @Autowired
    private ActiveDealService activeDealService;

    @Autowired
    private RestaurantDealsMapper restaurantDealsMapper;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private RestaurantDealsProvider restaurantDealsProvider;

    @Value("classpath:DealsApiResponse.json")
    private Resource dealsApiResponse;

    private List<Restaurant> restaurantDeals;

    @BeforeEach
    void beforeEach() throws IOException {
        RestaurantDealsData data = jsonMapper.readValue(dealsApiResponse.getInputStream(), RestaurantDealsData.class);

        restaurantDeals = data.restaurants().stream()
                .map(restaurantDealsMapper::toRestaurant)
                .flatMap(Optional::stream)
                .toList();
    }

    @Test
    void findActiveDealsAt3PM() {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurantDeals);

        List<String> dealIds = activeDealService.findActiveDeals("3:00pm").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertEquals(
                List.of(
                        "DEA567C5-0000-3C03-FF00-E3B24909BE00", // Masala Kitchen
                        "DEA567C5-1111-3C03-FF00-E3B24909BE00", // Masala Kitchen
                        "D80263E8-0000-2C70-FF6B-D854ADB8DB00", // ABC Chicken
                        "D80263E8-1111-2C70-FF6B-D854ADB8DB00", // ABC Chicken
                        "B5713CD0-0000-40C7-AFC3-7D46D26B00BF"  // Kekou
                ), dealIds);
    }

    @Test
    void findActiveDealsAt6PM() {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurantDeals);

        List<String> dealIds = activeDealService.findActiveDeals("6:00pm").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertEquals(
                List.of(
                        "DEA567C5-0000-3C03-FF00-E3B24909BE00", // Masala Kitchen
                        "DEA567C5-1111-3C03-FF00-E3B24909BE00", // Masala Kitchen
                        "D80263E8-0000-2C70-FF6B-D854ADB8DB00", // ABC Chicken
                        "D80263E8-1111-2C70-FF6B-D854ADB8DB00", // ABC Chicken
                        "CDB2B42A-0000-EE20-FF45-8D0A8057E200", // Vrindavan
                        "B5713CD0-0000-40C7-AFC3-7D46D26B00BF", // Kekou
                        "B5713CD0-1111-40C7-AFC3-7D46D26B00BF", // Kekou
                        "B5913CD0-0000-40C7-AFC3-7D46D26B01BF", // Gyoza Gyoza
                        "B5713CD0-1111-40C7-AFC3-7D46D26B00BF"  // Gyoza Gyoza
                ), dealIds);
    }

    @Test
    void findActiveDealsAt9PM() {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurantDeals);

        List<String> dealIds = activeDealService.findActiveDeals("9:00pm").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertEquals(
                List.of(
                        "D80263E8-0000-2C70-FF6B-D854ADB8DB00", // ABC Chicken
                        "D80263E8-1111-2C70-FF6B-D854ADB8DB00", // ABC Chicken
                        "B5913CD0-0000-40C7-AFC3-7D46D26B01BF", // Gyoza Gyoza
                        "B5713CD0-1111-40C7-AFC3-7D46D26B00BF"  // Gyoza Gyoza
                ), dealIds);
    }

    @Test
    void findActiveDealsWithMinutes() {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurantDeals);

        List<String> dealIds = activeDealService.findActiveDeals("10:30am").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertEquals(
                List.of(
                        "B5913CD0-0550-40C7-AFC3-7D46D26B01BF", // OzzyThai Cafe Bar
                        "B5713CD0-1361-40C7-AFC3-7D46D26B00BF"  // OzzyThai Cafe Bar
                ), dealIds);
    }

    @Test
    void findActiveDealsWith24HourTime() {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurantDeals);

        List<String> dealIds = activeDealService.findActiveDeals("15:00").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertEquals(
                List.of(
                        "DEA567C5-0000-3C03-FF00-E3B24909BE00", // Masala Kitchen
                        "DEA567C5-1111-3C03-FF00-E3B24909BE00", // Masala Kitchen
                        "D80263E8-0000-2C70-FF6B-D854ADB8DB00", // ABC Chicken
                        "D80263E8-1111-2C70-FF6B-D854ADB8DB00", // ABC Chicken
                        "B5713CD0-0000-40C7-AFC3-7D46D26B00BF"  // Kekou, 2:00pm deal
                ), dealIds);
    }

    @Test
    void findActiveDealsBeforeOpening() {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurantDeals);

        List<String> dealIds = activeDealService.findActiveDeals("13:59").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertFalse(dealIds.contains("B5713CD0-0000-40C7-AFC3-7D46D26B00BF")); //Kekou - deal opens 2pm
    }

    @Test
    void findActiveDealsOnOpening() {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurantDeals);

        List<String> dealIds = activeDealService.findActiveDeals("14:00").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertTrue(dealIds.contains("B5713CD0-0000-40C7-AFC3-7D46D26B00BF")); //Kekou - deal opens 2pm
    }

    @Test
    void findActiveDealsBeforeRestaurantOpens() {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurantDeals);

        List<String> dealIds = activeDealService.findActiveDeals("17:00").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertFalse(dealIds.contains("CDB2B42A-0000-EE20-FF45-8D0A8057E200")); //Vrindavan - deal opens 3pm restaurant opens 6pm
    }

    @Test
    void findActiveDealsOvernightPM() {
        List<Restaurant> restaurants = List.of(createRestaurant("ID-123-123", LocalTime.of(22, 0), LocalTime.of(4, 0),
                List.of(createDeal("DEAL-1", null, null, 5))));
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurants);

        List<String> dealIds = activeDealService.findActiveDeals("11:00pm").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertEquals(List.of("DEAL-1"), dealIds); //opens 10pm - 4am
    }

    @Test
    void findActiveDealsOvernightAM() {
        List<Restaurant> restaurants = List.of(createRestaurant("ID-123-123", LocalTime.of(22, 0), LocalTime.of(4, 0),
                List.of(createDeal("DEAL-1", null, null, 5))));
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurants);

        List<String> dealIds = activeDealService.findActiveDeals("2:00").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertEquals(List.of("DEAL-1"), dealIds); //opens 10pm - 4am
    }

    @Test
    void findActiveDealsOvernightPMFail() {
        List<Restaurant> restaurants = List.of(createRestaurant("ID-123-123", LocalTime.of(23, 30), LocalTime.of(4, 0),
                List.of(createDeal("DEAL-1", null, null, 5))));
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurants);

        List<String> dealIds = activeDealService.findActiveDeals("11:00pm").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertEquals(new ArrayList<>(), dealIds); //opens 10pm - 4am
    }

    @Test
    void findActiveDealsOvernightAMFail() {
        List<Restaurant> restaurants = List.of(createRestaurant("ID-123-123", LocalTime.of(22, 30), LocalTime.of(4, 0),
                List.of(createDeal("DEAL-1", null, null, 5))));
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurants);

        List<String> dealIds = activeDealService.findActiveDeals("5:00").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertEquals(new ArrayList<>(), dealIds); //opens 10pm - 4am
    }

    @Test
    void findActiveDealsSoldOut() {
        List<Restaurant> restaurants = List.of(createRestaurant("ID-123-123", LocalTime.of(17, 0), LocalTime.of(22, 0),
                List.of(createDeal("DEAL-1", LocalTime.of(17, 30), LocalTime.of(20, 0), 0))));
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurants);

        List<String> dealIds = activeDealService.findActiveDeals("19:00").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertEquals(new ArrayList<>(), dealIds);
    }

    @Test
    void findActiveDealsUnlimitedQuantity() {
        List<Restaurant> restaurants = List.of(createRestaurant("ID-123-123", LocalTime.of(17, 0), LocalTime.of(22, 0),
                List.of(createDeal("DEAL-1", LocalTime.of(17, 30), LocalTime.of(20, 0), null))));
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurants);

        List<String> dealIds = activeDealService.findActiveDeals("19:00").stream()
                .map(activeDeal -> activeDeal.deal().objectId())
                .toList();

        Assertions.assertEquals(List.of("DEAL-1"), dealIds);
    }

    @Test
    void findActiveDealsInvalidTime() {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenReturn(restaurantDeals);
        Assertions.assertThrows(DateTimeParseException.class, () -> activeDealService.findActiveDeals("a"));
    }

    @Test
    void findActiveDealsFailedApiCall() {
        Mockito.when(restaurantDealsProvider.getRestaurantDeals()).thenThrow(new RestaurantDataUnavailableException("Failed to get restaurant data"));
        Assertions.assertThrows(RestaurantDataUnavailableException.class, () -> activeDealService.findActiveDeals("15:00"));
    }

    private static Restaurant createRestaurant(String objectId, LocalTime open, LocalTime close, List<Deal> deals) {
        return new Restaurant(
                objectId,
                "test name",
                "1 address",
                "test",
                open,
                close,
                deals);
    }

    private static Deal createDeal(String objectId, LocalTime start, LocalTime end, Integer qtyLeft) {
        return new Deal(
                objectId,
                "50",
                "false",
                "true",
                start,
                end,
                qtyLeft);
    }
}