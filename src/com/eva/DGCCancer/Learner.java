package com.eva.DGCCancer;

import com.eva.GSACancer.Cell;
import com.eva.GSACancer.InputData;
import org.jscience.mathematics.number.Rational;

import javax.vecmath.GVector;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by eva on 5/8/16.
 */
public class Learner {
    private List<Cell> inputFAM = new ArrayList<>();
    private List<Cell> inputRMZ = new ArrayList<>();
    public double epsilon;
    public GVector lower = new GVector(Cell.DIM), range = new GVector(Cell.DIM);
    public List<DataParticle> dataParticlesFAM = new ArrayList<>();
    public List<DataParticle> dataParticlesRMZ = new ArrayList<>();
    public List<DataParticle> subsetA = new ArrayList<>();
    public List<DataParticle> subsetB = new ArrayList<>();
    public GVector weights = new GVector(Cell.DIM);
    public GVector selectionProbabilities = new GVector(Cell.DIM);
    public Rational ratio;
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
        ratio = Rational.valueOf(dataParticlesFAM.size(), dataParticlesRMZ.size());
        shrinkDP(dataParticlesFAM);
        shrinkDP(dataParticlesRMZ);
        selectSubsets();
        algorithmTRFS();
    }

    private void algorithmTRFS() {
        //TODO: finish this
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
    }
}
