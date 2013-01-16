package com.mikea.bayes;

import com.google.common.base.Joiner;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.mikea.bayes.VarSet.newVarSet;
import static com.mikea.bayes.VarSet.union;
import static java.util.Collections.sort;
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
        assertGetIndex(v);

        v = newVarSet(var1);
        assertGetIndex(v);

        v = newVarSet(var0, var3);
        assertGetIndex(v);
    }

    private static void assertGetIndex(VarSet v) {
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

    private static String getAssignmentOrder(VarSet v) {
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

    @Test
    public void testScan() throws Exception {
        assertEquals(
                " 0 : {1=0}\n" +
                " 1 : {1=1}", scan(newVarSet(var1)));

        assertEquals(
                " 0 : {2=0, 3=0}\n" +
                " 1 : {2=0, 3=1}\n" +
                " 2 : {2=0, 3=2}\n" +
                " 3 : {2=1, 3=0}\n" +
                " 4 : {2=1, 3=1}\n" +
                " 5 : {2=1, 3=2}", scan(newVarSet(var2, var3)));

        assertEquals(
                " 0 : {1=0, 2=0, 3=0}\n" +
                " 1 : {1=0, 2=0, 3=1}\n" +
                " 2 : {1=0, 2=0, 3=2}\n" +
                " 3 : {1=0, 2=1, 3=0}\n" +
                " 4 : {1=0, 2=1, 3=1}\n" +
                " 5 : {1=0, 2=1, 3=2}\n" +
                " 6 : {1=1, 2=0, 3=0}\n" +
                " 7 : {1=1, 2=0, 3=1}\n" +
                " 8 : {1=1, 2=0, 3=2}\n" +
                " 9 : {1=1, 2=1, 3=0}\n" +
                "10 : {1=1, 2=1, 3=1}\n" +
                "11 : {1=1, 2=1, 3=2}", scan(newVarSet(var1, var2, var3)));
    }

    @Test
    public void testScanWith() throws Exception {
        assertEquals(
                " 0 : {1=0} -  0 : {1=0}\n" +
                " 1 : {1=1} -  1 : {1=1}", scanWith(newVarSet(var1), newVarSet(var1)));


        assertEquals(
                " 0 : {1=0, 2=0} -  0 : {1=0}\n" +
                " 1 : {1=0, 2=1} -  0 : {1=0}\n" +
                " 2 : {1=1, 2=0} -  1 : {1=1}\n" +
                " 3 : {1=1, 2=1} -  1 : {1=1}", scanWith(newVarSet(var1, var2), newVarSet(var1)));

        assertEquals(
                " 0 : {1=0, 2=0, 3=0} -  0 : {1=0, 2=0}\n" +
                " 1 : {1=0, 2=0, 3=1} -  0 : {1=0, 2=0}\n" +
                " 2 : {1=0, 2=0, 3=2} -  0 : {1=0, 2=0}\n" +
                " 3 : {1=0, 2=1, 3=0} -  1 : {1=0, 2=1}\n" +
                " 4 : {1=0, 2=1, 3=1} -  1 : {1=0, 2=1}\n" +
                " 5 : {1=0, 2=1, 3=2} -  1 : {1=0, 2=1}\n" +
                " 6 : {1=1, 2=0, 3=0} -  2 : {1=1, 2=0}\n" +
                " 7 : {1=1, 2=0, 3=1} -  2 : {1=1, 2=0}\n" +
                " 8 : {1=1, 2=0, 3=2} -  2 : {1=1, 2=0}\n" +
                " 9 : {1=1, 2=1, 3=0} -  3 : {1=1, 2=1}\n" +
                "10 : {1=1, 2=1, 3=1} -  3 : {1=1, 2=1}\n" +
                "11 : {1=1, 2=1, 3=2} -  3 : {1=1, 2=1}", scanWith(newVarSet(var1, var2, var3), newVarSet(var1, var2)));
    }

    private static String scan(VarSet v) {
        final List<String> order = newArrayList();
        v.scan(new VarSet.Scanner() {
            @Override
            public void scan(int index, VarAssignment varAssignment) {
                order.add(String.format("%2d : %s", index, varAssignment.toString()));
            }
        });
        sort(order);
        return Joiner.on("\n").join(order);
    }

    private static String scanWith(final VarSet v1, final VarSet v2) {
        final List<String> order = newArrayList();
        v1.scanWith(v2, new VarSet.WithScanner() {
            @Override
            public void scan(int index1, int index2) {
                VarAssignment v1Assignment = v1.getAssignment(index1);
                VarAssignment v2Assignment = v2.getAssignment(index2);
                order.add(String.format("%2d : %s - %2d : %s", index1, v1Assignment, index2, v2Assignment));
            }
        });
        sort(order);
        return Joiner.on("\n").join(order);
    }

}
