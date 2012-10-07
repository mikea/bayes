package com.mikea.bayes.examples;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.IMap;
import com.mikea.bayes.Var;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class Homework2 {
    @Test
    public void testImap() throws Exception {
        Var a = Var.newVar("A", 2);
        Var b = Var.newVar("B", 2);
        Var c = Var.newVar("C", 2);
        Var d = Var.newVar("D", 2);
        Var e = Var.newVar("E", 2);

        BayesianNetwork network = BayesianNetwork
                .withVariables(a, b, c, d, e)
                .edge(a, b)
                .edge(a, c)
                .edge(b, d)
                .edge(c, d)
                .edge(d, e)
                .build();

        IMap iMap = IMap.computeIMap(network);
        assertEquals(
                "[(A ⊥ E | {D}), (B ⊥ E | {D}), (C ⊥ E | {D}), (A ⊥ E | {B, D}), (C ⊥ E | {B, D}), (A ⊥ D | {B, C}), (A ⊥ E | {B, C}), (A ⊥ E | {C, D}), (B ⊥ E | {C, D}), (A ⊥ E | {B, C, D}), (A ⊥ D | {B, C, E}), (B ⊥ C | {A}), (B ⊥ E | {A, D}), (C ⊥ E | {A, D}), (C ⊥ E | {A, B, D}), (B ⊥ E | {A, C, D})]",
                iMap.toString()
        );
    }
}
