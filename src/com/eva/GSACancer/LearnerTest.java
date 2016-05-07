package com.eva.GSACancer;

import org.testng.annotations.Test;

import static com.eva.GSACancer.TestUtils.vec;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * Created by eva on 5/3/16.
 */
public class LearnerTest {
    @Test
    public void testGenerateAgents() throws Exception {
        Learner learner = new Learner(InputData.fromCells(new Cell[] {Cell.rmz(1,1), Cell.fam(10,5)}));
        learner.generateAgents(learner.defaultAgentsNum);
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

    @Test
    public void test1() throws Exception {
        InputData inputData = InputData.fromCells(new Cell[]{Cell.fam(0, 1), Cell.fam(0, 0), Cell.rmz(1, 1), Cell.rmz(1, 0)});
        Learner learner = new Learner(inputData, 2);
        learner.generateAgents(2);
        for (int i = 0; i < 100; i++) {
            System.out.println("---------- " + i + "-epoch ----------");
            learner.nextStep();
            for (Cell cell : inputData.getCells()) {
                Cluster classify = learner.bestAgent().classify(cell);
                System.out.println(classify);
                System.out.println(classify.estimateClassification());
            }
        }
    }

    @Test
    public void testMasses() throws Exception {
        InputData inputData = InputData.fromCells(new Cell[]{Cell.fam(0, 1), Cell.fam(0, 0), Cell.rmz(1, 1), Cell.rmz(1, 0)});
        Learner learner = new Learner(inputData, 2);
        learner.generateAgents(2);
        // Extract useful variables
        Learner.Agent agent1 = learner.agents[0];
        Learner.Agent agent2 = learner.agents[1];
        // Place one agent into an ideal position and other in opposite one
        agent1.clusters[0].center = vec(0, 0.5);
        agent1.clusters[1].center = vec(1, 0.5);
        agent2.clusters[0].center = vec(0.5, 0);
        agent2.clusters[1].center = vec(0.5, 1);

//        learner.nextStep();
//        for (int i = 0; i < learner.masses.length; i++) {
//            System.out.println(learner.masses[i] + "  mass of the " + i + " elem");
//        }
//        System.out.println(agent1.clusters[0].center);
//        System.out.println(agent1.clusters[1].center);
//        System.out.println("=====ag=====");
//        System.out.println(agent2.clusters[0].center);
//        System.out.println(agent2.clusters[1].center);
//
//        learner.nextStep();
//        for (int i = 0; i < learner.masses.length; i++) {
//            System.out.println(learner.masses[i] + "  mass of the " + i + " elem");
//        }
//        System.out.println(agent1.clusters[0].center);
//        System.out.println(agent1.clusters[1].center);
//        System.out.println("=====ag=====");
//        System.out.println(agent2.clusters[0].center);
//        System.out.println(agent2.clusters[1].center);
//
//        for (int i = 0; i < learner.masses.length; i++) {
//            System.out.println(learner.masses[i] + "  mass of the " + i + " elem");
//        }
    }

    @Test
    public void testClassify() throws Exception {
        InputData inputData = InputData.fromCells(new Cell[]{Cell.fam(0, 1), Cell.fam(0, 0), Cell.rmz(1, 1), Cell.rmz(1, 0)});
        Learner learner = new Learner(inputData, 2);
        learner.generateAgents(1);
        Learner.Agent agent = learner.agents[0];
        agent.clusters[0].center = vec(0, 0.5);
        agent.clusters[1].center = vec(1, 0.5);
        System.out.println(agent.fitness());
        assertEquals(agent.fitness(), 0, 1E-6);
        assertEquals(agent.clusters[0].estimateClassification(), Cell.Classification.FAM);
        assertEquals(agent.clusters[1].estimateClassification(), Cell.Classification.RMZ);
        assertSame(agent.classify(Cell.fam(0, 0)), agent.clusters[0]);
        assertSame(agent.classify(Cell.fam(1, 1)), agent.clusters[1]);
        System.out.println(agent.classify(Cell.fam(0, 0)).center);
        System.out.println(agent.classify(Cell.fam(1, 1)).center);

        learner.nextStep();
        System.out.println("========================");
        System.out.println(agent.classify(Cell.fam(0, 0)).center);
        System.out.println(agent.classify(Cell.fam(1, 1)).center);
    }

    @Test
    public void test2() {
        InputData inputData = InputData.fromCells(new Cell[]{Cell.fam(0, 1), Cell.fam(0, 0), Cell.fam(2, 0),
                Cell.rmz(0, 2), Cell.rmz(2, 1), Cell.rmz(2, 2)});
        Learner learner = new Learner(inputData, 2);
        learner.generateAgents(2);
        // Extract useful variables
        Learner.Agent agent1 = learner.agents[0];
        Learner.Agent agent2 = learner.agents[1];
        // Place one agent into an ideal position and other in opposite one
        // So agent1 is best
        // And agent2 is worst
        agent1.clusters[0].center = vec(0.75, 0.4);
        agent1.clusters[1].center = vec(1.25, 1.6);
        agent2.clusters[0].center = vec(0.5, 1);
        agent2.clusters[1].center = vec(1.5, 1);
        // Put initial velocity for out agents equals zero
        learner.velocities[0] = learner.createAgent();
        learner.velocities[1] = learner.createAgent();

//        System.out.println("fitness");
//        assertEquals(agent1.fitness(), 0.0, 1E-3);
//        assertEquals(agent2.fitness(), 1.0, 1E-3);

//        assertEquals(agent1.clusters[0].distance(inputData.getCells().get(0).getPoint()), 0.9604686356149273, 1e-6);
//
//      /*  for (int i = 0; i < inputData.getCells().size(); i++) {
//            Cluster cluster = agent1.classify(inputData.getCells().get(i));
//            System.out.println(cluster.distance(inputData.getCells().get(i).getPoint()) + "<--- \tsmallest for "
//                    + inputData.getCells().get(i).getPoint() + "\t in agent1");
//        }*/
//        System.out.println(agent1.fitness() + "<--- fitness of agent1");
//
//        /*for (int i = 0; i < inputData.getCells().size(); i++) {
//            Cluster cluster1 = agent2.classify(inputData.getCells().get(i));
//            System.out.println(cluster1.distance(inputData.getCells().get(i).getPoint()) + "\t<--- smallest for "
//                    + inputData.getCells().get(i).getPoint() + " \tin agent2");
//        }*/
//        System.out.println(agent2.fitness() + "<--- fitness of agent2");
//        System.out.println("agent1:");
//        System.out.println(agent1.classify(Cell.fam(0, 0)).center);
//        System.out.println(agent1.classify(Cell.fam(1, 1)).center);
//        System.out.println("agent2:");
//        System.out.println(agent2.classify(Cell.fam(0, 0)).center);
//        System.out.println(agent2.classify(Cell.fam(1, 1)).center);
//
//        System.out.println("-------------nextStep()--------------");
//
//        System.out.println("fixup(): ");
//        learner.fixup();
//        System.out.println(agent1.clusters[0].center + "<--- agent1");
//        System.out.println(agent1.clusters[1].center + "<--- agent1");
//        System.out.println(agent2.clusters[0].center + "<--- agent2");
//        System.out.println(agent2.clusters[1].center + "<--- agent2");
//
//        System.out.println("calcMasses(): ");
//        learner.calcMasses();
//        System.out.println(learner.masses[0] + "<--- agent1");
//        System.out.println(learner.masses[1] + "<--- agent2");
//
//        System.out.println("calcAccelerations(): ");
//        learner.calcAccelerations();
//        System.out.println(learner.accelerations[0] + "<--- agent1");
//        System.out.println(learner.accelerations[1] + "<--- agent2");
    }

    @Test
    public void testFitness() {
        InputData inputData = InputData.fromCells(new Cell[]{Cell.fam(0, 1), Cell.fam(0, 0), Cell.rmz(1, 1), Cell.rmz(1, 0)});
        Learner learner = new Learner(inputData, 2);
        learner.generateAgents(2);
        // Extract useful variables
        Learner.Agent agent1 = learner.agents[0];
        Learner.Agent agent2 = learner.agents[1];
        // Place one agent into an ideal position and other in opposite one
        agent1.clusters[0].center = vec(0, 0.5);
        agent1.clusters[1].center = vec(1, 0.5);
        agent2.clusters[0].center = vec(0.5, 0);
        agent2.clusters[1].center = vec(0.5, 1);
        // Put initial velocity for out agents equals zero
        learner.velocities[0] = learner.createAgent();
        learner.velocities[1] = learner.createAgent();

        agent1.fitness();
    }

    //@Test

}
