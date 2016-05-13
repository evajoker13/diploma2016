package com.eva.DGCCancer;

import com.eva.GSACancer.Cell;
import com.eva.GSACancer.InputData;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by nikolay on 10/05/16.
 */
public class LearnerTest {
    @Test
    public void testForce() {
        InputData inputData = InputData.fromCells(new Cell[]{Cell.fam(0, 1), Cell.rmz(1, 0), Cell.fam(0,2), Cell.rmz(2,0)});
        System.out.println(inputData);
        Learner learner = new Learner(inputData);
        learner.prepareDP();
        assertEquals(learner.dataParticlesFAM.get(0).force(learner.dataParticlesFAM.get(1)), 1, 1e-5);
        assertEquals(learner.dataParticlesFAM.get(0).force(learner.dataParticlesRMZ.get(0)), 0.5, 1e-5);
        double famFAM = learner.sumForces(learner.dataParticlesFAM.get(0), learner.dataParticlesFAM);
        double famRMZ = learner.sumForces(learner.dataParticlesFAM.get(0), learner.dataParticlesRMZ);
        System.out.println(famFAM);
        System.out.println(famRMZ);
        assertTrue(famFAM > famRMZ, famFAM + ">" + famRMZ);
    }

    @Test
    public void testTrivial() {
        InputData inputData = InputData.fromCells(new Cell[]{Cell.fam(0, 1), Cell.rmz(1, 0), Cell.fam(0,2), Cell.rmz(2,0)});
        Learner learner = new Learner(inputData);
        learner.learn();
    }


    @Test
    public void testUniform() {
        Random random = new Random();
        random.setSeed(0);
        List<Cell> fam = IntStream.range(0, 2)
                .mapToObj(i -> Cell.fam(random.nextDouble()*5 + 10, random.nextDouble()*5 + 10))
                .collect(Collectors.toList());
        List<Cell> rmz = IntStream.range(0, 2)
                .mapToObj(i -> Cell.rmz(random.nextDouble()*5 - 10, random.nextDouble()*5 - 10))
                .collect(Collectors.toList());
        List<Cell> all = new ArrayList<>();
        fam.stream().collect(Collectors.toCollection(() -> all));
        rmz.stream().collect(Collectors.toCollection(() -> all));
        InputData inputData = InputData.fromCells(all);
        Learner learner = new Learner(inputData);
        learner.learn();
        assertTrue(learner.getCurrentMistakesFrequency() < 0.05, "mistakeFrequency " + learner.getCurrentMistakesFrequency() + " too high");
    }

    @Test
    public void testUniformOverlapY() {
        Random random = new Random();
        random.setSeed(0);
        List<Cell> fam = IntStream.range(0, 2)
                .mapToObj(i -> Cell.fam(random.nextDouble()*5 + 10, random.nextDouble()*5 + 10))
                .collect(Collectors.toList());
        List<Cell> rmz = IntStream.range(0, 2)
                .mapToObj(i -> Cell.rmz(random.nextDouble()*5 - 10, random.nextDouble()*5 + 7))
                .collect(Collectors.toList());
        List<Cell> all = new ArrayList<>();
        fam.stream().collect(Collectors.toCollection(() -> all));
        rmz.stream().collect(Collectors.toCollection(() -> all));
        InputData inputData = InputData.fromCells(all);
        Learner learner = new Learner(inputData);
        learner.learn();
        assertTrue(learner.getCurrentMistakesFrequency() < 0.05, "mistakeFrequency " + learner.getCurrentMistakesFrequency() + " too high");
    }
//    @Test
//    public void test1() {
//        InputData inputData = InputData.fromCells(new Cell[]{Cell.fam(0, 1), Cell.fam(0, 0), Cell.fam(2, 0),
//                Cell.rmz(0, 2), Cell.rmz(2, 1), Cell.rmz(2, 2)});
//        Learner learner = new Learner(inputData);
//        learner.learn();
//    }
}
