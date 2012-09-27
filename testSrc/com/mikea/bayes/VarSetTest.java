package com.mikea.bayes;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author mike.aizatsky@gmail.com
 */
public class VarSetTest {
    @Test
    public void testToString() throws Exception {
        VarSet v1 = new VarSet(new int[]{1}, new int[]{2});
        VarSet v2 = new VarSet(new int[]{2, 1}, new int[]{2, 2});
        VarSet v3 = new VarSet(new int[]{3, 2}, new int[]{2, 2});

        assertEquals("{1(2)}", v1.toString());
        assertEquals("{2(2), 1(2)}", v2.toString());
        assertEquals("{3(2), 2(2)}", v3.toString());
    }

    @Test
    public void testHasVariable() throws Exception {
        VarSet v = new VarSet(new int[]{2, 1}, new int[]{2, 2});

        assertTrue(v.hasVariable(1));
        assertTrue(v.hasVariable(2));
        assertTrue(!v.hasVariable(3));
    }

    @Test
    public void testGetCardinality() throws Exception {
        VarSet v = new VarSet(new int[]{2, 1}, new int[]{2, 3});

        assertEquals(2, v.getCardinality(2));
        assertEquals(3, v.getCardinality(1));
    }

    @Test
    public void testProduct() throws Exception {
        VarSet v1 = new VarSet(new int[]{1}, new int[]{1});
        VarSet v2 = new VarSet(new int[]{2, 1}, new int[]{2, 1});
        VarSet v3 = new VarSet(new int[]{3, 2}, new int[]{3, 2});

        assertEquals("{1(1), 2(2)}", VarSet.product(v1, v2).toString());
        assertEquals("{1(1), 2(2), 3(3)}", VarSet.product(v1, v3).toString());
        assertEquals("{1(1), 2(2), 3(3)}", VarSet.product(v2, v3).toString());
        assertEquals("{1(1), 2(2), 3(3)}", VarSet.product(v1, v2, v3).toString());
    }

    @Test
    public void testGetAssignment() throws Exception {
        VarSet v = new VarSet(new int[]{1}, new int[]{1});
        assertEquals("[-1, 0]", Arrays.toString(v.getAssignment(0)));

        v = new VarSet(new int[]{1}, new int[]{3});
        assertEquals("[-1, 0]", Arrays.toString(v.getAssignment(0)));
        assertEquals("[-1, 1]", Arrays.toString(v.getAssignment(1)));
        assertEquals("[-1, 2]", Arrays.toString(v.getAssignment(2)));

        v = new VarSet(new int[]{0, 3}, new int[]{2, 3});
        assertEquals("[0, -1, -1, 0]", Arrays.toString(v.getAssignment(0)));
        assertEquals("[1, -1, -1, 0]", Arrays.toString(v.getAssignment(1)));
        assertEquals("[0, -1, -1, 1]", Arrays.toString(v.getAssignment(2)));
        assertEquals("[1, -1, -1, 1]", Arrays.toString(v.getAssignment(3)));
        assertEquals("[0, -1, -1, 2]", Arrays.toString(v.getAssignment(4)));
        assertEquals("[1, -1, -1, 2]", Arrays.toString(v.getAssignment(5)));
    }

    @Test
    public void testGetIndex() throws Exception {
        VarSet v = new VarSet(new int[]{1}, new int[]{1});
        verifyGetIndex(v);

        v = new VarSet(new int[]{1}, new int[]{3});
        verifyGetIndex(v);

        v = new VarSet(new int[]{0, 3}, new int[]{2, 3});
        verifyGetIndex(v);
    }

    private void verifyGetIndex(VarSet v) {
        for (int i = 0; i < v.getMaxIndex(); ++i) {
            assertEquals(i, v.getIndex(v.getAssignment(i)));
        }
    }

    @Test
    public void testRemoveVars() throws Exception {
        VarSet v1 = new VarSet(new int[]{1, 2}, new int[]{3, 2});
        VarSet v2 = new VarSet(new int[]{2}, new int[]{2});

        VarSet v = v1.removeVars(v2);
        assertEquals("{1(3)}", v.toString());
    }
}
