package com.pseuco.np20.model;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents SI²R-statistics at some point in time.
 */
public class Statistics {
    @JsonProperty(value = "susceptible")
    private final long susceptible;

    @JsonProperty(value = "infected")
    private final long infected;

    @JsonProperty(value = "infectious")
    private final long infectious;

    @JsonProperty(value = "recovered")
    private final long recovered;

    /**
     * Constructs an SI²R-statistics object with the given numbers.
     *
     * @param susceptible The number of susceptible persons.
     * @param infected The number of infected persons.
     * @param infectious The number of infectious persons.
     * @param recovered The number of recovered persons.
     */
    public Statistics(
        @JsonProperty(value = "susceptible", required = true)
        final long susceptible,
        @JsonProperty(value = "infected", required = true)
        final long infected,
        @JsonProperty(value = "infectious", required = true)
        final long infectious,
        @JsonProperty(value = "recovered", required = true)
        final long recovered
    ) {
        this.susceptible = susceptible;
        this.infected = infected;
        this.infectious = infectious;
        this.recovered = recovered;
    }

    /**
     * Returns the number of susceptible persons.
     *
     * @return The number of susceptible persons.
     */
    public long getSusceptible() {
        return this.susceptible;
    }

    /**
     * Returns the number of infected persons.
     *
     * @return The number of infected persons.
     */
    public long getInfected() {
        return this.infected;
    }

    /**
     * Returns the number of infectious persons.
     *
     * @return The number of infectious persons.
     */
    public long getInfectious() {
        return this.infectious;
    }

    /**
     * Returns the number of recovered persons.
     *
     * @return The number of recovered persons.
     */
    public long getRecovered() {
        return this.recovered;
    }

    @Override
    public String toString() {
        return String.format(
            "Statistics(%d, %d, %d, %d)",
            this.susceptible,
            this.infected,
            this.infectious,
            this.recovered
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Statistics)) {
            return false;
        }
        final Statistics otherStatistics = (Statistics) other;
        return (
            this.susceptible == otherStatistics.susceptible
            && this.infected == otherStatistics.infected
            && this.infectious == otherStatistics.infectious
            && this.recovered == otherStatistics.recovered
        );
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31 * hash + Long.hashCode(this.susceptible);
        hash = 31 * hash + Long.hashCode(this.infected);
        hash = 31 * hash + Long.hashCode(this.infectious);
        hash = 31 * hash + Long.hashCode(this.recovered);
        return hash;
    }
}