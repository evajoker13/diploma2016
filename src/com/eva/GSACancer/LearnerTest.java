package com.eva.GSACancer;

import org.testng.annotations.Test;

import static com.eva.GSACancer.TestUtils.vec;
import static org.testng.Assert.assertTrue;

/**
 * Created by eva on 5/3/16.
 */
public class LearnerTest {
    @Test
    public void testGenerateAgents() throws Exception {
        Learner learner = new Learner(InputData.fromCells(new Cell[] {Cell.rmz(1,1), Cell.fam(10,5)}));
        learner.generateAgents();
        for (Learner.Agent agent : learner.agents) {
            for (Cluster cluster : agent.clusters) {
                for (int i = 0; i < cluster.center.getSize(); i++) {
                    assertTrue(Double.isFinite(cluster.center.getElement(i)));
                }
                assertTrue(cluster.within(vec(1, 1), vec(10, 5)));
            }
        }

        learner.fixup();
        for (Learner.Agent agent : learner.agents) {
            for (Cluster cluster : agent.clusters) {
                for (int i = 0; i < cluster.center.getSize(); i++) {
                    assertTrue(Double.isFinite(cluster.center.getElement(i)));
                }
                assertTrue(cluster.within(vec(1, 1), vec(10, 5)));
            }
        }
        learner.nextStep();
        learner.fixup();
        for (Learner.Agent agent : learner.agents) {
            for (Cluster cluster : agent.clusters) {
                for (int i = 0; i < cluster.center.getSize(); i++) {
                    assertTrue(Double.isFinite(cluster.center.getElement(i)));
                }
                assertTrue(cluster.within(vec(1, 1), vec(10, 5)));
            }
        }
    }

    @Test
    public void testGenerateCluster() throws Exception {
        Learner learner = new Learner(InputData.fromCells(new Cell[] {Cell.rmz(1,1), Cell.fam(10,5)}));
        for (int i = 0; i < 10; i++) {
            Cluster cluster = learner.generateCluster();
            assertTrue(cluster.within(vec(1, 1), vec(10, 5)));
            System.out.println(cluster.center);
        }
    }

}