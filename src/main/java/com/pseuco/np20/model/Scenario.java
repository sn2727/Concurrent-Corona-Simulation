package com.pseuco.np20.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents a simulation scenario.
 */
public class Scenario {
    @JsonProperty(value = "name")
    private final String name;

    @JsonProperty(value = "parameters")
    private final Parameters parameters;

    @JsonProperty(value = "ticks")
    private final int ticks;

    @JsonProperty(value = "gridSize")
    private final XY gridSize;

    @JsonProperty(value = "trace")
    private final boolean trace;

    @JsonProperty(value = "partition")
    private final Partition partition;

    @JsonProperty(value = "obstacles")
    private final List<Rectangle> obstacles;

    @JsonProperty("statQueries")
    final Map<String, Query> queries;

    @JsonProperty(value = "population")
    private final List<PersonInfo> population;

    /**
     * Constructs a scenario with the provided information.
     *
     * @param name The name of the scenario.
     * @param parameters The simulation parameters of the scenario.
     * @param ticks The amount of ticks to simulate.
     * @param gridSize The size of the grid of the simulation.
     * @param trace Whether a full trace should be captured.
     * @param partition The partition of the grid into patches.
     * @param obstacles The obstacles on the grid.
     * @param queries The statistic queries to compute.
     * @param population The population of the scenario.
     */
    @JsonCreator
    public Scenario(
        @JsonProperty(value = "name", required = true)
        final String name,
        @JsonProperty(value = "parameters", required = true)
        final Parameters parameters,
        @JsonProperty(value = "ticks", required = true)
        final int ticks,
        @JsonProperty(value = "gridSize", required = true)
        final XY gridSize,
        @JsonProperty(value = "trace", required = true)
        final boolean trace,
        @JsonProperty(value = "partition", required = true)
        final Partition partition,
        @JsonProperty(value = "obstacles", required = true)
        final List<Rectangle> obstacles,
        @JsonProperty(value = "statQueries", required = true)
        final Map<String, Query> queries,
        @JsonProperty(value = "population", required = true)
        final List<PersonInfo> population
    ) {
        this.name = name;
        this.parameters = parameters;
        this.ticks = ticks;
        this.gridSize = gridSize;
        this.trace = trace;
        this.partition = partition;
        this.obstacles = obstacles;
        this.queries = queries;
        this.population = population;
    }

    /**
     * Returns the name of the scenario.
     *
     * @return The name of the scenario.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the simulation parameters of the scenario.
     *
     * @return The simulation parameters of the scenario.
     */
    public Parameters getParameters() {
        return this.parameters;
    }

    /**
     * Returns the amount of ticks to simulate.
     *
     * @return The amount of ticks to simulate.
     */
    public int getTicks() {
        return this.ticks;
    }

    /**
     * Returns the size of the grid of the simulation.
     *
     * @return The size of the grid of the simulation.
     */
    public XY getGridSize() {
        return this.gridSize;
    }

    /**
     * Returns whether a full trace should be captured.
     *
     * @return Whether a full trace should be captured.
     */
    public boolean getTrace() {
        return this.trace;
    }

    /**
     * Returns the partition of the grid into patches.
     *
     * @return The partition of the grid into patches.
     */
    public Partition getPartition() {
        return this.partition;
    }

    /**
     * Returns the obstacles on the grid.
     *
     * @return The obstacles on the grid.
     */
    public List<Rectangle> getObstacles() {
        return this.obstacles;
    }

    /**
     * Returns the statistic queries to compute.
     *
     * @return The statistic queries to compute.
     */
    public Map<String, Query> getQueries() {
        return this.queries;
    }

    /**
     * Returns the population of the scenario.
     *
     * @return The population of the scenario.
     */
    public List<PersonInfo> getPopulation() {
        return this.population;
    }

    /**
     * Returns the grid as a rectangle.
     *
     * @return The grid as a rectangle.
     */
    @JsonIgnore
    public Rectangle getGrid() {
        return new Rectangle(XY.ZERO, this.getGridSize());
    }

    /**
     * Returns the number of patches.
     *
     * @return The number of patches.
     */
    @JsonIgnore
    public int getNumberOfPatches() {
        return (this.partition.getX().size() + 1) * (this.partition.getY().size() + 1);
    }

    /**
     * Checks whether there is an obstacle on the given cell.
     *
     * @param cell The cell to check for obstacles.
     * @return Whether there is an obstacle on the given cell.
     */
    public boolean onObstacle(XY cell) {
        for (Rectangle obstacle : this.obstacles) {
            if (obstacle.contains(cell)) {
                return true;
            }
        }
        return false;
    }
}