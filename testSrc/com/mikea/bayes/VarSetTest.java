package com.mikea.bayes;

import org.junit.Test;

import static com.mikea.bayes.Var.newVar;
import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author mike.aizatsky@gmail.com
 */
public class VarSetTest {
    private static final Var var0 = newVar("0", 1);
    private static final Var var1 = newVar("1", 2);
    private static final Var var2 = newVar("2", 2);
    private static final Var var3 = newVar("3", 3);

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

        assertTrue(v.hasVariable(var1));
        assertTrue(v.hasVariable(var2));
        assertTrue(!v.hasVariable(var3));
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
    public void testGetAssignment() throws Exception {
        VarSet v = newVarSet(var1);
        assertEquals("{1=0}", v.getAssignment(0).toString());
        assertEquals("{1=1}", v.getAssignment(1).toString());

        v = newVarSet(var1, var3);
        assertEquals("{1=0, 3=0}", v.getAssignment(0).toString());
        assertEquals("{1=1, 3=0}", v.getAssignment(1).toString());
        assertEquals("{1=0, 3=1}", v.getAssignment(2).toString());
        assertEquals("{1=1, 3=1}", v.getAssignment(3).toString());
        assertEquals("{1=0, 3=2}", v.getAssignment(4).toString());
        assertEquals("{1=1, 3=2}", v.getAssignment(5).toString());
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
    public void testRemoveVars() throws Exception {
        VarSet v1 = newVarSet(var1, var2);
        VarSet v2 = newVarSet(var2);

        assertEquals("{1(2)}", v1.removeVars(v2).toString());
    }
}
