package com.mikea.bayes.examples;

import com.mikea.bayes.Factor;
import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.Var;
import org.junit.Test;

import static com.mikea.bayes.VarAssignment.at;
import static org.junit.Assert.assertEquals;

/**
 * Misconception example from Koller.
 *
 * @author mike.aizatsky@gmail.com
 */
public class MisconceptionTest {
    @Test
    public void testMisconceptionFactors() throws Exception {
        ProbabilitySpace space = new ProbabilitySpace();

        Var a = space.newVar("A", 2);
        Var b = space.newVar("B", 2);
        Var c = space.newVar("C", 2);
        Var d = space.newVar("D", 2);

        Factor phi1 = Factor
                .withVariables(a, b)
                .row(a, at(b, 0), new double[] {30, 1})
                .row(a, at(b, 1), new double[] {5, 10})
                .build();

        Factor phi2 = Factor
                .withVariables(b, c)
                .row(b, at(c, 0), new double[] {100, 1})
                .row(b, at(c, 1), new double[] {1, 100})
                .build();

        Factor phi3 = Factor
                .withVariables(c, d)
                .row(c, at(d, 0), new double[] {1, 100})
                .row(c, at(d, 1), new double[] {100, 1})
                .build();

        Factor phi4 = Factor
                .withVariables(d, a)
                .row(d, at(a, 0), new double[] {100, 1})
                .row(d, at(a, 1), new double[] {1, 100})
                .build();

        Factor unnormalizedMeasure = Factor.product(phi1, phi2, phi3, phi4);

        assertEquals(
                "Factor({A(2), B(2), C(2), D(2)}):\n" +
                        "{A=0, B=0, C=0, D=0}:    300,000.0\n" +
                        "{A=0, B=0, C=0, D=1}:    300,000.0\n" +
                        "{A=0, B=0, C=1, D=0}:    300,000.0\n" +
                        "{A=0, B=0, C=1, D=1}:         30.0\n" +
                        "{A=0, B=1, C=0, D=0}:        500.0\n" +
                        "{A=0, B=1, C=0, D=1}:        500.0\n" +
                        "{A=0, B=1, C=1, D=0}:  5,000,000.0\n" +
                        "{A=0, B=1, C=1, D=1}:        500.0\n" +
                        "{A=1, B=0, C=0, D=0}:        100.0\n" +
                        "{A=1, B=0, C=0, D=1}:  1,000,000.0\n" +
                        "{A=1, B=0, C=1, D=0}:        100.0\n" +
                        "{A=1, B=0, C=1, D=1}:        100.0\n" +
                        "{A=1, B=1, C=0, D=0}:         10.0\n" +
                        "{A=1, B=1, C=0, D=1}:    100,000.0\n" +
                        "{A=1, B=1, C=1, D=0}:    100,000.0\n" +
                        "{A=1, B=1, C=1, D=1}:    100,000.0\n",
                unnormalizedMeasure.toStringAsTable("%,12.1f"));
    }
}
