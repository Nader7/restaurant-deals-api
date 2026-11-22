package io.github.nader7.dealsapi.mapper;

import io.github.nader7.dealsapi.dto.DealData;
import io.github.nader7.dealsapi.dto.RestaurantData;
import io.github.nader7.dealsapi.model.Deal;
import io.github.nader7.dealsapi.model.Restaurant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class RestaurantDealsMapperTest {

    @Autowired
    private RestaurantDealsMapper restaurantDealsMapper;

    private RestaurantData inputRestaurantData;
    private Restaurant expectedRestaurant;

    @Test
    void toRestaurantDealWithOpenAndClose() {
        inputRestaurantData = createRestaurantData("3:00pm", "9:00pm", List.of(createDealData("3:00pm", "9:00pm", null, null, "5")));
        expectedRestaurant = expectedRestaurant(
                LocalTime.of(15, 0),
                LocalTime.of(21, 0),
                List.of(expectedDeal(LocalTime.of(15, 0), LocalTime.of(21, 0), 5))
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantDealWithStartAndEnd() {
        inputRestaurantData = createRestaurantData("3:00pm", "9:00pm", List.of(createDealData(null, null, "2:00pm", "10:00pm", "3")));
        expectedRestaurant = expectedRestaurant(
                LocalTime.of(15, 0),
                LocalTime.of(21, 0),
                List.of(expectedDeal(LocalTime.of(14, 0), LocalTime.of(22, 0), 3))
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantDealWithBoth() {
        inputRestaurantData = createRestaurantData("3:00pm", "9:00pm", List.of(createDealData("3:00pm", "9:00pm", "5:00pm", "8:00pm", "5")));
        expectedRestaurant = expectedRestaurant(
                LocalTime.of(15, 0),
                LocalTime.of(21, 0),
                List.of(expectedDeal(LocalTime.of(17, 0), LocalTime.of(20, 0), 5))
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantDealWithNoTimes() {
        inputRestaurantData = createRestaurantData("3:00pm", "9:00pm", List.of(createDealData(null, null, null, null, "4")));
        expectedRestaurant = expectedRestaurant(
                LocalTime.of(15, 0),
                LocalTime.of(21, 0),
                List.of(expectedDeal(null, null, 4))
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantDealWithNoQuantity() {
        inputRestaurantData = createRestaurantData("3:00pm", "9:00pm", List.of(createDealData("3:00pm", "9:00pm", "5:00pm", "8:00pm", null)));
        expectedRestaurant = expectedRestaurant(
                LocalTime.of(15, 0),
                LocalTime.of(21, 0),
                List.of(expectedDeal(LocalTime.of(17, 0), LocalTime.of(20, 0), null))
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantDealWithBlankQuantity() {
        inputRestaurantData = createRestaurantData("3:00pm", "9:00pm", List.of(createDealData("3:00pm", "9:00pm", "5:00pm", "8:00pm", " ")));
        expectedRestaurant = expectedRestaurant(
                LocalTime.of(15, 0),
                LocalTime.of(21, 0),
                List.of(expectedDeal(LocalTime.of(17, 0), LocalTime.of(20, 0), null))
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantDealWithInvalidQuantity() {
        inputRestaurantData = createRestaurantData("3:00pm", "9:00pm", List.of(createDealData(null, null, null, null, "a")));
        expectedRestaurant = expectedRestaurant(
                LocalTime.of(15, 0),
                LocalTime.of(21, 0),
                new ArrayList<>()
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantDealWithUnparseableTime() {
        inputRestaurantData = createRestaurantData("3:00pm", "9:00pm", List.of(
                createDealData("25:00pm", "9:00pm", null, null, "5"),
                createDealData("3:00pm", "9:00pm", null, null, "5")));

        expectedRestaurant = expectedRestaurant(
                LocalTime.of(15, 0),
                LocalTime.of(21, 0),
                List.of(expectedDeal(LocalTime.of(15, 0), LocalTime.of(21, 0), 5))
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantWithNoDeals() {
        inputRestaurantData = createRestaurantData("3:00pm", "9:00pm", null);
        expectedRestaurant = expectedRestaurant(
                LocalTime.of(15, 0),
                LocalTime.of(21, 0),
                new ArrayList<>()
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantWithNoTimes() {
        inputRestaurantData = createRestaurantData(null, null, new ArrayList<>());
        expectedRestaurant = expectedRestaurant(
                null,
                null,
                new ArrayList<>()
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantWithUnparseableTime() {
        inputRestaurantData = createRestaurantData("25:00pm", "9:00pm", new ArrayList<>());

        Assertions.assertEquals(Optional.empty(), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantWithUpperCaseAnd24HourTime() {
        inputRestaurantData = createRestaurantData("3:00PM", "21:00", List.of());
        expectedRestaurant = expectedRestaurant(
                LocalTime.of(15, 0),
                LocalTime.of(21, 0),
                List.of()
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    @Test
    void toRestaurantDealWithExtraSpaces() {
        inputRestaurantData = createRestaurantData(" 3:00pm ", " 9:00pm ", new ArrayList<>());
        expectedRestaurant = expectedRestaurant(
                LocalTime.of(15, 0),
                LocalTime.of(21, 0),
                new ArrayList<>()
        );

        Assertions.assertEquals(Optional.of(expectedRestaurant), restaurantDealsMapper.toRestaurant(inputRestaurantData));
    }

    private static RestaurantData createRestaurantData(String open, String close, List<DealData> deals) {
        return new RestaurantData(
                "DEA567C5-F64C-3C03-FF00-E3B24909BE00",
                "Masala Kitchen",
                "55 Walsh Street",
                "Lower East",
                List.of("Indian"),
                "https://example.com/image.jpg",
                open,
                close,
                deals
        );
    }

    private static DealData createDealData(String open, String close, String start, String end, String qtyLeft) {
        return new DealData(
                "DEA567C5-0000-3C03-FF00-E3B24909BE00",
                "50",
                "false",
                "true",
                open,
                close,
                start,
                end,
                qtyLeft
        );
    }

    private static Restaurant expectedRestaurant(LocalTime open, LocalTime close, List<Deal> deals) {
        return new Restaurant("DEA567C5-F64C-3C03-FF00-E3B24909BE00",
                "Masala Kitchen",
                "55 Walsh Street",
                "Lower East",
                open,
                close,
                deals
        );
    }

    private static Deal expectedDeal(LocalTime start, LocalTime end, Integer qtyLeft) {
        return new Deal("DEA567C5-0000-3C03-FF00-E3B24909BE00",
                "50",
                "false",
                "true",
                start,
                end,
                qtyLeft
        );
    }
}