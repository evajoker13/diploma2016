package com.eva.GSACancer;

import javax.vecmath.GVector;

/**
 * Created by eva on 4/30/16.
 */
public class Cell {

    private GVector point;
    public Classification classification;
    public Cell(Classification classification, GVector point){
        this.classification = classification;
        this.point = point;
    }

    public Cell(Classification classification, double x, double y)
    {
        this.classification = classification;
        this.point = new GVector(new double[] {x, y});
    }

    public static Cell fam(double x, double y)
    {
        return new Cell(Classification.FAM, x, y);
    }

    public static Cell rmz(double x, double y)
    {
        return new Cell(Classification.RMZ, x, y);
    }

    public enum Classification {
        Unknown,
        RMZ,
        FAM,
        Unclear
    }

    public GVector getPoint() {
        return point;
    }

    public void setPoint(GVector point) {
        this.point = point;
    }

    @Override
    public Cell clone() {
        GVector vector = new GVector(point);
        return new Cell(classification, vector);
    }

    @Override
    public String toString() {
        return "Cell{" +
                "classification=" + classification +
                ", point=" + point +
                '}';
    }
}
