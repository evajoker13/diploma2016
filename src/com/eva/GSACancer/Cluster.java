package com.eva.GSACancer;

import javax.vecmath.GVector;

import static java.lang.Math.sqrt;

/**
 * Created by eva on 4/30/16.
 */
public class Cluster implements Comparable<Cluster> {
    public GVector center;

    public double distance(GVector point) {
        GVector delta = new GVector(center);
        delta.sub(point);
        return sqrt(delta.dot(delta));
    }


    @Override
    public int compareTo(Cluster o) {
        for (int i = 0; i < center.getSize(); i++) {
            int r = Double.compare(center.getElement(i), o.center.getElement(i));
            if(r != 0) return r;
        }
        return 0;
    }
}
