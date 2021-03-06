package com.eva.DGCCancer;

import com.eva.GSACancer.Cell;
import com.eva.GSACancer.InputData;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.jscience.mathematics.number.Rational;

import javax.vecmath.GVector;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by eva on 5/8/16.
 */
public class Learner {
    private List<Cell> inputFAM = new ArrayList<>();
    private List<Cell> inputRMZ = new ArrayList<>();
    public double epsilon = 0.0000001;
    public double delta = 0.1;
    public final GVector lower, range;
    public List<DataParticle> dataParticlesFAM = new ArrayList<>();
    public List<DataParticle> dataParticlesRMZ = new ArrayList<>();
    public List<DataParticle> subsetA = new ArrayList<>();
    public List<DataParticle> subsetB = new ArrayList<>();
    public GVector weights;
    public GVector selectionProbabilities;
    public Rational ratio;
//    public Random random = new Random();
//    public final int LCM = 24024;

    public static final class ClassificationQuality {
        public int total;
        public int totalFAM;
        public int totalRMZ;
        public double missRatio;
        public double missRatioFAM;
        public double missRatioRMZ;

        @Override
        public String toString() {
            return "{" +
                    "missRatio=" + missRatio +
                    ", missRatioFAM=" + missRatioFAM +
                    ", missRatioRMZ=" + missRatioRMZ +
                    ", total=" + total +
                    ", totalFAM=" + totalFAM +
                    ", totalRMZ=" + totalRMZ +
                    '}';
        }
    }

    public double getCurrentMistakesFrequency() {
        return currentMistakesFrequency;
    }

    private double currentMistakesFrequency = 1.0;

    public Learner(InputData inputData) {
        int featuresNum = inputData.featuresNum();
        // calculate parameters for normalization
        lower = new GVector(featuresNum);
        range = new GVector(featuresNum);
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
        weights = new GVector(featuresNum);
        selectionProbabilities = new GVector(featuresNum);
        for (int i = 0; i < weights.getSize(); i++) {
            weights.setElement(i, 1.0);
            selectionProbabilities.setElement(i, 1.0/weights.getSize());
        }
    }

    void normalize(Cell cell) {
//        cell.getPoint().sub(lower);
//        for (int i = 0; i < cell.getPoint().getSize(); i++) {
//            cell.getPoint().setElement(i, cell.getPoint().getElement(i) / range.getElement(i));
//        }
    }

    public ClassificationQuality learn() {
        assert inputFAM.size() > 0;
        assert inputRMZ.size() > 0;
        prepareDP();
        shrinkDP(dataParticlesFAM);
        shrinkDP(dataParticlesRMZ);
        return findWeights();
    }

    public ClassificationQuality findWeights() {
        ratio = Rational.valueOf(dataParticlesFAM.size(), dataParticlesRMZ.size());
        selectSubsets();
        return algorithmTRFS();
    }

    private ClassificationQuality algorithmTRFS() {
        return algorithmTRFS(0.05);
    }

    private ClassificationQuality algorithmTRFS(double maxMistakesFrequency) {
        ClassificationQuality quality;
        BitSet triedWeights = new BitSet(weights.getSize()); // markers for weights that were tried since last improvement
        // initially assume that we have 100% misses
        currentMistakesFrequency = 1.0;
        weights.zero(); // initial guess - no difference between nodes
        long testFamQuantity = subsetB.stream()
                .filter(dataParticle -> dataParticle.centroid.classification == Cell.Classification.FAM)
                .count();
        long testRmzQuantity = subsetB.size() - testFamQuantity;
        do{
//            System.out.println(selectionProbabilities);
            int index = randomFeature();
//            System.out.println("index="+index);
            weights.setElement(index, weights.getElement(index) + epsilon);

            quality = mistakesQuantity(subsetA, subsetB);
            double mistakesFrequency = quality.missRatio;
            double famMistakesFrequency = quality.missRatioFAM;
            double rmzMistakesFrequency = quality.missRatioRMZ;

            boolean isBetter = mistakesFrequency < currentMistakesFrequency;
            adjustProbability(index, isBetter);
            if (isBetter) {
                currentMistakesFrequency = mistakesFrequency;
                System.out.println("weights=" + weights);;
                System.out.println("mistakesFrequency = " + currentMistakesFrequency);
                System.out.println("famMistakesFrequency = " + famMistakesFrequency);
                System.out.println("rmzMistakesFrequency = " + rmzMistakesFrequency);
                // reset flags triedWeights since now new tries may give some results
                triedWeights.clear();
            }
            else {
                weights.setElement(index, weights.getElement(index) - epsilon);
                if (!triedWeights.get(index)) { // only if we hit this index first time since last improve
                    triedWeights.set(index); // mark this index as tried
                    boolean triedAll = triedWeights.cardinality() == weights.getSize();
                    if (triedAll) {
                        break; // break this cycle
                    }
                }
            }
        } while (currentMistakesFrequency > maxMistakesFrequency);
        return quality;
    }

    private void adjustProbability(int index, boolean isBetter) {
//        System.out.println("adjust for " + index + " " + isBetter);
        if (isBetter) {
            selectionProbabilities.setElement(index, selectionProbabilities.getElement(index) + delta);
        } else {
            if (selectionProbabilities.getElement(index) > delta) {
                selectionProbabilities.setElement(index, selectionProbabilities.getElement(index) - delta);
//            } else {
//                selectionProbabilities.setElement(index, delta);
            }
        }
        // normalize to make sum equal to 1.0
        double sum = 0;
        for (int i = 0; i < selectionProbabilities.getSize(); i++) {
            sum += selectionProbabilities.getElement(i);
        }
        for (int i = 0; i < selectionProbabilities.getSize(); i++) {
            selectionProbabilities.setElement(i, selectionProbabilities.getElement(i)/sum);
        }
    }

    private ClassificationQuality mistakesQuantity(List<DataParticle> subsetTeacher, List<DataParticle> subsetTester) {
        ClassificationQuality quality = new ClassificationQuality();
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

        for (DataParticle testDP : subsetTester) {
            double sumFam = sumForces(testDP, teacherFAM);
            double sumRmz = sumForces(testDP, teacherRMZ);
            quality.total++;
            switch (testDP.centroid.classification)
            {
                case FAM:
                    if (sumRmz >= sumFam) {
                        quality.missRatio++;
                        quality.missRatioRMZ++;
                        quality.totalRMZ++;
                    } else {
                        quality.totalFAM++;
                    }
                    break;
                case RMZ:
                    if (sumFam >= sumRmz) {
                        quality.missRatio++;
                        quality.missRatioFAM++;
                        quality.totalFAM++;
                    } else {
                        quality.totalRMZ++;
                    }
                    break;
            }
        }
        quality.missRatioFAM /= quality.totalFAM;
        quality.missRatioRMZ /= quality.totalRMZ;
        quality.missRatio /= quality.total;
        return quality;
    }

    double sumForces(DataParticle testDP, List<DataParticle> particles) {
        return particles.stream()
                .map(dataParticle -> testDP.force(dataParticle))
                .reduce((double)0, Double::sum);
    }

    private int randomFeature() { //TODO: test this
        List<Pair<Integer, Double>> itemWeights = IntStream.range(0, selectionProbabilities.getSize())
                .mapToObj(i -> new Pair<>(i, selectionProbabilities.getElement(i)))
                .collect(Collectors.toList());
        return new EnumeratedDistribution<>(itemWeights).sample();
/*
        int[] numsToGenerate = new int[LCM]; // maybe make this as parameter and write some update for it by delta
        for (int i = 0, k = 0; i < Cell.DIM; i++) {
            for (int j = 0; j < selectionProbabilities.getElement(i) * LCM; j++) {
                numsToGenerate[k] = i;
                k++;
            }
        }
        return numsToGenerate[random.nextInt(LCM)];
*/
    }

    private void selectSubsets() {
        subsetA.clear();
        subsetB.clear();
        int rmzMiddle = dataParticlesRMZ.size() / 2;
        dataParticlesRMZ.stream()
                .skip(rmzMiddle)
                .collect(Collectors.toCollection(() -> subsetA));
//        subsetA.addAll(rmzMiddle, dataParticlesRMZ);
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
        dataParticlesFAM.stream()
                .skip(i)
                .collect(Collectors.toCollection(() -> subsetB));
//        subsetB.addAll(i, dataParticlesFAM);
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

    void prepareDP() {
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
//                System.out.println("dx["+i+"]="+element+", w="+weights.getElement(i));
                sum += element * element * weights.getElement(i);
            }
//            System.out.println("sum="+sum);
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
//            System.out.println("m1="+mass()+", m2="+dp.mass()+", R="+distance);
            return mass() * dp.mass() / (distance * distance);
        }
    }
}
