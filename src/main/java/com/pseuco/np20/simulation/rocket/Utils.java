package com.pseuco.np20.simulation.rocket;

import java.util.Iterator;

import com.pseuco.np20.model.Rectangle;
import com.pseuco.np20.model.Scenario;
import com.pseuco.np20.model.XY;

/**
 * Some useful utilities for the concurrent implementation.
 */
public class Utils {
    static private class PatchesIterator implements Iterator<Rectangle> {
        private final Scenario scenario;

        private final Iterator<Integer> yIterator;

        private final int maxX;
        private final int maxY;

        private Iterator<Integer> xIterator;

        private int currentY = 0;

        private int lastX = 0;
        private int lastY = 0;

        public PatchesIterator(final Scenario scenario) {
            this.scenario = scenario;
            this.yIterator = this.scenario.getPartition().getY().iterator();
            this.maxX = this.scenario.getGridSize().getX();
            this.maxY = this.scenario.getGridSize().getY();
            this.xIterator = this.scenario.getPartition().getX().iterator();
            if (this.yIterator.hasNext()) {
                this.currentY = this.yIterator.next();
            } else {
                this.currentY = this.maxY;
            }
        }

        @Override
        public boolean hasNext() {
            return this.lastY != this.maxY;
        }

        @Override
        public Rectangle next() {
            assert this.hasNext();
            if (!this.xIterator.hasNext()) {
                final XY topLeft = new XY(this.lastX, this.lastY);
                final XY bottomRight = new XY(this.maxX, this.currentY);
                this.lastY = this.currentY;
                if (this.yIterator.hasNext()) {
                    this.currentY = this.yIterator.next();
                } else {
                    this.currentY = this.maxY;
                }
                this.xIterator = this.scenario.getPartition().getX().iterator();
                this.lastX = 0;
                return new Rectangle(topLeft, bottomRight.sub(topLeft));
            }
            final int currentX = this.xIterator.next();
            final XY topLeft = new XY(this.lastX, this.lastY);
            final XY bottomRight = new XY(currentX, this.currentY);
            this.lastX = currentX;
            return new Rectangle(topLeft, bottomRight.sub(topLeft));
        }
    }


    /**
     * Returns an iterator iterating over the patches specified in the scenario.
     *
     * @param scenario The scenario.
     * @return An iterator over the patches.
     */
    static public Iterator<Rectangle> getPatches(Scenario scenario) {
        return new PatchesIterator(scenario);
    }
}