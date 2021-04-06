package com.pseuco.np20.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents the population at some point in time.
 */
public class TraceEntry {
    @JsonProperty(value = "population")
    private final List<PersonInfo> population;

    /**
     * Constructs a trace entry with the given population.
     *
     * @param population The population of the entry.
     */
    public TraceEntry(
        @JsonProperty(value = "population", required = true)
        final List<PersonInfo> population
    ) {
        this.population = population;
    }

    /**
     * Returns the population of the entry.
     *
     * @return The population of the entry.
     */
    public List<PersonInfo> getPopulation() {
        return this.population;
    }
}