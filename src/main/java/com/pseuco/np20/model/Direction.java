package com.pseuco.np20.model;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents a direction of movement.
 */
public enum Direction {
    @JsonProperty("N") NORTH(0, -1),
    @JsonProperty("E") EAST(1, 0),
    @JsonProperty("S") SOUTH(0, 1),
    @JsonProperty("W") WEST(-1, 0),
    @JsonProperty("NE") NORTH_EAST(1, -1),
    @JsonProperty("NW") NORTH_WEST(-1, -1),
    @JsonProperty("SE") SOUTH_EAST(1, 1),
    @JsonProperty("SW") SOUTH_WEST(-1, 1),
    @JsonProperty("X") NONE(0, 0);

    private final XY vector;

    private Direction(final int x, final int y) {
        this.vector = new XY(x, y);
    }

    /**
     * Converts a vector into a direction.
     *
     * @return The direction corresponding to the vector.
     */
    public static Direction fromVector(XY vector) {
        switch (vector.getX()) {
            case -1:
                switch (vector.getY()) {
                    case -1:
                        return Direction.NORTH_WEST;
                    case 0:
                        return Direction.WEST;
                    case 1:
                        return Direction.SOUTH_WEST;
                }
                break;
            case 0:
                switch (vector.getY()) {
                    case -1:
                        return Direction.NORTH;
                    case 0:
                        return Direction.NONE;
                    case 1:
                        return Direction.SOUTH;
                }
                break;
            case 1:
                switch (vector.getY()) {
                    case -1:
                        return Direction.NORTH_EAST;
                    case 0:
                        return Direction.EAST;
                    case 1:
                        return Direction.SOUTH_EAST;
                }
                break;
        }

        throw new RuntimeException(
            "invalid direction vector " + vector
        );
    }

    /**
     * Returns the direction as a vector.
     *
     * @return The direction as a vector.
     */
    public XY getVector() {
        return this.vector;
    }
}