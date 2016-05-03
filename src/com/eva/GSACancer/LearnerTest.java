package com.eva.GSACancer;
import static org.testng.Assert.*;

/**
 * Created by eva on 5/3/16.
 */
public class LearnerTest {

    @org.testng.annotations.DataProvider(name = "test1")
    public static Object[][] test1() {
        return new Object[][] {
            {
                new Cell[] { Cell.fam(0, 0), Cell.fam(0, 1), Cell.rmz(1, 0), Cell.rmz(1, 1) }
            }
        };
    }

    @org.testng.annotations.Test(dataProvider = "test1")
    public void testLearn(Cell[] cells) throws Exception {
        Learner learner = new Learner(InputData.fromCells(cells));
        learner.learn();

    }

    @org.testng.annotations.DataProvider(name = "test2")
    public static Object[][] test2() {
        Learner learner = new Learner(new InputData());
        Learner.Agent a = learner.createAgent();
        Learner.Agent b = learner.createAgent();
        Learner.Agent c = learner.createAgent();
        a.clusters[0].center.setElement(0, 1.0);
        a.clusters[0].center.setElement(1, 1.0);
        b.clusters[0].center.setElement(0, 2.0);
        b.clusters[0].center.setElement(1, 2.0);
        c.clusters[0].center.setElement(0, -1.0);
        c.clusters[0].center.setElement(1, -1.0);

        return new Object[][] {
                {
                        a, b, c

                }
        };
    }

    @org.testng.annotations.Test(dataProvider = "test2")
    public void testSubtract(Learner.Agent a, Learner.Agent b, Learner.Agent c) throws Exception {
        Learner.Agent x = a.clone();
        x.subtract(b);
        assertEquals(x, c);
    }

}