package io.github.nader7.dealsapi.mapper;

import io.github.nader7.dealsapi.dto.DealData;
import io.github.nader7.dealsapi.dto.RestaurantData;
import io.github.nader7.dealsapi.model.Deal;
import io.github.nader7.dealsapi.model.Restaurant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RestaurantDealsMapper {
    private static final Logger log = LoggerFactory.getLogger(RestaurantDealsMapper.class);

    private final DateTimeFormatter dateTimeFormatter;

    public RestaurantDealsMapper(DateTimeFormatter dateTimeFormatter){
        this.dateTimeFormatter = dateTimeFormatter;
    }

    /**
     * Maps received restaurant data to the working object Restaurant skipping invalid restaurant entries
     */
    public Optional<Restaurant> toRestaurant(RestaurantData restaurantData) {
        try {

            List<DealData> deals = restaurantData.deals() == null ? new ArrayList<>() : restaurantData.deals();

            return Optional.of(new Restaurant(
                    restaurantData.objectId(),
                    restaurantData.name(),
                    restaurantData.address1(),
                    restaurantData.suburb(),
                    parseTime(restaurantData.open()),
                    parseTime(restaurantData.close()),
                    deals.stream().map(this::toDeal).flatMap(Optional::stream).toList()
            ));

        } catch (DateTimeParseException e) {
            log.warn("Skipping restaurant {}: {}", restaurantData.objectId(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Maps received deals data to the working object Deal skipping invalid deals entries
     */
    private Optional<Deal> toDeal(DealData dealData) {
        try {

            //Remove once API bug is resolved <Dependency EPIC ID or bug ID>
            String open = dealData.start() != null ? dealData.start() : dealData.open();
            String close = dealData.end() != null ? dealData.end() : dealData.close();

            return Optional.of(new Deal(
                    dealData.objectId(),
                    dealData.discount(),
                    dealData.dineIn(),
                    dealData.lightning(),
                    parseTime(open),
                    parseTime(close),
                    parseQuantity(dealData.qtyLeft())
            ));

        } catch (DateTimeParseException | NumberFormatException e) {
            log.warn("Skipping deal {}: {}", dealData.objectId(), e.getMessage());
            return Optional.empty();
        }
    }

    private LocalTime parseTime(String time){
        if (time == null || time.isBlank()) {
            return null;
        }

        return LocalTime.parse(time.strip(), dateTimeFormatter);
    }

    private Integer parseQuantity(String quantity){
        if (quantity == null || quantity.isBlank()) {
            return null;
        }

        return Integer.parseInt(quantity.strip());
    }
}
