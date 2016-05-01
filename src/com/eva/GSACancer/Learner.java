package com.eva.GSACancer;

import javax.vecmath.GVector;
import java.util.Random;

/**
 * Created by eva on 5/1/16.
 */
public class Learner {
    private InputData inputData;
    private GVector lower = new GVector(Cell.DIM);
    private GVector upper = new GVector(Cell.DIM);
    public Agent [] agents;
    public Learner(InputData inputData) {
        this.inputData = inputData;
    }

    public void learn() {
        inputData.findBoundaries(lower, upper);
        //Agent [] agents = new Agent[]
        generateAgents(10, 10);
        //System.out.println("upper " + upper);
        //System.out.println("lower " + lower);
    }

    private void generateAgents(int agentsNum, int clustersNum) {
        agents = new Agent[agentsNum];
        for (int i = 0; i<agentsNum; i++) {
            Cluster[] clusters = new Cluster[clustersNum];
            for (int k = 0; k<clustersNum; k++){
                clusters[k] = generateCluster();
            }
            agents[i] = new Agent(clusters);
        }
    }

    private Cluster generateCluster() {
        Cluster cluster = new Cluster();
        Random randomGenerator = new Random();
        for(int i = 0; i<Cell.DIM; i++) {
            cluster.center.setElement(i, lower.getElement(i) + randomGenerator.nextDouble()*(upper.getElement(i) - lower.getElement(i)));
        }

        return null;
    }

    public class Agent {
        private Cluster[] clusters;


        public Agent(Cluster[] clusters) {
            this.clusters = clusters;
        }



        public double fitness(){
            double [] sumsOfDistances = new double[clusters.length];
            for (Cell cell: inputData.getCells()) {
                int minIndex = 0;
                double minDist = clusters[0].distance(cell.getPoint());
                for(int i = 1; i<clusters.length; i++) {
                    double dist = clusters[i].distance(cell.getPoint());
                    if (dist < minDist) {
                        minIndex = i;
                        minDist = dist;
                    }
                }
                sumsOfDistances[minIndex] += minDist;
            }
            double sum = 0;
            for (double sumOfDistances : sumsOfDistances) {
                sum += sumOfDistances;
            }
            return sum;
        }
        
    }
}
