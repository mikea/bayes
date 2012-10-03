package com.mikea.bayes;

import com.google.common.base.Preconditions;
import org.gga.graph.Edge;
import org.gga.graph.Graph;
import org.gga.graph.search.dfs.AbstractDfsVisitor;
import org.gga.graph.sort.TopologicalSort;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.mikea.bayes.VarSet.newVarSet;

public class BayesianNetwork {
    private final Graph graph;
    private final Factor[] factors;
    private final Var[] vars;

    // TODO: should be a data graph.
    public BayesianNetwork(Graph graph,
                           Var[] vars,
                           Factor[] factors) {
        this.vars = vars;
        Preconditions.checkArgument(graph.isDirected());
        this.graph = graph;
        this.factors = factors;
    }

    public static BayesianNetwork buildFromSamples(Graph graph, int[] nValues, int[][] samples) {
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

        graph.getDfs().depthFirstSearch(new AbstractDfsVisitor() {
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
        checkState(factor.getVarSet().equals(factorVariables),
                "Factor variables for vertex %s do not match graph structure. Expected: %s, actual: %s",
                v,
                factorVariables,
                factor.getVarSet());

        Factor marginalizedFactor = factor.marginalize(newVarSet(vars[v]));

        double[] values = marginalizedFactor.getValues();
        for (double value : values) {
            checkState(1.0 == value, "Marginalized distribution for %s should contain only 1, but is %s.", v, marginalizedFactor);
        }
    }

    public Factor computeJointDistribution() {
        int[] vertices = TopologicalSort.sort(graph);
        Factor result = factors[vertices[0]];
        for (int i = 1; i < vertices.length; ++i) {
            result = result.product(factors[vertices[i]]);
        }
        return result;
    }

    public double computeProbability(int[] values) {
        return computeProbability(new VarAssignment(vars, values));
    }

    public double computeProbability(VarAssignment assignment) {
        int[] vertices = TopologicalSort.sort(graph);
        double result = 1;

        for (int v : vertices) {
            Factor factor = factors[v];
            double value = factor.getValue(assignment);
            result *= value;
        }

        return result;
    }

    public Graph getGraph() {
        return graph;
    }

    public Var getVar(int j) {
        return vars[j];
    }

    public int getVarIndex(Var observation) {
        for (int i = 0; i < vars.length; i++) {
            Var var = vars[i];
            if (var == observation) return i;
        }
        throw new NoSuchElementException();
    }
}
