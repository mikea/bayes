package com.mikea.bayes;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.gga.graph.Edge;
import org.gga.graph.Graph;
import org.gga.graph.impl.DataGraphImpl;
import org.gga.graph.search.dfs.AbstractDfsVisitor;
import org.gga.graph.sort.TopologicalSort;
import org.gga.graph.util.Pair;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.mikea.bayes.SumProduct.sumProductVariableElimination;
import static com.mikea.bayes.VarSet.newVarSet;

public class BayesianNetwork {
    @Nonnull
    private final DataGraphImpl<Var, Void> graph;
    @Nonnull
    private final Factor[] factors;

    // TODO: move factors into dataGraph.
    private BayesianNetwork(@Nonnull DataGraphImpl<Var, Void> graph,
                            @Nonnull Factor[] factors) {
        Preconditions.checkArgument(graph.isDirected());
        this.graph = graph;
        this.factors = factors;
    }

    public void validate() {
        // Validate factors
        checkState(factors.length == graph.V());

        final Map<Var, Set<Var>> factorVariables = newHashMap();
        for (int i = 0; i < graph.V(); i++) {
            Var var = getVar(i);
            HashSet<Var> s = newHashSet(var);
            factorVariables.put(var, s);
        }

        graph.getIntGraph().getDfs().depthFirstSearch(new AbstractDfsVisitor() {
            @Override
            public void examineEdge(Edge e, Graph graph) {
                Var w = getVar(e.w());
                Var v = getVar(e.v());
                factorVariables.get(w).add(v);
            }
        });

        for (int i = 0; i < factors.length; i++) {
            Factor factor = factors[i];
            Var var = getVar(i);
            validateFactor(i, factor, factorVariables.get(var));
        }
    }

    private void validateFactor(int v, Factor factor, Set<Var> factorVariables) {
        checkState(factor.getScope().equals(factorVariables),
                "Factor variables for vertex %s do not match graph structure. Expected: %s, actual: %s",
                v,
                factorVariables,
                factor.getScope());

        Factor marginalizedFactor = factor.marginalize(newVarSet(getVar(v)));

        double[] values = marginalizedFactor.getValues();
        for (double value : values) {
            checkState(1.0 == value, "Marginalized distribution for %s should contain only 1, but is %s.", v, marginalizedFactor);
        }
    }

    public Factor computeJointDistribution() {
        return new FactorProduct(factors).compute();
    }

    public double computeProbability(VarAssignment assignment) {
        return new FactorProduct(factors).computeAt(assignment);
    }

    public Graph getGraph() {
        return graph.getIntGraph();
    }

    public Var getVar(int j) {
        return checkNotNull(graph.getNode(j));
    }

    public int getVarIndex(Var observation) {
        return graph.getIndex(observation);
    }

    public static Builder withVariables(Var...vars) {
        return new Builder().withVariables(vars);
    }

    public double computeProbability(VarAssignment.Builder at) {
        return computeProbability(at.build());
    }

    public Set<Var> getVarSet() {
        Set<Var> vars = newHashSet();
        for (int i = 0; i < graph.V(); i++) {
            vars.add(graph.getNode(i));
        }

        return Collections.unmodifiableSet(vars);
    }

    public List<Var> getVarList() {
        ImmutableList.Builder<Var> result = new ImmutableList.Builder<Var>();
        for (int i = 0; i < graph.V(); i++) {
            result = result.add(graph.getNode(i));
        }

        return result.build();
    }

    public Factor query(Var...queryVars) {
        return query(newVarSet(queryVars));
    }

    public Factor query(VarSet query) {
        return query(query, new Var[]{}, new int[]{});
    }

    public Factor query(VarSet query, Var[] observedVariables, int[] observedValues) {
        List<Var> vars = newArrayList();

        int[] order = TopologicalSort.sort(graph.getIntGraph());

        for (int i = 0; i < graph.V(); i++) {
            Var var = getVar(order[i]);
            if (!query.contains(var)) {
                vars.add(var);
            }
        }

        List<Factor> factors = newArrayList();
        for (Factor factor : this.factors) {
            factors.add(factor.observeEvidence(observedVariables, observedValues));
        }

        return sumProductVariableElimination(vars, newArrayList(factors)).normalize();
    }

    @Nonnull
    public Factor[] getFactors() {
        return factors;
    }

    public static Builder withVariables(Iterable<Var> vars) {
        return withVariables(Iterables.toArray(vars, Var.class));
    }

    public Var getVarByName(String varName) {
        for (int i = 0; i < graph.V(); ++i) {
            if (getVar(i).getName().equals(varName)) return getVar(i);
        }

        throw new NoSuchElementException();
    }

    public Factor getFactor(Var v) {
        return factors[getVarIndex(v)];
    }

    public Factor query(Var[] queryVars, Var[] vars, String[] values) {
        int[] intValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            intValues[i] = vars[i].getValueIndex(values[i]);
        }

        return query(newVarSet(queryVars), vars, intValues);
    }

    public static class Builder {
        private Var[] vars;
        private final List<Pair<Var, Var>> edges = newArrayList();
        private final Map<Var, Factor> factors = newHashMap();

        public Builder withVariables(Var[] vars) {
            this.vars = vars;
            return this;
        }

        public BayesianNetwork build() {
            DataGraphImpl<Var, Void> g = new DataGraphImpl<Var, Void>(vars.length, true);
            for (int i = 0; i < vars.length; i++) {
                g.setNode(i, vars[i]);
            }

            for (Pair<Var, Var> edge : edges) {
                g.insert(edge.first, edge.second, null);
            }

            Factor[] factorArray = new Factor[vars.length];
            for (int i = 0; i < vars.length; i++) {
                Var var = vars[i];
                factorArray[i] = factors.get(var);
            }

            return new BayesianNetwork(g, factorArray);
        }

        public Builder edge(Var from, Var to) {
            edges.add(Pair.of(from, to));
            return this;
        }

        public Builder factor(Var var, Factor factor) {
            factors.put(var, factor);
            return this;
        }
    }
}
