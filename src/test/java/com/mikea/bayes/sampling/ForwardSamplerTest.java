package com.mikea.bayes.sampling;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarAssignment;
import com.mikea.bayes.data.StudentsNetwork;
import com.mikea.bayes.query.QueryAlgorithm;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class ForwardSamplerTest {
    @Test
    public void testProbabilities() throws Exception {
        BayesianNetwork network = StudentsNetwork.buildStudentsNetwork();
        int nSamples = 10000;

        QueryAlgorithm.Result queryResult = QueryAlgorithm.VAR_ELIMINATION.prepare(network);

        VarAssignment[] samples = ForwardSampler.sample(network, nSamples);

        int[][] counts = new int[network.V()][];
        for (VarAssignment sample : samples) {
            for (Var var : sample) {
                int v = var.getIndex();
                if (counts[v] == null) counts[v] = new int[var.getCardinality()];
                counts[v][sample.get(var)]++;
            }
        }

        for (int i = 0; i < network.V(); ++i) {
            Var var = network.getVar(i);
            int sum = 0;
            int[] varCounts = counts[var.getIndex()];
            for (int varCount : varCounts) {
                sum += varCount;
            }
            assertEquals(nSamples, sum);

            for (int j = 0; j < varCounts.length; j++) {
                double p = (double) varCounts[j] / sum;
                double expectedProb = queryResult.getProbability(new VarAssignment(new Var[]{var}, new int[]{j}));
                assertEquals(expectedProb, p, 1e-2);
            }

        }
    }
}
