package com.mikea.bayes.query;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Factor;
import com.mikea.bayes.SumProduct;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public interface QueryAlgorithm {
    QueryAlgorithm DEFAULT = new VarElimination(new SumProduct.MinNeighborsStrategy());

    Factor query(BayesianNetwork network, VarSet query, Var[] observedVariables, int[] observedValues);
}
