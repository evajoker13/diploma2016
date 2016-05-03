package com.eva.GSACancer;

import javax.vecmath.GVector;

import static java.lang.Math.*;

/**
 * Created by eva on 4/30/16.
 */
public class Cluster implements Comparable<Cluster> {
    public GVector center;
    public Cell.Classification classification;
    private int rmzCalc;
    private int famCalc;
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
            rmzCalc++;
            this.classification = classification;
        } else if (this.classification != classification) {
            this.classification = Cell.Classification.Unclear;
        } else {
            ++rmzCalc;
        }
        switch (classification) {
            case RMZ:
                ++rmzCalc;
                break;
            case FAM:
                ++famCalc;
                break;
        }

    }
    public void reset() {
        this.classification = Cell.Classification.Unknown;
        rmzCalc = 0;
        famCalc = 0;
    }
    public double mixtureLevel() {
        if (rmzCalc + famCalc == 0) return 0;
        return abs(rmzCalc - famCalc) / (rmzCalc + famCalc);
    }
}
