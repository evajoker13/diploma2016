package com.eva.GSACancer;

import javax.vecmath.GVector;

/**
 * Created by eva on 4/30/16.
 */
public class Cell {

    public final static int DIM = 15;
    private GVector point;
    private Classification classification;
    public Cell(Classification classification, GVector point){
        this.classification = classification;
        this.point = point;
    }
    public enum Classification {
        Unknown,
        RMZ,
        FAM
    }

    public GVector getPoint() {
        return point;
    }

    public void setPoint(GVector point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "classification=" + classification +
                ", point=" + point +
                '}';
    }
}
