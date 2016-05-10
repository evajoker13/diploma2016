package com.eva.DGCCancer;

import com.eva.GSACancer.Cell;
import com.eva.GSACancer.InputData;
import processing.core.PApplet;

import javax.vecmath.GVector;

/**
 * Created by eva on 5/10/16.
 */
public class DataDraw extends PApplet {

    int scaleSize = 2;

    public void settings() {
        size(500, 500);
//        fullScreen();
    }

    public void setup() {
    }

    public void draw() {
        background(127);
        noFill();
//        translate(1, 3);

//        strokeWeight(2f);
        InputData inputData = InputData.fromCells(new Cell[]{Cell.fam(0, 1), Cell.rmz(1, 0), Cell.fam(0,2), Cell.rmz(2,0)});
        Learner learner = new Learner(inputData);
        learner.prepareDP();
        for (Learner.DataParticle dataParticle : learner.dataParticlesFAM) {
            drawDP(dataParticle, 0, 1);
//            scale(scaleSize);
        }
        for (Learner.DataParticle dataParticle : learner.dataParticlesRMZ) {
            drawDP(dataParticle, 0, 1);
//            scale(scaleSize);
        }
    }

    public void drawDP(Learner.DataParticle dp, int xCoord, int yCoord) {
        for (int i = 0; i < dp.cells.size(); i++) {
            float x, y, xc, yc;
            stroke(255, 4, 4);
            GVector centroid = dp.centroid.getPoint();
            xc = (float) centroid.getElement(xCoord) * scaleSize;
            yc = (/*height - */(float) centroid.getElement(yCoord)) * scaleSize ;
//            scale(scaleSize);
            centre(xc, yc);
            if (dp.centroid.classification == Cell.Classification.FAM) stroke(0, 255, 0);
            else stroke(0, 0, 255);
            for (Cell cell : dp.cells) {
                GVector point = cell.getPoint();
                x = (float) point.getElement(xCoord) * scaleSize;
                y = (/*height -*/ (float) point.getElement(yCoord)) * scaleSize;
                scale(scaleSize);
                line(x, y, xc, yc);
            }
        }
    }

    public void centre(float x, float y) {
        beginShape();
        int i = 2;
        line(x, y - i, x, y + i);
        line(x - i, y, x + i, y);
        endShape(CLOSE);
    }

    static public void main(String args[]) {
        PApplet.main(new String[] { "com.eva.DGCCancer.DataDraw" });
    }

}
