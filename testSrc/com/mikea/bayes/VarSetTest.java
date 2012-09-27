package com.mikea.bayes;

import org.junit.Test;

import java.util.Arrays;

import static com.mikea.bayes.VarSet.createVarSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author mike.aizatsky@gmail.com
 */
public class VarSetTest {
    private static final ProbabilitySpace space = new ProbabilitySpace(
            4, new int[]{1, 2, 2, 3});

    @Test
    public void testToString() throws Exception {
        VarSet v1 = createVarSet(space, new int[]{1});
        VarSet v2 = createVarSet(space, new int[]{2, 1});
        VarSet v3 = createVarSet(space, new int[]{3, 2});

        assertEquals("{1}", v1.toString());
        assertEquals("{1, 2}", v2.toString());
        assertEquals("{2, 3}", v3.toString());
    }

    @Test
    public void testHasVariable() throws Exception {
        VarSet v = createVarSet(space, new int[]{2, 1});

        assertTrue(v.hasVariable(1));
        assertTrue(v.hasVariable(2));
        assertTrue(!v.hasVariable(3));
    }

    @Test
    public void testProduct() throws Exception {
        VarSet v1 = createVarSet(space, new int[]{1});
        VarSet v2 = createVarSet(space, new int[]{2, 1});
        VarSet v3 = createVarSet(space, new int[]{3, 2});

        assertEquals("{1, 2}", VarSet.product(v1, v2).toString());
        assertEquals("{1, 2, 3}", VarSet.product(v1, v3).toString());
        assertEquals("{1, 2, 3}", VarSet.product(v2, v3).toString());
        assertEquals("{1, 2, 3}", VarSet.product(v1, v2, v3).toString());
    }

    @Test
    public void testGetAssignment() throws Exception {
        VarSet v = createVarSet(space, new int[]{1});
        assertEquals("[-1, 0, -1, -1]", Arrays.toString(v.getAssignment(0)));

        v = createVarSet(space, new int[]{1});
        assertEquals("[-1, 0, -1, -1]", Arrays.toString(v.getAssignment(0)));
        assertEquals("[-1, 1, -1, -1]", Arrays.toString(v.getAssignment(1)));

        v = createVarSet(space, new int[]{1, 3});
        assertEquals("[-1, 0, -1, 0]", Arrays.toString(v.getAssignment(0)));
        assertEquals("[-1, 1, -1, 0]", Arrays.toString(v.getAssignment(1)));
        assertEquals("[-1, 0, -1, 1]", Arrays.toString(v.getAssignment(2)));
        assertEquals("[-1, 1, -1, 1]", Arrays.toString(v.getAssignment(3)));
        assertEquals("[-1, 0, -1, 2]", Arrays.toString(v.getAssignment(4)));
        assertEquals("[-1, 1, -1, 2]", Arrays.toString(v.getAssignment(5)));
    }

    @Test
    public void testGetIndex() throws Exception {
        VarSet v = createVarSet(space, new int[]{1});
        verifyGetIndex(v);

        v = createVarSet(space, new int[]{1});
        verifyGetIndex(v);

        v = createVarSet(space, new int[]{0, 3});
        verifyGetIndex(v);
    }

    private void verifyGetIndex(VarSet v) {
        for (int i = 0; i < v.getMaxIndex(); ++i) {
            assertEquals(i, v.getIndex(v.getAssignment(i)));
        }
    }

    @Test
    public void testRemoveVars() throws Exception {
        VarSet v1 = createVarSet(space, new int[]{1, 2});
        VarSet v2 = createVarSet(space, new int[]{2});

        VarSet v = v1.removeVars(v2);
        assertEquals("{1}", v.toString());
    }
}
