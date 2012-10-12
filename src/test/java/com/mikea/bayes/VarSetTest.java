package com.mikea.bayes;

import org.junit.Test;

import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author mike.aizatsky@gmail.com
 */
public class VarSetTest {
    private static final Var var0 = new Var("0", 1);
    private static final Var var1 = new Var("1", 2);
    private static final Var var2 = new Var("2", 2);
    private static final Var var3 = new Var("3", 3);
    private static final Var var4 = new Var("4", 4);

    @Test
    public void testToString() throws Exception {
        VarSet v1 = newVarSet(var1);
        VarSet v2 = newVarSet(var2, var1);
        VarSet v3 = newVarSet(var3, var2);

        assertEquals("{1(2)}", v1.toString());
        assertEquals("{2(2), 1(2)}", v2.toString());
        assertEquals("{3(3), 2(2)}", v3.toString());
    }

    @Test
    public void testHasVariable() throws Exception {
        VarSet v = newVarSet(var2, var1);

        assertTrue(v.contains(var1));
        assertTrue(v.contains(var2));
        assertTrue(!v.contains(var3));
    }

    @Test
    public void testProduct() throws Exception {
        VarSet v1 = newVarSet(var1);
        VarSet v2 = newVarSet(var2, var1);
        VarSet v3 = newVarSet(var3, var2);

        assertEquals("{1(2), 2(2)}", VarSet.product(v1, v2).toString());
        assertEquals("{1(2), 2(2), 3(3)}", VarSet.product(v1, v3).toString());
        assertEquals("{1(2), 2(2), 3(3)}", VarSet.product(v2, v3).toString());
        assertEquals("{1(2), 2(2), 3(3)}", VarSet.product(v1, v2, v3).toString());
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
        for (int i = 0; i < v.getMaxIndex(); ++i) {
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
        for (int i = 0; i < v.getMaxIndex(); ++i) {
            if (i > 0) order.append("\n");
            order.append(v.getAssignment(i).toString());
        }

        return order.toString();
    }

    @Test
    public void testRemoveVars() throws Exception {
        VarSet v1 = newVarSet(var1, var2);
        VarSet v2 = newVarSet(var2);

        assertEquals("{1(2)}", v1.removeVars(v2).toString());
    }
}
