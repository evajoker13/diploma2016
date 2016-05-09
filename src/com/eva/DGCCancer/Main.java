package com.eva.DGCCancer;

import com.eva.GSACancer.Cell;
import com.eva.GSACancer.InputData;

import java.io.*;
import java.util.*;

/**
 * Created by nikolay on 09.05.16.
 */
public class Main {
    public static void main(String[] args) {
        InputData inputData = new InputData();
        if (args.length < 3) {
            System.out.println("You should specify 3 filenames as arguments for FAM, RMZ and test");
            return;
        }

        loadFile(inputData, args[0], Cell.Classification.FAM);
        loadFile(inputData, args[1], Cell.Classification.RMZ);

        Learner learner = new Learner(inputData);
        System.out.println("Learning...");
        learner.learn();
    }

    private static void loadFile(InputData inputData, String fileName, Cell.Classification classification) {
        System.out.println(("Loading file " + fileName));
        try {
            Scanner sc = new Scanner(new BufferedReader(new FileReader(fileName)));
            inputData.loadFromScanner(sc, classification);
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
