package com.mikea.bayes;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import org.gga.graph.maps.DataGraph;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.newHashMap;
import static com.mikea.bayes.VarSet.newVarSet;
import static org.gga.graph.impl.DataGraphImpl.newDataGraph;

/**
 * @author mike.aizatsky@gmail.com
 */

//todo: equals()
public class Factor {
    private final VarSet scope;
    private final double[] values;

    private Factor(VarSet scope, double[] values) {
        checkArgument(scope.getCardinality() == values.length);
        this.scope = scope;
        this.values = values;
    }

    public static Factor newFactor(VarSet varSet, double[] values) {
        return new Factor(varSet, values);
    }

    public static Factor newFactor(Var[] vars, double[] doubles) {
        return newFactor(newVarSet(vars), doubles);
    }

    public static Factor newFactor(Iterable<Var> vars, double[] doubles) {
        return newFactor(newVarSet(vars), doubles);
    }

    public static Factor product(Factor...factors) {
        if (factors.length == 1) return factors[0];
        return product(Arrays.asList(factors));
    }

    public static Factor product(List<Factor> factors) {
        if (factors.size() == 1) return factors.get(0);
        return product((Iterable<Factor>)factors);
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

        VarSet productVarSet = VarSet.union(varSets);
        int numValues = productVarSet.getCardinality();
        double[] values = new double[numValues];

        for (int i = 0; i < productVarSet.getCardinality(); ++i) {
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

    static List<VarSet> getScopes(List<Factor> factors) {
        return transform(factors, new Function<Factor, VarSet>() {
            @Nullable
            @Override
            public VarSet apply(@Nullable Factor input) {
                return checkNotNull(input).getScope();
            }
        });
    }

    @Override
    public String toString() {
        return "Factor(" + scope + ", " + Arrays.toString(values) + ")";
    }

    public String toString(final String valueFormat) {
        return "Factor(" + scope + ", " +
                Lists.transform(Doubles.asList(values), new Function<Double, Object>() {
                    @Override
                    public String apply(@Nullable Double input) {
                        return String.format(valueFormat, input);
                    }
                }) +
                ")";
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

    public String toStringAsTable(Var columnVar, String valueFormat) {
        StringBuilder result = new StringBuilder();
        result.append("Factor(");
        result.append(scope);
        result.append("):\n");
        VarSet rowsVarSet = scope.removeVars(columnVar);

        for (VarAssignment rowAssignment : rowsVarSet.assignments()) {
            result.append(rowAssignment.toString());
            result.append(": ");

            for (int i = 0; i < columnVar.getCardinality(); ++i) {
                if (i > 0) result.append(" ");
                VarAssignment assignment = rowAssignment.set(columnVar, i);
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
        double[] newValues = new double[newVarSet.getCardinality()];

        for (int i = 0; i < values.length; ++i) {
            VarAssignment assignment = scope.getAssignment(i);
            newValues[newVarSet.getIndex(assignment)] += values[i];
        }

        return new Factor(newVarSet, newValues);
    }

    public Factor observeEvidence(Var[] observedVariables, int[] observedValues) {
        checkArgument(observedValues.length == observedVariables.length);
        if (observedValues.length == 0) { return this; }
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

    public static DataGraph<Var, List<Factor>> induceMarkovNetwork(Factor... factors) {
        VarSet vars = VarSet.union(Iterables.transform(Arrays.asList(factors), new Function<Factor, VarSet>() {
            @Nullable
            @Override
            public VarSet apply(@Nullable Factor input) {
                return checkNotNull(input).getScope();
            }
        }));

        DataGraph<Var, List<Factor>> graph = newDataGraph(Var.class, vars.size(), false);

        int i = 0;
        for (Var var : vars) {
            graph.setNode(i, var);
            ++i;
        }

        for (int v = 0; v < graph.V(); ++v) {
            Var vVar = checkNotNull(graph.getNode(v));
            for (int w = v + 1; w < graph.V(); ++w) {
                Var wVar = checkNotNull(graph.getNode(w));

                List<Factor> edgeFactors = newArrayList();

                for (Factor factor : factors) {
                    VarSet scope = factor.getScope();
                    if (scope.contains(vVar) && scope.contains(wVar)) {
                        edgeFactors.add(factor);
                    }
                }

                if (!edgeFactors.isEmpty()) {
                    checkState(v != w);
                    graph.insert(v, w, edgeFactors);
                }
            }
        }

        return graph;
    }

    public static class Builder {
        private VarSet varSet;
        private double[] values;

        public Builder withVariables(Var[] vars) {
            this.varSet = newVarSet(vars);
            this.values = new double[varSet.getCardinality()];
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
            this.values[varSet.getIndex(assignment)] = value;
            return this;
        }

        public Builder uniform(double v) {
            Arrays.fill(values, v);
            return this;
        }

        public Builder row(Var var, VarAssignment.Builder at, String[] valueOrder, double[] doubles) {
            VarAssignment assignment = at.build();

            for (int i = 0; i < valueOrder.length; ++i) {
                String value = valueOrder[i];
                double d = doubles[i];

                VarAssignment a = assignment.set(var, value);
                this.values[varSet.getIndex(a)] = d;
            }

            return this;
        }
    }

}
