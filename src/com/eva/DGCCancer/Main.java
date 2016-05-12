package com.eva.DGCCancer;

import com.eva.GSACancer.Cell;
import com.eva.GSACancer.InputData;

import java.io.*;
import java.util.*;

/**
 * Created by eva on 09.05.16.
 */
public class Main {
    public static void main(String[] args) {
        InputData inputData = new InputData();
        if (args.length < 1) {
            System.out.println("You should specify at least 1 filename as argument");
            return;
        }
        if (args[0].endsWith(".pok")) {
            if (args.length < 3) {
                System.out.println("You should specify 3 filenames as arguments for FAM, RMZ and test");
                return;
            }

            loadPokFile(inputData, args[0], Cell.Classification.FAM);
            loadPokFile(inputData, args[1], Cell.Classification.RMZ);
        }
        else if (args[0].endsWith("wdbc.data")) {
            loadWdbcFile(inputData, args[0]);
        }
        else {
            System.out.println("Unidentified file type for " + args[0]);
            return;
        }

        Learner learner = new Learner(inputData);
        System.out.println("Learning...");
        learner.learn();
    }

    private static void loadWdbcFile(InputData inputData, String fileName) {
        System.out.println("Loading Wisconsin Diagnostic Breast Cancer file " + fileName);
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            inputData.loadWdbcFromScanner(bufferedReader);
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadPokFile(InputData inputData, String fileName, Cell.Classification classification) {
        System.out.println("Loading POK file " + fileName + " for " + classification);
        try {
            Scanner sc = new Scanner(new BufferedReader(new FileReader(fileName)));
            inputData.loadPokFromScanner(sc, classification);
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
