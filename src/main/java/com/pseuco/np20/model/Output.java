package com.pseuco.np20.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The output to be computed by the simulator.
 */
public class Output {
    @JsonProperty("scenario")
    private final Scenario scenario;

    @JsonProperty("trace")
    private final List<TraceEntry> trace;

    @JsonProperty("stats")
    private final Map<String, List<Statistics>> statistics;

    /**
     * Constructs an output with the given information.
     *
     * @param scenario The scenario the output has been computed for.
     * @param trace The trace capturing the population at each tick.
     * @param statistics The computed statistics for each tick.
     */
    public Output(
        @JsonProperty(value = "scenario", required = true)
        final Scenario scenario,
        @JsonProperty(value = "trace", required = true)
        final List<TraceEntry> trace,
        @JsonProperty(value = "stats", required = true)
        final Map<String, List<Statistics>> statistics
    ) {
        this.scenario = scenario;
        this.trace = trace;
        this.statistics = statistics;
    }

    /**
     * Returns the scenario the output has been computed for.
     *
     * @return The scenario the output has been computed for.
     */
    public Scenario getScenario() {
        return this.scenario;
    }

    /**
     * Returns the trace capturing the population at each tick.
     *
     * The trace may be empty if the scenario does not require it's computation.
     *
     * @return The trace capturing the population at each tick.
     */
    public List<TraceEntry> getTrace() {
        return this.trace;
    }

    /**
     * Returns the computed statistics for each tick.
     *
     * @return The computed statistics for each tick.
     */
    public Map<String, List<Statistics>> getStatistics() {
        return this.statistics;
    }
}