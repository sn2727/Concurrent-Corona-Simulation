package com.pseuco.np20.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Container for the simulation parameters.
 */
public class Parameters {
    @JsonProperty("coughThreshold")
    private final int coughThreshold;

    @JsonProperty("breathThreshold")
    private final int breathThreshold;

    @JsonProperty("accelerationDivisor")
    private final int accelerationDivisor;

    @JsonProperty("recoveryTime")
    private final int recoveryTime;

    @JsonProperty("infectionRadius")
    private final int infectionRadius;

    @JsonProperty("incubationTime")
    private final int incubationTime;

    /**
     * Constructs a parameters object with the provided information.
     *
     * @param coughThreshold The threshold dermining how often a person coughs.
     * @param breathThreshold The threshold dermining how often a person breaths.
     * @param accelerationDivisor The divisor determining how likely a person is to accelerate.
     * @param recoveryTime The number of ticks a person is infectious before recovering.
     * @param infectionRadius The maximum (non-diagonal) distance the infection can spread directly.
     * @param incubationTime The number of ticks a person is infected before becoming infectious.
     */
    @JsonCreator
    public Parameters(
        @JsonProperty(value = "coughThreshold", required = true)
        final int coughThreshold,
        @JsonProperty(value = "breathThreshold", required = true)
        final int breathThreshold,
        @JsonProperty(value = "accelerationDivisor", required = true)
        final int accelerationDivisor,
        @JsonProperty(value = "recoveryTime", required = true)
        final int recoveryTime,
        @JsonProperty(value = "infectionRadius", required = true)
        final int infectionRadius,
        @JsonProperty(value = "incubationTime", required = true)
        final int incubationTime
    ) {
        this.coughThreshold = coughThreshold;
        this.breathThreshold = breathThreshold;
        this.accelerationDivisor = accelerationDivisor;
        this.recoveryTime = recoveryTime;
        this.infectionRadius = infectionRadius;
        this.incubationTime = incubationTime;
    }

    /**
     * Returns the threshold dermining how often a person coughs.
     *
     * @return The threshold dermining how often a person coughs.
     */
    public int getCoughThreshold() {
        return this.coughThreshold;
    }

    /**
     * Returns the threshold dermining how often a person breaths.
     *
     * @return The threshold dermining how often a person breaths.
     */
    public int getBreathThreshold() {
        return this.breathThreshold;
    }

    /**
     * Returns the divisor determining how likely a person is to accelerate.
     *
     * @return The divisor determining how likely a person is to accelerate.
     */
    public int getAccelerationDivisor() {
        return this.accelerationDivisor;
    }

    /**
     * Returns the number of ticks a person is infectious before recovering.
     *
     * @return The number of ticks a person is infectious before recovering.
     */
    public int getRecoveryTime() {
        return this.recoveryTime;
    }

    /**
     * Returns the maximum (non-diagonal) distance the infection can spread directly.
     *
     * @return The maximum (non-diagonal) distance the infection can spread directly.
     */
    public int getInfectionRadius() {
        return this.infectionRadius;
    }

    /**
     * Returns the number of ticks a person is infected before becoming infectious.
     *
     * @return The number of ticks a person is infected before becoming infectious.
     */
    public int getIncubationTime() {
        return this.incubationTime;
    }
}