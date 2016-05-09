package com.eva.DGCCancer;

import com.eva.GSACancer.Cell;
import com.eva.GSACancer.InputData;
import org.jscience.mathematics.number.Rational;

import javax.vecmath.GVector;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by eva on 5/8/16.
 */
public class Learner {
    private List<Cell> inputFAM = new ArrayList<>();
    private List<Cell> inputRMZ = new ArrayList<>();
    public double epsilon;
    public double delta;
    public GVector lower = new GVector(Cell.DIM), range = new GVector(Cell.DIM);
    public List<DataParticle> dataParticlesFAM = new ArrayList<>();
    public List<DataParticle> dataParticlesRMZ = new ArrayList<>();
    public List<DataParticle> subsetA = new ArrayList<>();
    public List<DataParticle> subsetB = new ArrayList<>();
    public GVector weights = new GVector(Cell.DIM);
    public GVector selectionProbabilities = new GVector(Cell.DIM);
    public Rational ratio;
    public Random random = new Random();
    public final int LCM = 360360;

    public Learner(InputData inputData) {
        inputData.findBoundaries(lower, range);
        range.sub(lower);
        for (Cell cell : inputData.getCells()) {
            normalize(cell);
            switch (cell.classification) {
                case FAM:
                    inputFAM.add(cell);
                    break;
                case RMZ:
                    inputRMZ.add(cell);
                    break;
            }
        }
        for (int i = 0; i < weights.getSize(); i++) {
            weights.setElement(i, 1.0);
            selectionProbabilities.setElement(i, 1.0/15);
        }
    }

    void normalize(Cell cell) {
        cell.getPoint().sub(lower);
        for (int i = 0; i < cell.getPoint().getSize(); i++) {
            cell.getPoint().setElement(i, cell.getPoint().getElement(i) / range.getElement(i));
        }
    }

    public void learn() {
        prepareDP();
        shrinkDP(dataParticlesFAM);
        shrinkDP(dataParticlesRMZ);
    }

    public void findWeights() {
        ratio = Rational.valueOf(dataParticlesFAM.size(), dataParticlesRMZ.size());
        selectSubsets();
        algorithmTRFS();
    }

    private void algorithmTRFS() {
        //TODO: look closer!!!
        double maxMistakesFrequency = 0.05;
        double currentMistakesFrequency;
        do{
            int index = randomFeature();
            weights.setElement(index, weights.getElement(index) + epsilon);

            int testResult = mistakesQuantity(subsetA, subsetB);
            currentMistakesFrequency = testResult / (subsetB.size() + subsetB.size());

            if (testResult < currentMistakesFrequency) {
                currentMistakesFrequency = testResult;
                selectionProbabilities.setElement(index, selectionProbabilities.getElement(index) + delta);
            } else {
                weights.setElement(index, weights.getElement(index) - epsilon);
                if (selectionProbabilities.getElement(index) > delta) {
                    selectionProbabilities.setElement(index, selectionProbabilities.getElement(index) - delta);
                } else {
                    selectionProbabilities.setElement(index, 0.0);
                }
            }
        }while (currentMistakesFrequency > maxMistakesFrequency);
    }

    private int mistakesQuantity(List<DataParticle> subsetTeacher, List<DataParticle> subsetTester) { // TODO: look closer!!!!
        int index = 0;
        List<DataParticle> teacherFAM = new ArrayList<>();
        List<DataParticle> teacherRMZ = new ArrayList<>();
        for (DataParticle dataParticle : subsetTeacher) {
            switch (dataParticle.centroid.classification) {
                case FAM:
                    teacherFAM.add(dataParticle);
                    break;
                case RMZ:
                    teacherRMZ.add(dataParticle);
                    break;
            }
        }
        shrinkDP(teacherFAM);
        shrinkDP(teacherRMZ);

        for (int i = 0; i < subsetTester.size(); i++) {
            double sumFam = 0, sumRmz = 0;
            for (int j = 0, k = 0; j < teacherFAM.size() || k < teacherRMZ.size();j++, k++) { // TODO: is this correct????????? or make 2 for-loop???
                DataParticle testdp = subsetTester.get(i);
                sumFam += testdp.force(teacherFAM.get(j));
                sumRmz += testdp.force(teacherRMZ.get(k));
            }
            if (subsetTester.get(i).centroid.classification != (sumFam > sumRmz ? Cell.Classification.FAM  : Cell.Classification.RMZ)) index++;
        }

        return index;
    }

    private int randomFeature() { //TODO: test this
        int[] numsToGenerate = new int[LCM]; // maybe make this as parameter and write some update for it by delta
        for (int i = 0, k = 0; i < Cell.DIM; i++) {
            for (int j = 0; j < selectionProbabilities.getElement(i) * LCM; j++) {
                numsToGenerate[k] = i;
                k++;
            }
        }
        return numsToGenerate[random.nextInt(LCM)];
    }

    private void selectSubsets() {
        int rmzMiddle = dataParticlesRMZ.size() / 2;
        subsetA.addAll(rmzMiddle, dataParticlesRMZ);
        int rmzMass = subsetA.stream()
                .map(DataParticle::mass)
                .reduce(0, Integer::sum);
        double targetFamMass = ratio.times(rmzMass).doubleValue();
        int famMass = 0;
        int i = 0;
        for (; famMass < targetFamMass && i < dataParticlesFAM.size(); i++) {
            subsetA.add(dataParticlesFAM.get(i));
            famMass += dataParticlesFAM.get(i).mass();
        }
        dataParticlesRMZ.stream().limit(rmzMiddle).collect(Collectors.toCollection(() -> subsetB));
        subsetB.addAll(i, dataParticlesFAM);
    }

    private void shrinkDP(List<DataParticle> dataParticles) {
        for (int i = 0; i < dataParticles.size(); i++) {
            DataParticle dpO = dataParticles.get(i);
            for (int j = i + 1; j < dataParticles.size(); ) {
                double distance = dpO.distance(dataParticles.get(j));
                if (distance < epsilon)
                {
                    dpO.add(dataParticles.get(j));
                    dataParticles.remove(j);
                }
                else ++j;
            }
        }
    }

    private void prepareDP() {
        for (Cell cell : inputFAM) {
            DataParticle dp = new DataParticle(cell);
            dataParticlesFAM.add(dp);
        }
        for (Cell cell : inputRMZ) {
            DataParticle dp = new DataParticle(cell);
            dataParticlesRMZ.add(dp);
        }
    }

    /**
     * Created by eva on 5/8/16.
     */
    public class DataParticle {
        List<Cell> cells = new ArrayList<>();
        public Cell centroid;

        public DataParticle(Cell cell) {
            cells.add(cell);
            centroid = new Cell(cell.classification, new GVector(cell.getPoint()));
        }
        public int mass() {
            return cells.size();
        }

        public double distance(DataParticle dataParticle) {
            double sum = 0;
            for (int i = 0; i < centroid.getPoint().getSize(); i++) {
                double element = centroid.getPoint().getElement(i) - dataParticle.centroid.getPoint().getElement(i);
                sum += element * element * weights.getElement(i);
            }
            return Math.sqrt(sum);
        }

        public void add(DataParticle dataParticle) {
            assert centroid.classification == dataParticle.centroid.classification;
            double k = (double)dataParticle.mass() / (dataParticle.mass() + mass());
            centroid.getPoint().interpolate(dataParticle.centroid.getPoint(), k);
            cells.addAll(dataParticle.cells);
        }

        public double force(DataParticle dp) {
            double distance = distance(dp);
            return mass() * dp.mass() / distance * distance;
        }
    }
}
