package io.github.nader7.dealsapi.client;

import io.github.nader7.dealsapi.dto.RestaurantDealsData;
import io.github.nader7.dealsapi.exception.RestaurantDataUnavailableException;
import io.github.nader7.dealsapi.mapper.RestaurantDealsMapper;
import io.github.nader7.dealsapi.model.Restaurant;
import io.github.nader7.dealsapi.service.RestaurantDealsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

@Component
public class RestaurantDealsClient implements RestaurantDealsProvider {
    private static final Logger log = LoggerFactory.getLogger(RestaurantDealsClient.class);

    private final RestClient restClient;
    private final RestaurantDealsMapper restaurantDealsMapper;
    private final String restaurantDataUrl;

    public RestaurantDealsClient(RestClient restClient, RestaurantDealsMapper restaurantDealsMapper, @Value("${restaurant-deals.data-url}") String restaurantDataUrl){
        this.restClient = restClient;
        this.restaurantDealsMapper = restaurantDealsMapper;
        this.restaurantDataUrl = restaurantDataUrl;
    }

    /**
     * Fetches restaurant and deals data from an external api and transforms them to working objects
     * Entries with an unparsable format are skipped
     */
    @Override
    public List<Restaurant> getRestaurantDeals() {
        RestaurantDealsData restaurantDealsData;

        try{
            log.info("Retrieving restaurant deals data");
            restaurantDealsData = restClient.get()
                    .uri(restaurantDataUrl)
                    .retrieve()
                    .body(RestaurantDealsData.class);

        } catch (RestClientException e){
            throw new RestaurantDataUnavailableException("Failed to get restaurant data", e);
        }

        if (restaurantDealsData == null || restaurantDealsData.restaurants() == null){
            throw new RestaurantDataUnavailableException("Restaurant data API response has no restaurants");
        }

        log.info("Retrieved {} restaurants", restaurantDealsData.restaurants().size());

        return restaurantDealsData.restaurants().stream()
                .map(restaurantDealsMapper::toRestaurant)
                .flatMap(Optional::stream)
                .toList();
    }
}
