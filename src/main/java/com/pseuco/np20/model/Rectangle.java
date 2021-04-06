package com.pseuco.np20.model;

import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents a rectangle and provides some convenient operations on rectangles.
 */
public class Rectangle implements Iterable<XY> {
    private class CellIterator implements Iterator<XY> {
        private final Rectangle rectangle;

        private int nextX;
        private int nextY;

        public CellIterator(final Rectangle rectangle) {
            this.rectangle = rectangle;
            this.nextX = this.rectangle.getTopLeft().getX();
            this.nextY = this.rectangle.getTopLeft().getY();
        }

        @Override
        public boolean hasNext() {
            return this.nextY < this.rectangle.getBottomRight().getY();
        }

        @Override
        public XY next() {
            assert this.hasNext();
            final XY result = new XY(this.nextX, this.nextY);
            this.nextX += 1;
            if (this.nextX >= this.rectangle.getBottomRight().getX()) {
                this.nextX = this.rectangle.getTopLeft().getX();
                this.nextY += 1;
            }
            return result;
        }

    }

    @JsonProperty("topLeft")
    private final XY topLeft;

    @JsonIgnore
    private final XY bottomRight;

    @JsonProperty("size")
    private final XY size;

    /**
     * Constructs a rectangle of the specified size at the given position.
     *
     * @param topLeft The top-left coordinate of the rectangle.
     * @param size The size of the rectangle.
     */
    @JsonCreator
    public Rectangle(
        @JsonProperty(value = "topLeft", required = true)
        final XY topLeft,
        @JsonProperty(value = "size", required = true)
        final XY size
    ) {
        this.topLeft = topLeft;
        this.bottomRight = topLeft.add(size);
        this.size = size;
    }

    /**
     * Returns the top-left coordinate of the rectangle.
     *
     * @return The top-left coordinate of the rectangle.
     */
    public XY getTopLeft() {
        return this.topLeft;
    }

    /**
     * Returns the bottom-right coordinate of the rectangle.
     *
     * <p>
     * Note that the cell located at the bottom-right coordinate is not
     * considered a part of the rectangle.
     * </p>
     *
     * @return The bottom-right coordinate of the rectangle.
     */
    public XY getBottomRight() {
        return this.bottomRight;
    }

    /**
     * Returns the size of the rectangle as a vector.
     *
     * @return The size of the rectangle.
     */
    public XY getSize() {
        return this.size;
    }

    /**
     * Checks whether two rectangles overlap.
     *
     * @param other The other rectangle.
     * @return Whether the two rectangles overlap.
     */
    public boolean overlaps(Rectangle other) {
        return !(
             this.bottomRight.getX() <= other.topLeft.getX()
             || other.bottomRight.getX() <= this.topLeft.getX()
             || this.topLeft.getY() >= other.bottomRight.getY()
             || other.topLeft.getY() >= this.bottomRight.getY()
        );
    }

    /**
     * Computes the intersection of the rectangle with another rectangle.
     *
     * <p>
     * Preconditions: The rectangles should overlap.
     * </p>
     *
     * <p>
     * Hint: You may want to use this method to compute which parts of the area owned
     * by a patch need to be synchronized with the padding of another patch.
     * </p>
     *
     * @param other The other rectangle.
     * @return The intersection of both rectangles.
     */
    public Rectangle intersect(Rectangle other) {
        assert this.overlaps(other);
        XY topLeft = new XY(
            Math.max(this.topLeft.getX(), other.topLeft.getX()),
            Math.max(this.topLeft.getY(), other.topLeft.getY())
        );
        XY bottomRight = new XY(
            Math.min(this.bottomRight.getX(), other.bottomRight.getX()),
            Math.min(this.bottomRight.getY(), other.bottomRight.getY())
        );
        XY size = bottomRight.sub(topLeft);
        return new Rectangle(topLeft, size);
    }

    /**
     * Returns whether the given cell is contained in the rectangle.
     *
     * @param cell The cell to check.
     * @return Whether the given cell is contained in the rectangle.
     */
    public boolean contains(XY cell) {
        return (
            this.topLeft.getX() <= cell.getX()
            && cell.getX() < this.bottomRight.getX()
            && this.topLeft.getY() <= cell.getY()
            && cell.getY() < this.bottomRight.getY()
        );
    }

    /**
     * Returns an iterator over the cells of the rectangle.
     *
     * @return An iterator over the cells of the rectangle.
     */
    public Iterator<XY> iterator() {
        return new CellIterator(this);
    }

    @Override
    public String toString() {
        return "Rectangle(" + this.topLeft + ", " + this.size + ")";
    }
}