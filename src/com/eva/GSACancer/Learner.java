package com.eva.GSACancer;

import javax.vecmath.GVector;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by eva on 5/1/16.
 */
public class Learner {
    public static final double EPSILON = 0.0001;
    private InputData inputData;
    private double gravityCoef0 = 50;
    private double alpha = 0.0;
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
       inputData.findBoundaries(lower, upper);
    }

    public void learn() {
        //Agent [] agents = new Agent[]
        generateAgents();
        for (epoch = 0; epoch < epochMax; ) {
            nextStep();
        }
        //System.out.println("upper " + upper);
        //System.out.println("lower " + lower);
    }

    void nextStep() {
        fixup();
        calcMasses();
        calcAccelerations();
        updateAgents();
        ++epoch;
    }

    void generateAgents() {
        agents = new Agent[agentsNum];
        masses = new double[agents.length];
        accelerations = new Agent[agents.length];
        velocities = new Agent[agents.length];
        for (int i = 0; i<agentsNum; i++) {
            agents[i] = generateAgent();
            accelerations[i] = new Agent();
            velocities[i] = generateAgent();
            velocities[i].subtract(agents[i]);
        }
    }

    Agent generateAgent() {
        Cluster[] clusters = new Cluster[clustersNum];
        for (int k = 0; k<clustersNum; k++){
            clusters[k] = generateCluster();
        }
        return new Agent(clusters);
    }

    Cluster generateCluster() {
        Cluster cluster = new Cluster();
        for(int i = 0; i<Cell.DIM; i++) {
            cluster.center.setElement(i, lower.getElement(i) + randomGenerator.nextDouble()*(upper.getElement(i) - lower.getElement(i)));
        }

        return cluster;
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
            masses[i] = (fitnesses[i] - worst + EPSILON) / (best - worst + EPSILON);
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
                double coef = gravityCoef() * masses[i] * masses[j] / (agents[i].distance(agents[j]) + EPSILON);
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
            double rand = randomGenerator.nextDouble();
            velocities[i].scale(rand);
            velocities[i].add(accelerations[i]);
            agents[i].add(velocities[i]);
        }
    }

    public void fixup() {
        for (int i = 0; i < agents.length; i++) {
            if (!agents[i].inBorders()) {
                for (int j = 0; j < agents[i].clusters.length; j++) {
                    agents[i].clusters[j] = generateCluster();
                }
            }
            Arrays.sort(agents[i].clusters);
        }
    }

    public Agent bestAgent() {
        Agent best = agents[0];
        double bestFitness = best.fitness();
        for (int i = 1; i < agents.length; i++) {
            double fitness = agents[i].fitness();
            if (fitness < bestFitness) {
                best = agents[i];
                bestFitness = fitness;
            }
        }
        return best;
    }

    public Agent subtract(Agent a, Agent b) {
        Agent z = a.clone();
        z.subtract(b);
        return z;
    }

    public class Agent {
        public Cluster[] clusters;

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
            for (Cluster cluster : clusters) {
                cluster.reset();
            }
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
                clusters[minIndex].update(cell.classification);
            }
            double sum = 0;
            for (double sumOfDistances : sumsOfDistances) {
                sum += sumOfDistances;
            }
            double prod = 1;
            for (Cluster cluster : clusters) {
                prod *= 1 - cluster.estimationConfidence() + EPSILON;
            }
            return sum * prod;
        }



        @Override
        protected Agent clone() {
            return new Agent(clusters.clone());
        }

        public boolean inBorders() {
            for (Cluster cluster : clusters) {
                if (!cluster.within(lower, upper)) {
                    return false;
                }
            }
            return true;
        }

        public Cluster classify(Cell cell) {
            Cluster best = null;
            double bestDistance = Double.MAX_VALUE;
            for (Cluster cluster : clusters) {
                double distance = cluster.distance(cell.getPoint());
                if (distance < bestDistance) {
                    best = cluster;
                    bestDistance = distance;
                }
            }
            //assert best != null;
            //if (best == null) return Cell.Classification.Unknown;

            return best;
        }
    }
}
