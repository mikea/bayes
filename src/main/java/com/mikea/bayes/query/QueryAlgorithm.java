package com.mikea.bayes.query;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Evidence;
import com.mikea.bayes.Factor;
import com.mikea.bayes.SumProduct;
import com.mikea.bayes.VarSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author mike.aizatsky@gmail.com
 */
public interface QueryAlgorithm<R extends QueryAlgorithm.Result> {
    VarElimination VAR_ELIMINATION = new VarElimination(new SumProduct.MinNeighborsStrategy());
    QueryAlgorithm DEFAULT = VAR_ELIMINATION;

    R prepare(BayesianNetwork network);

    abstract class Result {
        public abstract Factor query(VarSet query, @Nullable Evidence evidence);

        public double getProbability(@Nonnull Evidence evidence) {
            Factor factor = query(VarSet.newVarSet(evidence.getObservedVars()), null);
            return factor.getValue(evidence.getObservedVars(), evidence.getObservedValues());
        }

        public Factor query(VarSet query) {
            return query(query, null);
        }
    }
}
