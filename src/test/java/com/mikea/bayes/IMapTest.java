package com.mikea.bayes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class IMapTest {
    @Test
    public void testImap1() throws Exception {
        ProbabilitySpace space = new ProbabilitySpace();
        // A, B, C, D, E
        Var A = space.newVar("A", 2);
        Var B = space.newVar("B", 2);
        Var C = space.newVar("C", 2);
        Var D = space.newVar("D", 2);
        Var E = space.newVar("E", 2);

        BayesianNetwork network = BayesianNetwork
                .withVariables(A, B, C, D, E)
                .edge(A, C)
                .edge(A, E)
                .edge(B, C)
                .edge(B, D)
                .edge(C, E)
                .build();

        assertEquals("[(A ⊥ B | {D}), (A ⊥ B | {}), (A ⊥ D | {B, C, E}), (A ⊥ D | {B, C}), (A ⊥ D | {B, E}), (A ⊥ D | {B}), (A ⊥ D | {}), (B ⊥ E | {A, C, D}), (B ⊥ E | {A, C}), (C ⊥ D | {A, B, E}), (C ⊥ D | {A, B}), (C ⊥ D | {B, E}), (C ⊥ D | {B}), (D ⊥ E | {A, B, C}), (D ⊥ E | {A, B}), (D ⊥ E | {A, C}), (D ⊥ E | {B, C}), (D ⊥ E | {B})]", IMap.computeIMap(network).toString());
    }
}
