package com.eva.DGCCancer;

import com.eva.GSACancer.Cell;

import javax.vecmath.GVector;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eva on 5/8/16.
 */
public class DataParticle {
    List<Cell> cells = new ArrayList<>();
    public DataParticle(Cell.Classification classification, GVector point) {
        cells.add(new Cell(classification, point));
    }
    public int mass() {
        return cells.size();
    }

}
