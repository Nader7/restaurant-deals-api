package io.github.nader7.dealsapi.mapper;

import io.github.nader7.dealsapi.dto.ActiveDeal;
import io.github.nader7.dealsapi.dto.PeakTimeResponse;
import io.github.nader7.dealsapi.model.ActiveRestaurantDeal;
import io.github.nader7.dealsapi.model.Deal;
import io.github.nader7.dealsapi.model.PeakTime;
import io.github.nader7.dealsapi.model.Restaurant;
import org.junit.jupiter.api.*;

import java.time.LocalTime;
import java.util.List;

class DealsResponseMapperTest {

    private final DealsResponseMapper dealsResponseMapper = new DealsResponseMapper();
    private ActiveRestaurantDeal activeRestaurantDeal = new ActiveRestaurantDeal(null, null);

    @Test
    void testToActiveDeal() {
        activeRestaurantDeal = new ActiveRestaurantDeal(
                createRestaurant(LocalTime.of(15, 0), LocalTime.of(21, 0)),
                createDeal(5)
        );

        Assertions.assertEquals(createActiveDeal("3:00pm", "9:00pm", "5"), dealsResponseMapper.toActiveDeal(activeRestaurantDeal));
    }

    @Test
    void testToActiveDealWithMinutes() {
        activeRestaurantDeal = new ActiveRestaurantDeal(
                createRestaurant(LocalTime.of(11, 14), LocalTime.of(19, 32)),
                createDeal(3)
        );

        Assertions.assertEquals(createActiveDeal("11:14am", "7:32pm", "3"), dealsResponseMapper.toActiveDeal(activeRestaurantDeal));
    }

    @Test
    void testToActiveDealMidnightAndMidday() {
        activeRestaurantDeal = new ActiveRestaurantDeal(
                createRestaurant(LocalTime.of(12, 0), LocalTime.of(0, 0)),
                createDeal(1)
        );

        Assertions.assertEquals(createActiveDeal("12:00pm", "12:00am", "1"), dealsResponseMapper.toActiveDeal(activeRestaurantDeal));
    }

    @Test
    void testToActiveDealMissingQuantity() {
        activeRestaurantDeal = new ActiveRestaurantDeal(
                createRestaurant(LocalTime.of(15, 0), LocalTime.of(21, 0)),
                createDeal(null)
        );

        Assertions.assertEquals(createActiveDeal("3:00pm", "9:00pm", null), dealsResponseMapper.toActiveDeal(activeRestaurantDeal));
    }

    @Test
    void testToActiveDealMissingTimes() {
        activeRestaurantDeal = new ActiveRestaurantDeal(
                createRestaurant(null, null),
                createDeal(5)
        );

        Assertions.assertEquals(createActiveDeal(null, null, "5"), dealsResponseMapper.toActiveDeal(activeRestaurantDeal));
    }

    @Test
    void testToPeakTimeResponse() {
        PeakTime peakTime = new PeakTime(LocalTime.of(18, 0), LocalTime.of(21, 0));

        Assertions.assertEquals(new PeakTimeResponse("6:00pm", "9:00pm"), dealsResponseMapper.toPeakTimeResponse(peakTime));
    }

    @Test
    void testToPeakTimeResponseNoPeakTime() {
        PeakTime peakTime = new PeakTime(null, null);

        Assertions.assertEquals(new PeakTimeResponse(null, null), dealsResponseMapper.toPeakTimeResponse(peakTime));
    }

    @Test
    void testToPeakTimeResponsePeakAllDay() {
        PeakTime peakTime = new PeakTime(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT);

        Assertions.assertEquals(new PeakTimeResponse("12:00am", "12:00am"), dealsResponseMapper.toPeakTimeResponse(peakTime));
    }

    private static Restaurant createRestaurant(LocalTime open, LocalTime close) {
        return new Restaurant(
                "DEA567C5-F64C-3C03-FF00-E3B24909BE00",
                "Masala Kitchen",
                "55 Walsh Street",
                "Lower East",
                open,
                close,
                List.of());
    }

    private static Deal createDeal(Integer qtyLeft) {
        return new Deal(
                "DEA567C5-0000-3C03-FF00-E3B24909BE00",
                "50",
                "false",
                "true",
                LocalTime.of(0, 0),
                LocalTime.of(0, 0),
                qtyLeft);
    }

    private static ActiveDeal createActiveDeal(String open, String close, String qtyLeft) {
        return new ActiveDeal(
                "DEA567C5-F64C-3C03-FF00-E3B24909BE00",
                "Masala Kitchen",
                "55 Walsh Street",
                "Lower East",
                open,
                close,
                "DEA567C5-0000-3C03-FF00-E3B24909BE00",
                "50",
                "false",
                "true",
                qtyLeft);
    }
}