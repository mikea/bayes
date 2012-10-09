package com.mikea.bayes;

import com.google.common.base.Preconditions;
import org.gga.graph.Edge;
import org.gga.graph.Graph;
import org.gga.graph.impl.DataGraphImpl;
import org.gga.graph.search.dfs.AbstractDfsVisitor;
import org.gga.graph.util.Pair;

import javax.annotation.Nonnull;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
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

    public static BayesianNetwork buildFromSamples(Graph graph, int[] nValues, int[][] samples) {
        // TODO: implement
        throw new UnsupportedOperationException();
/*
        int root = 0;
        assert Trees.isTree(graph, root);
        int[] parents = ParentRecorder.computeParents(graph, root);

        double[][][] probabilities = new double[graph.V()][][];

        for (int i = 0; i < graph.V(); ++i) {
            if (i == root) continue;
            int parent = parents[i];
            assert parent >= 0;
            probabilities[i] = new double[nValues[parent]][];
            for (int j = 0; j < nValues[parent]; ++j) {
                probabilities[i][j] = new double[nValues[i]];
            }
        }

        for (int[] sample : samples) {
            for (int i = 0; i < graph.V(); ++i) {
                if (i == root) continue;
                int parent = parents[i];

                probabilities[i][sample[parent]][sample[i]] += 1;
            }
        }

        // Normalize probabilities
        for (int i = 0; i < graph.V(); ++i) {
            if (i == root) continue;

            for (int j = 0; j < probabilities[i].length; ++j) {
                double sum = 0;
                for (int k = 0; k < nValues[i]; ++k) {
                    sum += probabilities[i][j][k];
                }
                for (int k = 0; k < nValues[i]; ++k) {
                    probabilities[i][j][k] /= sum;
                }
            }
        }

        return new BayesianNetwork(graph, nValues, probabilities);
*/
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

    public Set<Var> getVars() {
        Set<Var> vars = newHashSet();
        for (int i = 0; i < graph.V(); i++) {
            vars.add(graph.getNode(i));
        }

        return Collections.unmodifiableSet(vars);
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
