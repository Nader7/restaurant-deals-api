package io.github.nader7.dealsapi.mapper;

import io.github.nader7.dealsapi.dto.ActiveDeal;
import io.github.nader7.dealsapi.dto.PeakTimeResponse;
import io.github.nader7.dealsapi.model.ActiveRestaurantDeal;
import io.github.nader7.dealsapi.model.PeakTime;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class DealsResponseMapper {
    private static final String OUTPUT_TIME_FORMAT = "h:mma";

    /**
     * Maps active deal data from the working class ActiveRestaurantDeal to the response DTO ActiveDeal
     * Used by the /deals GET endpoint
     */
    public ActiveDeal toActiveDeal(ActiveRestaurantDeal activeRestaurantDeal) {
        return new ActiveDeal(
                activeRestaurantDeal.restaurant().objectId(),
                activeRestaurantDeal.restaurant().name(),
                activeRestaurantDeal.restaurant().address1(),
                activeRestaurantDeal.restaurant().suburb(),
                formatTime(activeRestaurantDeal.restaurant().open()),
                formatTime(activeRestaurantDeal.restaurant().close()),
                activeRestaurantDeal.deal().objectId(),
                activeRestaurantDeal.deal().discount(),
                activeRestaurantDeal.deal().dineIn(),
                activeRestaurantDeal.deal().lightning(),
                formatQuantity(activeRestaurantDeal.deal().qtyLeft())
        );
    }

    /**
     * Maps LocalTime objects to a String for the peak-time response
     * Used by the /peak-time GET endpoint
     */
    public PeakTimeResponse toPeakTimeResponse(PeakTime peakTime){
        return new PeakTimeResponse(formatTime(peakTime.start()), formatTime(peakTime.end()));
    }


    private String formatTime(LocalTime time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(OUTPUT_TIME_FORMAT, Locale.ENGLISH);
        return time == null ? null : dateTimeFormatter.format(time).toLowerCase(Locale.ENGLISH);
    }

    private String formatQuantity(Integer quantity){
        return quantity == null ? null : Integer.toString(quantity);
    }
}