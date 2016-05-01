package com.eva.GSACancer;

import javax.vecmath.GVector;

import static java.lang.Math.sqrt;

/**
 * Created by eva on 4/30/16.
 */
public class Cluster {
    public GVector center;

    public double distance(GVector point) {
        GVector delta = new GVector(center);
        delta.sub(point);
        return sqrt(delta.dot(delta));
    }

}
