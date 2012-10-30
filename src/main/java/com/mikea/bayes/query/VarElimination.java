package com.mikea.bayes.query;

import com.google.common.collect.Sets;
import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Evidence;
import com.mikea.bayes.Factor;
import com.mikea.bayes.SumProduct;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarSet;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.mikea.bayes.SumProduct.sumProductVariableElimination;

/**
 * @author mike.aizatsky@gmail.com
 */
public class VarElimination implements QueryAlgorithm<QueryAlgorithm.Result> {
    private final SumProduct.VarOrderStrategy strategy;

    public VarElimination(SumProduct.VarOrderStrategy strategy) {
        this.strategy = strategy;
    }


    @Override
    public Result run(final BayesianNetwork network) {
        return new Result() {
            @Override
            public Factor query(VarSet query, @Nullable Evidence evidence) {
                return queryImpl(network, query, evidence);
            }
        };
    }

    private Factor queryImpl(BayesianNetwork network, VarSet query, @Nullable Evidence evidence) {
        List<Factor> factors = newArrayList();
        for (Factor factor : network.getFactors()) {
            factors.add(factor.observeEvidence(evidence));
        }

        Sets.SetView<Var> varsToEliminate = Sets.difference(network.getVarSet(), query.getVarSet());
        return sumProductVariableElimination(varsToEliminate, factors, strategy).normalize();

    }
}
