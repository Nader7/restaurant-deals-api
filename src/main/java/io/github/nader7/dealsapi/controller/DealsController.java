package io.github.nader7.dealsapi.controller;

import io.github.nader7.dealsapi.dto.ActiveDeal;
import io.github.nader7.dealsapi.dto.ActiveDealsResponse;
import io.github.nader7.dealsapi.dto.PeakTimeResponse;
import io.github.nader7.dealsapi.mapper.DealsResponseMapper;
import io.github.nader7.dealsapi.model.PeakTime;
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
    private final DealsResponseMapper dealsResponseMapper;

    public DealsController(ActiveDealService activeDealService, DealsResponseMapper dealsResponseMapper){
        this.activeDealService = activeDealService;
        this.dealsResponseMapper = dealsResponseMapper;
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
                .map(dealsResponseMapper::toActiveDeal)
                .toList();

        log.info("Returning {} active deals for timeOfDay={}", deals.size(), timeOfDay);
        return new ActiveDealsResponse(deals);
    }

    /**
     * Returns the time window during which most deals are active.
     *
     * @return the start and end time when most deals are active and null if there are no active deals
     */
    @GetMapping("/peak-time")
    public PeakTimeResponse getPeakTime(){
        PeakTime peakTime = activeDealService.findPeakTime();

        return dealsResponseMapper.toPeakTimeResponse(peakTime);
    }
}
