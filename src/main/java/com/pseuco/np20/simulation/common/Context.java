package com.pseuco.np20.simulation.common;

import java.util.List;

import com.pseuco.np20.model.Rectangle;


/**
 * Represents a context a person is simulated in.
 *
 * <p>
 * Hint: You may want your patch class to implement this interface.
 * </p>
 */
public interface Context {
    /**
     * Returns the grid of the scenario the person is simulated in.
     *
     * @return The grid of the scenario the person is simulated in.
     */
    Rectangle getGrid();

    /**
     * Returns the obstacles to be considered when simulating the person.
     *
     * @return The obstacles to be considered when simulating the person.
     */
    List<Rectangle> getObstacles();

    /**
     * Returns all persons to be considered when simulating the person.
     *
     * @return All persons to be considered when simulating the person.
     */
    List<Person> getPopulation();
}