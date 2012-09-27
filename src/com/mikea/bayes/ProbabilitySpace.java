package com.mikea.bayes;

import com.google.common.base.Preconditions;

/**
 * Describes the probability space: number of random variables and their cardinalities.
 *
 * @author mike.aizatsky@gmail.com
 */
public class ProbabilitySpace {
    private final int variables;
    private final int[] cardinalities;

    public ProbabilitySpace(int variables, int[] cardinalities) {
        Preconditions.checkArgument(variables == cardinalities.length);
        this.variables = variables;
        this.cardinalities = cardinalities;
    }

    public int getNumberOfVariables() {
        return variables;
    }

    public int getCardinality(int var) {
        return cardinalities[var];
    }

    public int[] getCardinalities(int[] variables) {
        int[] result = new int[variables.length];
        for (int i = 0; i < variables.length; i++) {
            result[i] = cardinalities[variables[i]];
        }
        return result;
    }


}
