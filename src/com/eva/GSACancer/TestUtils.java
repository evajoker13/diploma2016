package com.eva.GSACancer;

import javax.vecmath.GVector;

/**
 * Created by eva on 5/3/16.
 */
public class TestUtils {
        public static GVector vec(double x, double y) {
            GVector v = new GVector(Cell.DIM);
            v.zero();
            v.setElement(0, x);
            v.setElement(1, y);
            return v;
        }
}
