package com.eva.GSACancer;

import com.sun.istack.internal.NotNull;

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

    public int featuresNum() {
        assert cells.size() > 0;
        return cells.get(0).getPoint().getSize();
    }

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

    public static InputData fromCells(List<Cell> cells) {
        InputData inputData = new InputData();
        for (Cell cell : cells) {
            inputData.cells.add(cell);
        }
        return inputData;
    }

    public void loadPokFromScanner(Scanner sc, Cell.Classification classification) {
        final int featuresNum = 15;
        for (;sc.hasNextInt();) {
            int number = sc.nextInt();
            for(int i = 0; i<number; i++){
                double[] values = new double[featuresNum];
                sc.nextInt();
                for(int j = 0; j < featuresNum; j++){
                    values[j] = sc.nextDouble();
                }
                Cell cell = new Cell(classification,new GVector(values));
                cells.add(cell);
            }
        }
    }

    public void findBoundaries(@NotNull GVector lower, @NotNull GVector upper){
        assert cells.size() > 0;

        lower.set(cells.get(0).getPoint());
        upper.set(cells.get(0).getPoint());
        final int featuresNum = lower.getSize();
        for (Cell cell:cells) {
            for (int i = 0; i < featuresNum; i++){
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
