package io.github.nader7.dealsapi.dto;

import java.util.List;

/**
 * Data Transfer Object used to store all entries of ActiveDeal and used as the response object for the /deals route
 */
public record ActiveDealsResponse(List<ActiveDeal> deals) {
}
