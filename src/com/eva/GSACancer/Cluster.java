package com.eva.GSACancer;

import javax.vecmath.GVector;

/**
 * Created by eva on 4/30/16.
 */
public class Cluster implements Comparable<Cluster> {
    public GVector center;
    public Cell.Classification classification;
    public Cluster() {
        center = new GVector(Cell.DIM);
    }

    public double distance(GVector point) {
        GVector delta = new GVector(center);
        delta.sub(point);
        return delta.norm();
    }


    @Override
    public int compareTo(Cluster o) {
        for (int i = 0; i < center.getSize(); i++) {
            int r = Double.compare(center.getElement(i), o.center.getElement(i));
            if(r != 0) return r;
        }
        return 0;
    }

    public boolean within(GVector lower, GVector upper) {
        for (int i = 0; i < center.getSize(); i++) {
            if (center.getElement(i) < lower.getElement(i)) {
                     return false;
            } else if (center.getElement(i) > upper.getElement(i)) {
                     return false;
            }
        }
        return true;
    }

    public void update(Cell.Classification classification) {
        if (this.classification == Cell.Classification.Unknown) {
            this.classification = classification;
        } else if (this.classification != classification) {
            this.classification = Cell.Classification.Unclear;
        }
    }
}
