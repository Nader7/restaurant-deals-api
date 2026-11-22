package io.github.nader7.dealsapi.service;

import io.github.nader7.dealsapi.model.ActiveRestaurantDeal;
import io.github.nader7.dealsapi.model.Deal;
import io.github.nader7.dealsapi.model.PeakTime;
import io.github.nader7.dealsapi.model.Restaurant;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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
     * Used by the /deals endpoint
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
     * Finds the time window during which the most deals are active
     * Window length is dynamic and based on the reference data
     * Returns null if there are no active deals, or midnight to midnight if the peak lasts all day
     * Used by the /peak-time endpoint
     */
    public PeakTime findPeakTime(){
        List<Restaurant> restaurants = restaurantDealsProvider.getRestaurantDeals();

        // Gathers all start and end boundaries into a list making sure each value is unique and not null
        // Deals with a start or end time outside the restaurant's opening hours will be counted here as a boundary
        // This boundary won't be returned as it will never be the earliest boundary with the most active deals, nor will it be the latest boundary with a change in active deals
        List<LocalTime> boundaries = restaurants.stream()
                .flatMap(restaurant -> Stream.concat(Stream.of(restaurant.open(), restaurant.close()),
                        restaurant.deals().stream().flatMap(deal -> Stream.of(deal.start(), deal.end()))))
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        // No times on deals or restaurants are counted as active 24/7
        if (boundaries.isEmpty()) {
            boolean anyActive = countActiveDeals(restaurants, LocalTime.MIDNIGHT) > 0;
            return anyActive ? new PeakTime(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT) : new PeakTime(null, null);
        }

        // At each boundary check how many active deals there are and store it
        int boundariesCount = boundaries.size();
        long[] dealCounts = new long[boundariesCount];
        long peakDeals = 0;
        for (int i = 0; i < boundariesCount; i++) {
            dealCounts[i] = countActiveDeals(restaurants, boundaries.get(i));
            peakDeals = Math.max(peakDeals, dealCounts[i]);
        }

        //If there are no active deals at all
        if (peakDeals == 0) {
            return new PeakTime(null, null);
        }

        // Find the boundary with the most deals and use it as the start boundary
        // This accounts for multiple boundaries with the same amount of deals, starting at the first
        int startTimeIndex = 0;
        int previous = boundariesCount - 1; // Allows for overnight window
        for (int i = 0; i < boundariesCount; i++) {
            if (dealCounts[i] == peakDeals && dealCounts[previous] != peakDeals) {
                startTimeIndex = i;
                break;
            }
            previous = i;
        }

        // Finds the next boundary where the amount of deals is not equal to the peak deals time
        int endTimeIndex = startTimeIndex;
        for (int i = 0; i < boundariesCount; i++) {
            endTimeIndex = (endTimeIndex + 1) % boundariesCount; // Allows index to loop to the start of the list
            if(dealCounts[endTimeIndex] != peakDeals) {
                break;
            }
        }

        // If the peak lasts all day return midnight to midnight
        return endTimeIndex == startTimeIndex ?
                new PeakTime(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT) :
                new PeakTime(boundaries.get(startTimeIndex), boundaries.get(endTimeIndex));
    }

    private long countActiveDeals(List<Restaurant> restaurants, LocalTime time) {
        return restaurants.stream()
                .flatMap(restaurant -> restaurant.deals().stream()
                        .filter(deal -> isActive(restaurant, deal, time)))
                .count();
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
