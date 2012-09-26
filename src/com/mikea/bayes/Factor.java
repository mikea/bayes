package com.mikea.bayes;

import java.util.Arrays;

/**
 * @author mike.aizatsky@gmail.com
 */

//todo: equals()
public class Factor {
    final VarSet varSet;
    double[] values;

    private Factor(VarSet varSet, double[] values) {
        this.varSet = varSet;
        this.values = values;
    }

    @Override
    public String toString() {
        return "Factor(" + varSet + ", " + Arrays.toString(values) + ")";
    }

    public Factor product(Factor f2) {
        return Factors.product(this, f2);
    }

    public static Factor newFactor(int[] variables, int[] cardinality, double[] values) {
        return new Factor(new VarSet(variables, cardinality), values);
    }

    public VarSet getVarSet() {
        return varSet;
    }

    public static Factor newFactor(VarSet varSet, double[] values) {
        return new Factor(varSet, values);
    }
}
