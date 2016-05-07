package com.eva.GSACancer;

import org.testng.annotations.Test;

import static com.eva.GSACancer.TestUtils.*;
import static org.testng.Assert.*;

/**
 * Created by eva on 5/3/16.
 */
public class ClusterTest {

    @Test
    public void testDistance() throws Exception {
        Cluster cluster = new Cluster();
        cluster.center = vec(0, 0);
        assertEquals(cluster.distance(vec(3,4)), 5.0, 0.001);
    }

    @Test
    public void testWithin() throws Exception {
        Cluster cluster = new Cluster();
        cluster.center = vec(0, 1);
        assertTrue(cluster.within(vec(0, 0), vec(2.0, 3.0)));
    }

}