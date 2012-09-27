package com.mikea.bayes;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * @author mike.aizatsky@gmail.com
 */

//todo: equals()
public class Factor {
    private final VarSet varSet;
    private double[] values;

    private Factor(VarSet varSet, double[] values) {
        Preconditions.checkArgument(varSet.getMaxIndex() == values.length);
        this.varSet = varSet;
        this.values = values;
    }

    public static Factor createFactor(ProbabilitySpace space, int[] variables, double[] values) {
        VarSet varSet = VarSet.createVarSet(space, variables);

        // Reorder values, since variables might not be in ascending order.
        double[] orderedValues = new double[values.length];
        int[] cards = space.getCardinalities(variables);
        int maxIdx = 1;
        for (int card : cards) {
            maxIdx *= card;
        }

        int[] assignment = new int[space.getNumberOfVariables()];
        for (int i = 0; i < maxIdx; ++i) {
            // First compute the assignment based on passed variables
            int idx = i;
            for (int var : variables) {
                int cardinality = space.getCardinality(var);
                int varValue = idx % cardinality;
                assignment[var] = varValue;
                idx = idx / cardinality;
            }
            // Now store value at correct index.
            orderedValues[varSet.getIndex(assignment)] = values[i];
        }

        return createFactor(varSet, orderedValues);
    }

    public static Factor createFactor(VarSet varSet, double[] values) {
        return new Factor(varSet, values);
    }

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

        return Factor.createFactor(productVarSet, values);
    }

    @Override
    public String toString() {
        return "Factor(" + varSet + ", " + Arrays.toString(values) + ")";
    }

    public Factor product(Factor f2) {
        return product(this, f2);
    }

    public VarSet getVarSet() {
        return varSet;
    }

    public double sum() {
        double result = 0;
        for (double value : values) {
            result += value;
        }
        return result;
    }

    public Factor normalize() {
        double sum = sum();
        Preconditions.checkState(sum != 0);
        double[] newValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i] / sum;
        }

        return new Factor(varSet, newValues);
    }

    /**
     * sum out vars.
     */
    public Factor marginalize(VarSet vars) {
        VarSet newVarSet = varSet.removeVars(vars);
        double[] newValues = new double[newVarSet.getMaxIndex()];

        for (int i = 0; i < values.length; ++i) {
            int[] assignment = varSet.getAssignment(i);
            newValues[newVarSet.getIndex(assignment)] += values[i];
        }

        return new Factor(newVarSet, newValues);
    }
}
