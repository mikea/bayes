package com.mikea.bayes;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static com.mikea.bayes.VarSet.newVarSet;

/**
 * @author mike.aizatsky@gmail.com
 */

//todo: equals()
public class Factor {
    private final VarSet scope;
    private final double[] values;

    private Factor(VarSet scope, double[] values) {
        checkArgument(scope.getMaxIndex() == values.length);
        this.scope = scope;
        this.values = values;
    }

    public static Factor newFactor(VarSet varSet, double[] values) {
        return new Factor(varSet, values);
    }

    public static Factor newFactor(Var[] vars, double[] doubles) {
        return newFactor(newVarSet(vars), doubles);
    }

    public static Factor product(Factor...factors) {
        if (factors.length == 1) return factors[0];
        return product(Arrays.asList(factors));
    }

    // todo: this should be part of FactorProduct?
    public static Factor product(Iterable<Factor> factors) {
        Iterable<VarSet> varSets = Iterables.transform(factors, new Function<Factor, VarSet>() {
            @Override
            public VarSet apply(@Nullable Factor factor) {
                assert factor != null;
                return factor.getScope();
            }
        });

        VarSet productVarSet = VarSet.product(varSets);
        int numValues = productVarSet.getMaxIndex();
        double[] values = new double[numValues];

        for (int i = 0; i < productVarSet.getMaxIndex(); ++i) {
            double value = 1;

            for (Factor factor : factors) {
                VarSet varSet = factor.getScope();
                int j = varSet.transformIndex(i, productVarSet);
                value *= factor.values[j];
            }

            values[i] = value;
        }

        return Factor.newFactor(productVarSet, values);
    }

    @Override
    public String toString() {
        return "Factor(" + scope + ", " + Arrays.toString(values) + ")";
    }

    public String toStringAsTable(String valueFormat) {
        StringBuilder result = new StringBuilder();
        result.append("Factor(");
        result.append(scope);
        result.append("):\n");

        for (VarAssignment assignment : scope.assignments()) {
            result.append(assignment.toString());
            result.append(": ");
            result.append(String.format(valueFormat, getValue(assignment)));
            result.append("\n");
        }
        return result.toString();
    }

    public String toStringAsTable(Var rowsVar, String valueFormat) {
        StringBuilder result = new StringBuilder();
        result.append("Factor(");
        result.append(scope);
        result.append("):\n");
        VarSet rowsVarSet = scope.removeVars(rowsVar);

        for (VarAssignment rowAssignment : rowsVarSet.assignments()) {
            result.append(rowAssignment.toString());
            result.append(": ");

            for (int i = 0; i < rowsVar.getCardinality(); ++i) {
                if (i > 0) result.append(" ");
                VarAssignment assignment = rowAssignment.set(rowsVar, i);
                result.append(String.format(valueFormat, getValue(assignment)));
            }
            result.append("\n");
        }


        return result.toString();
    }

    // todo: remove this method.
    public Factor product(Factor f2) {
        return product(this, f2);
    }

    public VarSet getScope() {
        return scope;
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

        return new Factor(scope, newValues);
    }

    /**
     * Sum out vars.
     */
    public Factor marginalize(VarSet vars) {
        VarSet newVarSet = scope.removeVars(vars);
        double[] newValues = new double[newVarSet.getMaxIndex()];

        for (int i = 0; i < values.length; ++i) {
            VarAssignment assignment = scope.getAssignment(i);
            newValues[newVarSet.getIndex(assignment)] += values[i];
        }

        return new Factor(newVarSet, newValues);
    }

    public Factor observeEvidence(Var[] observedVariables, int[] observedValues) {
        checkArgument(observedValues.length == observedVariables.length);
        double[] newValues = new double[values.length];

        Map<Var, BitSet> allowedValues = newHashMap();
        for (Var observedVariable : observedVariables) {
            allowedValues.put(observedVariable, new BitSet());
        }

        for (int i = 0; i < observedVariables.length; i++) {
            allowedValues.get(observedVariables[i]).set(observedValues[i]);
        }

        for (int i = 0; i < values.length; i++) {
            VarAssignment assignment = scope.getAssignment(i);

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

        return new Factor(scope, newValues);
    }

    public double[] getValues() {
        return values;
    }

    public double getValue(VarAssignment assignment) {
        return values[scope.getIndex(assignment)];
    }

    public double getValue(Var[] vars, int[] values) {
        return getValue(new VarAssignment(vars, values));
    }

    public double getValue(VarAssignment.Builder builder) {
        return getValue(builder.build());
    }

    public static Builder withVariables(Var... vars) {
        return new Builder().withVariables(vars);
    }

    /**
     * Normalize value for every possible assignment of vars.
     */
    public Factor normalizeBy(Var...vars) {
        Factor scale = marginalize(scope.removeVars(vars));

        double[] newValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            VarAssignment assignment = scope.getAssignment(i);
            newValues[i] = values[i] / scale.getValue(assignment);
        }

        return new Factor(scope, newValues);
    }

    public static class Builder {
        private VarSet varSet;
        private double[] values;

        public Builder withVariables(Var[] vars) {
            this.varSet = newVarSet(vars);
            this.values = new double[varSet.getMaxIndex()];
            return this;
        }

        public Builder row(Var var, VarAssignment.Builder assignmentBuilder, double[] values) {
            checkArgument(values.length == var.getCardinality());
            VarAssignment assignment = assignmentBuilder.build();
            checkArgument(!assignment.contains(var));

            for (int val = 0; val < var.getCardinality(); ++val) {
                VarAssignment a = assignment.set(var, val);
                int idx = varSet.getIndex(a);
                this.values[idx] = values[val];
            }

            return this;
        }

        public Factor build() {
            return newFactor(varSet, values);
        }

        public Builder add(VarAssignment.Builder at, double value) {
            VarAssignment assignment = at.build();
            int idx = varSet.getIndex(assignment);
            this.values[idx] += value;
            return this;
        }

        public Builder set(VarAssignment.Builder at, double value) {
            VarAssignment assignment = at.build();
            int idx = varSet.getIndex(assignment);
            this.values[idx] = value;
            return this;
        }

        public Builder uniform(double v) {
            Arrays.fill(values, v);
            return this;
        }
    }
}
