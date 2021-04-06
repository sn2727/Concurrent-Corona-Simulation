package com.pseuco.np20.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents a two-dimensional vector with an <em>x</em>- and a <em>y</em>-component.
 *
 * Useful for all kinds of calculations with coordinates, velocities, et cetera.
 */
public class XY {
    @JsonProperty(value = "x")
    private final int x;

    @JsonProperty(value = "y")
    private final int y;

    /**
     * The <em>origin</em>, i.e., a vector with both components set to zero.
     */
    public static XY ZERO = new XY(0, 0);

    /**
     * Constructs a vector with the given <em>x</em>- and <em>y</em>-components.
     *
     * @param x The <em>x</em>-component.
     * @param y The <em>y</em>-component.
     */
    @JsonCreator
    public XY(
        @JsonProperty(value = "x", required = true)
        final int x,
        @JsonProperty(value = "y", required = true)
        final int y
    ) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the <em>x</em>-component of the vector.
     *
     * @return The <em>x</em>-component of the vector.
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the <em>y</em>-component of the vector.
     *
     * @return The <em>y</em>-component of the vector.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Adds the specified scalars to the respective components of the vector.
     *
     * @param deltaX The scalar to add to the <em>x</em>-component.
     * @param deltaY The scalar to add to the <em>y</em>-component.
     * @return The resulting vector.
     */
    public XY add(int deltaX, int deltaY) {
        return new XY(this.x + deltaX, this.y + deltaY);
    }

    /**
     * Adds the specified vector to this vector.
     *
     * @param delta The vector to add to this vector.
     * @return The resulting vector.
     */
    public XY add(XY delta) {
        return new XY(this.x + delta.x, this.y + delta.y);
    }

    /**
     * Adds the specified scalar to both components of the vector.
     *
     * @param delta The scalar to add to both components.
     * @return The resulting vector.
     */
    public XY add(int delta) {
        return new XY(this.x + delta, this.y + delta);
    }

    /**
     * Substracts the specified scalars from the respective components of the vector.
     *
     * @param deltaX The scalar to substract from the <em>x</em>-component.
     * @param deltaY The scalar to substract from the <em>y</em>-component.
     * @return The resulting vector.
     */
    public XY sub(int deltaX, int deltaY) {
        return new XY(this.x - deltaX, this.y - deltaY);
    }

    /**
     * Substracts the specified vector from this vector.
     *
     * @param delta The vector to substract from this vector.
     * @return The resulting vector.
     */
    public XY sub(XY delta) {
        return new XY(this.x - delta.x, this.y - delta.y);
    }

    /**
     * Substracts the specified scalar from both components of the vector.
     *
     * @param delta The scalar to substract from both components.
     * @return The resulting vector.
     */
    public XY sub(int delta) {
        return new XY(this.x - delta, this.y - delta);
    }

    /**
     * Limits the values of the respective components to the specified ranges.
     *
     * @param min A vector with the minima for the respective components.
     * @param max A vector with the maxima for the respective components.
     * @return The resulting vector.
     */
    public XY limit(XY min, XY max) {
        return new XY(
            Math.max(Math.min(this.x, max.x), min.x),
            Math.max(Math.min(this.y, max.y), min.y)
        );
    }

    /**
     * Limits the value of both components to the specified range.
     *
     * @param min The minimum.
     * @param max The maximum.
     * @return The resulting vector.
     */
    public XY limit(int min, int max) {
        return new XY(
            Math.max(Math.min(this.x, max), min),
            Math.max(Math.min(this.y, max), min)
        );
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof XY)) {
            return false;
        }
        XY otherXY = (XY) other;
        return this.x == otherXY.x && this.y == otherXY.y;
    }

    @Override
    public int hashCode() {
        return 31 * this.x + this.y;
    }
}