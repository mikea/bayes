package com.mikea.bayes;

import org.junit.Test;

import static com.mikea.bayes.VarSet.newVarSet;
import static com.mikea.bayes.VarSet.union;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author mike.aizatsky@gmail.com
 */
public class VarSetTest {
    private static final ProbabilitySpace space = new ProbabilitySpace("VarSetTest");
    private static final Var var0 = space.newVar("0", 1);
    private static final Var var1 = space.newVar("1", 2);
    private static final Var var2 = space.newVar("2", 2);
    private static final Var var3 = space.newVar("3", 3);
    private static final Var var4 = space.newVar("4", 4);

    @Test
    public void testToString() throws Exception {
        VarSet v1 = newVarSet(var1);
        VarSet v2 = newVarSet(var2, var1);
        VarSet v3 = newVarSet(var3, var2);

        assertEquals("{1}", v1.toString());
        assertEquals("{2, 1}", v2.toString());
        assertEquals("{3, 2}", v3.toString());
        assertEquals("{3(3), 2(2)}", v3.toString(true));
    }

    @Test
    public void testHasVariable() throws Exception {
        VarSet v = newVarSet(var2, var1);

        assertTrue(v.contains(var1));
        assertTrue(v.contains(var2));
        assertTrue(!v.contains(var3));
    }

    @Test
    public void testUnion() throws Exception {
        VarSet v1 = newVarSet(var1);
        VarSet v2 = newVarSet(var2, var1);
        VarSet v3 = newVarSet(var3, var2);

        assertEquals("{1, 2}", union(v1, v2).toString());
        assertEquals("{1, 2, 3}", union(v1, v3).toString());
        assertEquals("{1, 2, 3}", union(v2, v3).toString());
        assertEquals("{1, 2, 3}", union(v1, v2, v3).toString());
    }

    @Test
    public void testGetIndex() throws Exception {
        VarSet v = newVarSet(var1);
        verifyGetIndex(v);

        v = newVarSet(var1);
        verifyGetIndex(v);

        v = newVarSet(var0, var3);
        verifyGetIndex(v);
    }

    private void verifyGetIndex(VarSet v) {
        for (int i = 0; i < v.getCardinality(); ++i) {
            assertEquals(i, v.getIndex(v.getAssignment(i)));
        }
    }

    @Test
    public void testAssignmentOrder() throws Exception {

        assertEquals(
                "{1=0}\n" +
                "{1=1}", getAssignmentOrder(newVarSet(var1)));

        assertEquals(
                "{2=0, 3=0}\n" +
                "{2=0, 3=1}\n" +
                "{2=0, 3=2}\n" +
                "{2=1, 3=0}\n" +
                "{2=1, 3=1}\n" +
                "{2=1, 3=2}",
                getAssignmentOrder(newVarSet(var2, var3)));

        assertEquals(
                "{2=0, 3=0, 4=0}\n" +
                "{2=0, 3=0, 4=1}\n" +
                "{2=0, 3=0, 4=2}\n" +
                "{2=0, 3=0, 4=3}\n" +
                "{2=0, 3=1, 4=0}\n" +
                "{2=0, 3=1, 4=1}\n" +
                "{2=0, 3=1, 4=2}\n" +
                "{2=0, 3=1, 4=3}\n" +
                "{2=0, 3=2, 4=0}\n" +
                "{2=0, 3=2, 4=1}\n" +
                "{2=0, 3=2, 4=2}\n" +
                "{2=0, 3=2, 4=3}\n" +
                "{2=1, 3=0, 4=0}\n" +
                "{2=1, 3=0, 4=1}\n" +
                "{2=1, 3=0, 4=2}\n" +
                "{2=1, 3=0, 4=3}\n" +
                "{2=1, 3=1, 4=0}\n" +
                "{2=1, 3=1, 4=1}\n" +
                "{2=1, 3=1, 4=2}\n" +
                "{2=1, 3=1, 4=3}\n" +
                "{2=1, 3=2, 4=0}\n" +
                "{2=1, 3=2, 4=1}\n" +
                "{2=1, 3=2, 4=2}\n" +
                "{2=1, 3=2, 4=3}",
                getAssignmentOrder(newVarSet(var2, var3, var4)));
    }

    private String getAssignmentOrder(VarSet v) {
        StringBuilder order = new StringBuilder();
        for (int i = 0; i < v.getCardinality(); ++i) {
            if (i > 0) order.append("\n");
            order.append(v.getAssignment(i).toString());
        }

        return order.toString();
    }

    @Test
    public void testRemoveVars() throws Exception {
        VarSet v1 = newVarSet(var1, var2);
        VarSet v2 = newVarSet(var2);

        assertEquals("{1}", v1.removeVars(v2).toString());
    }
}
