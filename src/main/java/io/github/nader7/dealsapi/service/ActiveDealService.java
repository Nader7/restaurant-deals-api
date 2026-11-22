package io.github.nader7.dealsapi.service;

import io.github.nader7.dealsapi.model.ActiveRestaurantDeal;
import io.github.nader7.dealsapi.model.Deal;
import io.github.nader7.dealsapi.model.Restaurant;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ActiveDealService {

    private final DateTimeFormatter dateTimeFormatter;

    private final RestaurantDealsProvider restaurantDealsProvider;

    public ActiveDealService(RestaurantDealsProvider restaurantDealsProvider, DateTimeFormatter dateTimeFormatter){
        this.dateTimeFormatter = dateTimeFormatter;
        this.restaurantDealsProvider = restaurantDealsProvider;
    }

    /**
     * Matches the current time with active restaurant deals and retrieves them
     */
    public List<ActiveRestaurantDeal> findActiveDeals(String timeOfDay){
        LocalTime currentTime = LocalTime.parse(timeOfDay, dateTimeFormatter);

        List<Restaurant> restaurantDeals = restaurantDealsProvider.getRestaurantDeals();

        return restaurantDeals.stream()
                .flatMap(restaurant -> restaurant.deals()
                        .stream().filter(deal -> isActive(restaurant, deal, currentTime))
                        .map(deal -> new ActiveRestaurantDeal(restaurant, deal)))
                        .toList();
    }

    /**
     * Checks whether the deal has run out and if the time is valid for the current restaurant and deal
     */
    private boolean isActive(Restaurant restaurant, Deal deal, LocalTime currentTime){
        boolean isSoldOut = deal.qtyLeft() != null && deal.qtyLeft() < 1;

        return !isSoldOut &&
                isWithin(currentTime, restaurant.open(), restaurant.close()) &&
                isWithin(currentTime, deal.start(), deal.end());
    }

    /**
     * Checks whether the current time sits within a deals and restaurants active windows
     * Accounts for 24h windows and overnight windows
     */
    private boolean isWithin(LocalTime time, LocalTime open, LocalTime close ){
        //open 24 hours
        if(open == null && close == null){
            return true;
        }

        // only one given = invalid window
        if (open == null || close == null) {
            return false;
        }

        //open 24 hours
        if (open.equals(close)) {
            return true;
        }

        //overnight
        if(!open.isBefore(close)){
            return !time.isBefore(open) || time.isBefore(close);
        }

        //same day
        return !time.isBefore(open) && time.isBefore(close);
    }
}
