package com.eva.GSACancer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
        for (Cell cell : newInputData.getCells()) {
            System.out.println(cell);
            Cluster cluster = agent.classify(cell);
            System.out.println(cluster.estimateClassification());
            System.out.println(cluster.estimationConfidence());
            System.out.println(cluster);
        }

    }
}
