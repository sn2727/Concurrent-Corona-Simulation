package com.pseuco.np20.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents a partion of the grid into patches.
 *
 * A partition is represented by a list of strictly ascending <em>x</em>- and
 * <em>y</em>-coordinates that “cut” the grid into patches.
 */
public class Partition {
    @JsonProperty("x")
    private final List<Integer> x;

    @JsonProperty("y")
    private final List<Integer> y;

    @JsonCreator
    public Partition(
        @JsonProperty(value = "x", required = true)
        final List<Integer> x,
        @JsonProperty(value = "y", required = true)
        final List<Integer> y
    ) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the strictly ascending list of <em>x</em>-coordinates cutting the grid.
     *
     * @return The strictly ascending list of <em>x</em>-coordinates cutting the grid.
     */
    public List<Integer> getX() {
        return this.x;
    }

    /**
     * Returns the strictly ascending list of <em>y</em>-coordinates cutting the grid.
     *
     * @return The strictly ascending list of <em>y</em>-coordinates cutting the grid.
     */
    public List<Integer> getY() {
        return this.y;
    }
}