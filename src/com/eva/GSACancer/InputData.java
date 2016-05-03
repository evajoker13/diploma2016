package com.eva.GSACancer;

import javax.vecmath.GVector;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by eva on 4/30/16.
 *
 *
 */
public class InputData {
    private List<Cell> cells = new ArrayList<>();

    public List<Cell> getCells() {
        return cells;
    }

    public static InputData fromCells(Cell[] cells) {
        InputData inputData = new InputData();
        for (Cell cell : cells) {
            inputData.cells.add(cell);
        }
        return inputData;
    }

    public void loadFromScanner(Scanner sc, Cell.Classification classification) {
        for (;sc.hasNextInt();) {
            int number = sc.nextInt();
            for(int i = 0; i<number; i++){
                double[] values = new double[Cell.DIM];
                sc.nextInt();
                for(int j = 0; j<Cell.DIM; j++){
                    values[j] = sc.nextDouble();
                }
                Cell cell = new Cell(classification,new GVector(values));
                cells.add(cell);
            }
        }
    }

    public void findBoundaries(GVector lower, GVector upper){
        lower.set(cells.get(0).getPoint());
        upper.set(cells.get(0).getPoint());
        for (Cell cell:cells) {
            for (int i = 0; i<Cell.DIM; i++){
                double value = cell.getPoint().getElement(i);
                if (value < lower.getElement(i)) {
                    lower.setElement(i, value);
                }
                if (value > upper.getElement(i)) {
                    upper.setElement(i, value);
                }
            }
        }
    }
}
