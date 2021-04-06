package com.pseuco.np20.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents an SI²R-statistics query.
 */
public class Query {
    @JsonProperty("area")
    private final Rectangle area;

    /**
     * Constructs a query over the given area.
     *
     * @param area The area to collect statistics for.
     */
    @JsonCreator
    public Query(
        @JsonProperty(value = "area", required = true)
        final Rectangle area
    ) {
        this.area = area;
    }

    /**
     * Returns the area for which to collect statistics for.
     *
     * @return The area for which to collect statistics for.
     */
    public Rectangle getArea() {
        return this.area;
    }
}