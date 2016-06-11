package com.eva.DGCCancer;

import com.eva.GSACancer.Cell;
import com.eva.GSACancer.InputData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;

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
        System.out.println("Result:" + learner.learn());

        Learner.ClassificationQuality avgResult =
            Collections.nCopies(10, null)
                    .stream()
                    .map(x -> learner.findWeights())
                    .reduce(null, Main::aggregateClassificationQuality);
        System.out.println(avgResult);
    }

    private static Learner.ClassificationQuality aggregateClassificationQuality(Learner.ClassificationQuality a, Learner.ClassificationQuality b) {
        if (a == null) return b;
        if (b == null) return a;
        Learner.ClassificationQuality c = new Learner.ClassificationQuality();
        c.total = a.total + b.total;
        c.totalFAM = a.totalFAM + b.totalFAM;
        c.totalRMZ = a.totalRMZ + b.totalRMZ;
        c.missRatio = (a.missRatio*a.total + b.missRatio*b.total) / c.total;
        c.missRatioFAM = (a.missRatioFAM*a.totalFAM + b.missRatioFAM*b.totalFAM) / c.totalFAM;
        c.missRatioRMZ = (a.missRatioRMZ*a.totalRMZ + b.missRatioRMZ*b.totalRMZ) / c.totalRMZ;
        return c;
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
            BufferedReader source = new BufferedReader(new FileReader(fileName));
            Scanner sc = new Scanner(source).useLocale(Locale.ENGLISH);
//            inputData.loadPokFromScanner(sc, classification);
            inputData.loadStatPokFromScanner(sc, classification);
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
