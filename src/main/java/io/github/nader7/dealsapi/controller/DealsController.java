package io.github.nader7.dealsapi.controller;

import io.github.nader7.dealsapi.dto.ActiveDeal;
import io.github.nader7.dealsapi.dto.ActiveDealsResponse;
import io.github.nader7.dealsapi.mapper.ActiveDealsMapper;
import io.github.nader7.dealsapi.service.ActiveDealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class DealsController {
    private static final Logger log = LoggerFactory.getLogger(DealsController.class);

    private final ActiveDealService activeDealService;
    private final ActiveDealsMapper activeDealsMapper;

    public DealsController(ActiveDealService activeDealService, ActiveDealsMapper activeDealsMapper){
        this.activeDealService = activeDealService;
        this.activeDealsMapper = activeDealsMapper;
    }

    /**
     * Returns the deals that are active at the given time of day.
     *
     * @param timeOfDay a 12-hour or 24-hour time value eg: 3:00pm or 15:00
     * @return the active deals, or an empty list if there are none,
     *         400 if timeOfDay is missing or invalid, 502 if the restaurant data is unavailable
     */
    @GetMapping("/deals")
    public ActiveDealsResponse getDeals(@RequestParam String timeOfDay){
        List<ActiveDeal> deals = activeDealService.findActiveDeals(timeOfDay).stream()
                .map(activeDealsMapper::toActiveDeal)
                .toList();

        log.info("Returning {} active deals for timeOfDay={}", deals.size(), timeOfDay);
        return new ActiveDealsResponse(deals);
    }
}
