package com.eva.GSACancer;

import javax.vecmath.GVector;

/**
 * Created by eva on 5/1/16.
 */
public class Learner {
    private InputData inputData;
    private GVector lower = new GVector(Cell.DIM);
    private GVector upper = new GVector(Cell.DIM);
    public Learner(InputData inputData) {
        this.inputData = inputData;
    }

    public void learn() {
        inputData.findBoundaries(lower, upper);
        generateAgents(10);
        //System.out.println("upper " + upper);
        //System.out.println("lower " + lower);
    }

    private void generateAgents(int agentsNum) {
        for (int i = 0; i<agentsNum; i++) {
            Agent agent = new Agent();

        }
    }

}
