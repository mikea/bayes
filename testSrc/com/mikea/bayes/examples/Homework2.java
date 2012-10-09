package com.mikea.bayes.examples;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.IMap;
import com.mikea.bayes.Var;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author mike.aizatsky@gmail.com
 */
public class Homework2 {
    @Test
    public void testImap1() throws Exception {
        Var a = new Var("A", 2);
        Var b = new Var("B", 2);
        Var c = new Var("C", 2);
        Var d = new Var("D", 2);

        BayesianNetwork g = BayesianNetwork
                .withVariables(a, b, c, d)
                .edge(a, b)
                .edge(b, c)
                .edge(b, d)
                .build();

        assertEquals(
                "[" +
                        "(A ⊥ C | {B, D}), " +
                        "(A ⊥ C | {B}), " +
                        "(A ⊥ D | {B, C}), " +
                        "(A ⊥ D | {B}), " +
                        "(C ⊥ D | {A, B}), " +
                        "(C ⊥ D | {B})]", IMap.computeIMap(g).toString());
    }

    @Test
    public void testImap() throws Exception {
        Var a = new Var("A", 2);
        Var b = new Var("B", 2);
        Var c = new Var("C", 2);
        Var d = new Var("D", 2);
        Var e = new Var("E", 2);

        BayesianNetwork g = BayesianNetwork
                .withVariables(a, b, c, d, e)
                .edge(a, b)
                .edge(a, c)
                .edge(b, d)
                .edge(c, d)
                .edge(d, e)
                .build();

        IMap m = IMap.computeIMap(g);
        assertEquals(
                "[(A ⊥ D | {B, C, E}), (A ⊥ D | {B, C}), (A ⊥ E | {B, C, D}), (A ⊥ E | {B, C}), (A ⊥ E | {B, D}), (A ⊥ E | {C, D}), (A ⊥ E | {D}), (B ⊥ C | {A}), (B ⊥ E | {A, C, D}), (B ⊥ E | {A, D}), (B ⊥ E | {C, D}), (B ⊥ E | {D}), (C ⊥ E | {A, B, D}), (C ⊥ E | {A, D}), (C ⊥ E | {B, D}), (C ⊥ E | {D})]",
                m.toString()
        );

        BayesianNetwork g1 = BayesianNetwork
                .withVariables(a, b, c, d, e)
                .edge(a, b)
                .edge(b, d)
                .edge(c, d)
                .edge(d, e)
                .build();

        IMap m1 = IMap.computeIMap(g1);
        assertEquals(
                "[(A ⊥ C | {B, D, E}), (A ⊥ C | {B, D}), (A ⊥ C | {B, E}), (A ⊥ C | {B}), (A ⊥ C | {}), (A ⊥ D | {B, C, E}), (A ⊥ D | {B, C}), (A ⊥ D | {B, E}), (A ⊥ D | {B}), (A ⊥ E | {B, C, D}), (A ⊥ E | {B, C}), (A ⊥ E | {B, D}), (A ⊥ E | {B}), (A ⊥ E | {C, D}), (A ⊥ E | {D}), (B ⊥ C | {A}), (B ⊥ C | {}), (B ⊥ E | {A, C, D}), (B ⊥ E | {A, D}), (B ⊥ E | {C, D}), (B ⊥ E | {D}), (C ⊥ E | {A, B, D}), (C ⊥ E | {A, D}), (C ⊥ E | {B, D}), (C ⊥ E | {D})]",
                m1.toString()
        );

        BayesianNetwork g2 = BayesianNetwork
                .withVariables(a, b, c, d, e)
                .edge(e, d)
                .edge(d, b)
                .edge(d, c)
                .edge(b, a)
                .edge(c, a)
                .build();

        IMap m2 = IMap.computeIMap(g2);
        assertEquals(
                "[(A ⊥ D | {B, C, E}), (A ⊥ D | {B, C}), (A ⊥ E | {B, C, D}), (A ⊥ E | {B, C}), (A ⊥ E | {B, D}), (A ⊥ E | {C, D}), (A ⊥ E | {D}), (B ⊥ C | {D, E}), (B ⊥ C | {D}), (B ⊥ E | {A, C, D}), (B ⊥ E | {A, D}), (B ⊥ E | {C, D}), (B ⊥ E | {D}), (C ⊥ E | {A, B, D}), (C ⊥ E | {A, D}), (C ⊥ E | {B, D}), (C ⊥ E | {D})]",
                m2.toString()
        );


        BayesianNetwork g3 = BayesianNetwork
                .withVariables(a, b, c, d, e)
                .edge(e, d)
                .edge(d, b)
                .edge(d, c)
                .edge(d, a)
                .edge(b, a)
                .edge(b, c)
                .edge(c, a)
                .build();

        IMap m3 = IMap.computeIMap(g3);
        assertEquals(
                "[(A ⊥ E | {B, C, D}), (A ⊥ E | {B, D}), (A ⊥ E | {C, D}), (A ⊥ E | {D}), (B ⊥ E | {A, C, D}), (B ⊥ E | {A, D}), (B ⊥ E | {C, D}), (B ⊥ E | {D}), (C ⊥ E | {A, B, D}), (C ⊥ E | {A, D}), (C ⊥ E | {B, D}), (C ⊥ E | {D})]",
                m3.toString()
        );

        assertFalse(m.contains(m1));
        assertFalse(m.contains(m2));
        assertTrue(m.contains(m3));
    }

    @Test
    public void testIEquivalent() throws Exception {
        Var a = new Var("A", 2);
        Var b = new Var("B", 2);
        Var c = new Var("C", 2);
        Var d = new Var("D", 2);
        Var e = new Var("E", 2);

        BayesianNetwork g = BayesianNetwork
                .withVariables(a, b, c, d, e)
                .edge(a, b)
                .edge(a, c)
                .edge(b, d)
                .edge(c, d)
                .edge(c, e)
                .build();

        BayesianNetwork g1 = BayesianNetwork
                .withVariables(a, b, c, d, e)
                .edge(a, b)
                .edge(b, d)
                .edge(c, a)
                .edge(c, d)
                .edge(e, c)
                .build();

        BayesianNetwork g2 = BayesianNetwork
                .withVariables(a, b, c, d, e)
                .edge(a, c)
                .edge(b, a)
                .edge(b, d)
                .edge(c, d)
                .edge(e, c)
                .build();

        BayesianNetwork g3 = BayesianNetwork
                .withVariables(a, b, c, d, e)
                .edge(b, a)
                .edge(b, d)
                .edge(c, a)
                .edge(c, d)
                .edge(c, e)
                .build();

        BayesianNetwork g4 = BayesianNetwork
                .withVariables(a, b, c, d, e)
                .edge(a, b)
                .edge(a, c)
                .edge(c, e)
                .edge(d, b)
                .edge(d, c)
                .build();

        IMap m = IMap.computeIMap(g);
        IMap m1 = IMap.computeIMap(g1);
        IMap m2 = IMap.computeIMap(g2);
        IMap m3 = IMap.computeIMap(g3);
        IMap m4 = IMap.computeIMap(g4);

        assertEquals(m, m1);
        assertFalse(m.equals(m2));
        assertFalse(m.equals(m3));
        assertFalse(m.equals(m4));
    }
}
