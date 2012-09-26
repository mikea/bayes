package com.mikea.bayes;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * @author mike.aizatsky@gmail.com
 */
public class Factors {
    public static Factor product(Factor...factors) {
        if (factors.length == 1) return factors[0];
        return product(Arrays.asList(factors));
    }

    public static Factor product(Iterable<Factor> factors) {
        Iterable<VarSet> varSets = Iterables.transform(factors, new Function<Factor, VarSet>() {
            @Override
            public VarSet apply(@Nullable Factor factor) {
                assert factor != null;
                return factor.getVarSet();
            }
        });

        VarSet productVarSet = VarSet.product(varSets);
        int numValues = productVarSet.getMaxIndex();
        double[] values = new double[numValues];

        for (int i = 0; i < productVarSet.getMaxIndex(); ++i) {
            double value = 1;

            for (Factor factor : factors) {
                VarSet varSet = factor.getVarSet();
                int j = varSet.transformIndex(i, productVarSet);
                value *= factor.values[j];
            }

            values[i] = value;
        }

        return Factor.newFactor(productVarSet, values);
    }
}
