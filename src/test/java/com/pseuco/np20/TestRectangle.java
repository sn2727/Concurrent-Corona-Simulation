package com.pseuco.np20;

import static org.junit.Assert.*;

import com.pseuco.np20.model.Rectangle;
import com.pseuco.np20.model.XY;

import org.junit.Test;


public class TestRectangle {
    @Test
    public void testOverlaps() {
        Rectangle base = new Rectangle(new XY(5, 10), new XY(3, 7));
        assertTrue(
            base.overlaps(new Rectangle(new XY(4, 9), new XY(120, 42)))
        );
        assertTrue(
            base.overlaps(new Rectangle(new XY(6, 8), new XY(1, 3)))
        );
        assertFalse(
            base.overlaps(new Rectangle(new XY(6, 8), new XY(1, 2)))
        );
    }

}