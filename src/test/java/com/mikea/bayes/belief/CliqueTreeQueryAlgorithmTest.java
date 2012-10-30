package com.mikea.bayes.belief;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Evidence;
import com.mikea.bayes.Factor;
import com.mikea.bayes.StudentsNetwork;
import com.mikea.bayes.SumProduct;
import com.mikea.bayes.Var;
import com.mikea.bayes.query.QueryAlgorithm;
import com.mikea.bayes.query.VarElimination;
import org.junit.Test;

import static com.mikea.bayes.VarSet.newVarSet;
import static org.junit.Assert.assertEquals;

/**
 * @author mike.aizatsky@gmail.com
 */
public class CliqueTreeQueryAlgorithmTest {
    @Test
    public void testNoEvidence() throws Exception {
        QueryAlgorithm<?> baselineAlgorithm = new VarElimination(new SumProduct.MinNeighborsStrategy());
        QueryAlgorithm<?> algorithm = new CliqueTreeQueryAlgorithm();

        BayesianNetwork network = StudentsNetwork.buildStudentsNetwork();

        QueryAlgorithm.Result baselineResult = baselineAlgorithm.run(network);
        QueryAlgorithm.Result actualResult = algorithm.run(network);

        for (Var var : network.getVarList()) {
            Factor expected = baselineResult.query(newVarSet(var));
            Factor actual = actualResult.query(newVarSet(var));
            assertEquals(expected, actual);

            double baselineProbability = baselineResult.getProbability(new Evidence(new Var[]{var}, new int[]{0}));
            double actualProbability = actualResult.getProbability(new Evidence(new Var[]{var}, new int[]{0}));
            assertEquals(baselineProbability, actualProbability, 0.001);
        }
    }
}
