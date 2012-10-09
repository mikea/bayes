package com.mikea.bayes;

/**
 * @author mike.aizatsky@gmail.com
 */
//todo FactorProduct should have the same interface as Factor.
public class FactorProduct {
    private final Factor[] factors;

    public FactorProduct(Factor[] factors) {
        this.factors = factors;
    }

    public Factor compute() {
        Factor result = factors[0];
        for (int i = 1; i < factors.length; i++) {
            result = result.product(factors[i]);
        }
        return result;
    }

    public double computeAt(VarAssignment assignment) {
        double result = 1;

        for (Factor factor : factors) {
            double value = factor.getValue(assignment);
            result *= value;
        }

        return result;
    }
}
