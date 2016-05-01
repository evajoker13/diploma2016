package com.eva.GSACancer;

import javax.vecmath.GVector;

/**
 * Created by eva on 4/30/16.
 */
public class Cell {
    private GVector point;
    private Classification classification;
    public enum Classification {
        Unknown,
        RMZ,
        FAM
    };
}
