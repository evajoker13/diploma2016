package com.eva.GSACancer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

/**
 * Created by eva on 5/1/16.
 */
public class Main {
    static public void main(String args[]){
        InputData inputData = new InputData();
        try {
            Scanner sc = new Scanner(new BufferedReader(new FileReader("/home/eva/workspaceEclipse/GSACancer/data/a.pok")));
            inputData.loadFromScanner(sc, Cell.Classification.FAM);
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        try {
            Scanner sc = new Scanner(new BufferedReader(new FileReader("/home/eva/workspaceEclipse/GSACancer/data/b.pok")));
            inputData.loadFromScanner(sc, Cell.Classification.RMZ);
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        //inputData.getCells().forEach(System.out::println);

        Learner learner = new Learner(inputData);
        learner.learn();

        System.out.println("Testing..");
        InputData newInputData = new InputData();

        try {
            Scanner sc = new Scanner(new BufferedReader(new FileReader("/home/eva/workspaceEclipse/GSACancer/data/c.pok")));
            newInputData.loadFromScanner(sc, Cell.Classification.Unknown);
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        Learner.Agent agent = learner.bestAgent();

        //System.out.println(newInputData.getCells());
        int famQ = 0;
        int rmzQ = 0;
        List<Cell> cells = newInputData.getCells();
        for (int i = 0; i < cells.size(); i++) {
            System.out.println(cells.get(i));
            Cluster cluster = agent.classify(cells.get(i));
            if (i < 10 && cluster.estimateClassification() == Cell.Classification.FAM) ++famQ;
            if (i >= 10 && cluster.estimateClassification() == Cell.Classification.RMZ) ++rmzQ;
            System.out.println(cluster.estimateClassification());
            System.out.println(cluster.estimationConfidence());
            System.out.println(cluster);
        }
        System.out.println(famQ * 10 + "% <--- fam");
        System.out.println(rmzQ * 10 + "% <--- rmz");
    }
}
