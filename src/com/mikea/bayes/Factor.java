package com.mikea.bayes;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

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

    public static Factor newFactor(VarSet varSet, double[] values) {
        return new Factor(varSet, values);
    }

    public static Factor newFactor(Var[] vars, double[] doubles) {
        return newFactor(VarSet.newVarSet(vars), doubles);
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

        return Factor.newFactor(productVarSet, values);
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
            VarAssignment assignment = varSet.getAssignment(i);
            newValues[newVarSet.getIndex(assignment)] += values[i];
        }

        return new Factor(newVarSet, newValues);
    }

    public Factor observeEvidence(Var[] observedVariables, int[] observedValues) {
        Preconditions.checkArgument(observedValues.length == observedVariables.length);
        double[] newValues = new double[values.length];

        Map<Var, BitSet> allowedValues = newHashMap();
        for (Var observedVariable : observedVariables) {
            allowedValues.put(observedVariable, new BitSet());
        }

        for (int i = 0; i < observedVariables.length; i++) {
            allowedValues.get(observedVariables[i]).set(observedValues[i]);
        }

        for (int i = 0; i < values.length; i++) {
            VarAssignment assignment = varSet.getAssignment(i);

            boolean consistent = true;

            for (Var var : assignment) {
                int val = assignment.get(var);
                BitSet allowedValue = allowedValues.get(var);
                if (val >= 0 && allowedValue != null && !allowedValue.isEmpty() && !allowedValue.get(val)) {
                    consistent = false;
                    break;
                }
            }

            if (consistent) {
                newValues[i] = values[i];
            }
        }

        return new Factor(varSet, newValues);
    }



    public double[] getValues() {
        return values;
    }

    public double getValue(VarAssignment assignment) {
        return values[varSet.getIndex(assignment)];
    }

    public double getValue(Var[] vars, int[] values) {
        return getValue(new VarAssignment(vars, values));
    }

    public double getValue(VarAssignment.VarAssignmentBuilder builder) {
        return getValue(builder.build());
    }
}
