package com.eva.GSACancer;

import javax.vecmath.GVector;
import java.util.Random;

/**
 * Created by eva on 5/1/16.
 */
public class Learner {
    public static final double epsilon = 0;
    private InputData inputData;
    private double gravityCoef0 = 50;
    private double alpha = 5;
    private int epochMax = 100;
    private double[] masses;
    private Agent [] accelerations;
    private Agent[] velocities;
    private GVector lower = new GVector(Cell.DIM);
    private GVector upper = new GVector(Cell.DIM);
    public Agent [] agents;
    private final int agentsNum = 10;
    private final int clustersNum = 10;
    private int epoch;
    private static Random randomGenerator = new Random();

   public Learner(InputData inputData) {
       this.inputData = inputData;
       masses = new double[agentsNum];
    }

    public void learn() {
        inputData.findBoundaries(lower, upper);
        //Agent [] agents = new Agent[]
        generateAgents();
        calcMasses();
        calcAccelerations();
        updateAgents();
        //System.out.println("upper " + upper);
        //System.out.println("lower " + lower);
    }

    private void generateAgents() {
        agents = new Agent[agentsNum];
        accelerations = new Agent[agents.length];
        velocities = new Agent[agents.length];
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
        for(int i = 0; i<Cell.DIM; i++) {
            cluster.center.setElement(i, lower.getElement(i) + randomGenerator.nextDouble()*(upper.getElement(i) - lower.getElement(i)));
        }

        return null;
    }

    public double gravityCoef() {
        return gravityCoef0 * Math.exp(-alpha* this.epoch / epochMax);
    }

    public void calcMasses() {
        double[] fitnesses = new double[agents.length];
        fitnesses[0] = agents[0].fitness();
        double best = fitnesses[0];
        double worst = best;
        for (int i = 1; i < agents.length; i++) {
            Agent agent = agents[i];
            double value = agent.fitness();
            fitnesses[i] = value;
            if (value < best) {
                best = value;
            }
            if (value > worst) {
                worst = value;
            }
        }
        double sumOfMasses = 0;
        for (int i = 0; i < fitnesses.length; i++) {
            masses[i] = (fitnesses[i] - worst) / (best - worst);
            sumOfMasses += masses[i];
        }
        for (int i = 0; i < masses.length; i++) {
            masses[i] /= sumOfMasses;
        }
    }

    public void calcAccelerations() {
        for (int i = 0; i < agents.length; i++) {
            accelerations[i] = new Agent();
            for (int j = 0; j < agents.length; j++){
                if (i == j) continue;
                double coef = gravityCoef() * masses[i] * masses[j] / (agents[i].distance(agents[j]) + epsilon);
                Agent agent = subtract(agents[j], agents[i]);
                agent.scale(coef);
                agent.scale(randomGenerator.nextDouble());
                //crossForces[i][j] = agent;
                accelerations[i].add(agent);
            }
            accelerations[i].scale(1.0/masses[i]);
        }
    }

    public void updateAgents() {
        for (int i = 0; i < velocities.length; i++) {
            velocities[i].scale(randomGenerator.nextDouble());
            velocities[i].add(accelerations[i]);
            agents[i].add(velocities[i]);
        }
    }

    public Agent subtract(Agent a, Agent b) {
        Agent z = a.clone();
        z.subtract(b);
        return z;
    }

    public class Agent {
        private Cluster[] clusters;

        public Agent(Cluster[] clusters) {
            this.clusters = clusters;
        }

        public Agent() {
            clusters = new Cluster[clustersNum];
            for (int i = 0; i < clusters.length; i++) {
                clusters[i] = new Cluster();
                clusters[i].center = new GVector(Cell.DIM);
                clusters[i].center.zero();
            }
        }

        public void add(Agent other) {
            for (int i = 0; i < clusters.length; i++) {
                clusters[i].center.add(other.clusters[i].center);
            }
        }

        public void subtract(Agent other) {
            for (int i = 0; i < clusters.length; i++) {
                clusters[i].center.sub(other.clusters[i].center);
            }
        }

        public void scale(double coef) {
            for (Cluster cluster : clusters) {
                cluster.center.scale(coef);
            }
        }

        public double sumOfSquares() {
            double sum = 0;
            for (Cluster cluster : clusters) {
                for (int i = 0; i < cluster.center.getSize(); i++) {
                    double value = cluster.center.getElement(i);
                    sum += value * value;
                }
            }
            return sum;
        }

        public double distance(Agent other) {
            Agent delta = clone();
            delta.subtract(other);
            return delta.sumOfSquares();
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

        @Override
        protected Agent clone() {
            return new Agent(clusters.clone());
        }
    }
}
