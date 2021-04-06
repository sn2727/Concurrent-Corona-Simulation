package com.pseuco.np20.simulation.common;

import java.util.HashSet;
import java.util.Set;

import com.pseuco.np20.model.Rectangle;
import com.pseuco.np20.model.Scenario;
import com.pseuco.np20.model.XY;

/**
 * Some utility functions you may find useful.
 */
public class Utils {
    /**
     * Computes whether it is possible to propagate information from a <em>source area</em>
     * to a <em>target area</em> after an arbitrary amount of ticks.
     *
     * You may use this method to check whether it is possible to propagate information from
     * the padding of a patch inside the area owned by the patch. If you do not want to use
     * this method make sure your method is as least as precise as this method.
     *
     * For those who would like to earn a bonus: In some cases this method returns that
     * information may propagate although on closer inspection this is not the case. What
     * are those cases? Can you improve on that?
     *
     * @param scenario The scenario to check for obstacles and use the parameters from.
     * @param source The <em>source area</em> for which to check the propagation possibility.
     * @param target The <em>target area</em> for which to check the propagation possibility.
     * @return Whether information may propagate from the <em>source</em> to the <em>target area</em>.
     */
    static public boolean mayPropagateFrom(
        final Scenario scenario,
        final Rectangle source,
        final Rectangle target
    ) {
        final Set<XY> region = new HashSet<>();
        final Set<XY> frontier = new HashSet<>();
        for (final XY targetCell : target) {
            if (!scenario.onObstacle(targetCell)) {
                frontier.add(targetCell);
            }
        }
        final int infectionRadius = scenario.getParameters().getInfectionRadius();
        while (!frontier.isEmpty()) {
            final XY cell = frontier.iterator().next();
            frontier.remove(cell);
            region.add(cell);
            for (int deltaX = -infectionRadius; deltaX <= infectionRadius; deltaX++) {
                for (int deltaY = -infectionRadius; deltaY <= infectionRadius; deltaY++) {
                    if (
                        Math.abs(deltaX) + Math.abs(deltaY) <= infectionRadius
                        || (Math.abs(deltaX) <= 1 && Math.abs(deltaY) <= 1)
                    ) {
                        final XY neighbor = cell.add(deltaX, deltaY);
                        if (
                            !region.contains(neighbor)
                            && scenario.getGrid().contains(neighbor)
                            && !scenario.onObstacle(neighbor)
                        ) {
                            frontier.add(neighbor);
                        }
                    }
                }
            }
        }
        for (final XY sourceCell : source) {
            if (scenario.onObstacle(sourceCell)) {
                continue;
            }
            if (region.contains(sourceCell)) {
                return true;
            }
        }

        return false;
    }
}