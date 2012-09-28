package com.mikea.bayes;

import org.gga.graph.Edge;
import org.gga.graph.Graph;
import org.gga.graph.search.dfs.AbstractDfsVisitor;
import org.gga.graph.sort.TopologicalSort;

import java.util.BitSet;

import static com.google.common.base.Preconditions.checkState;
import static com.mikea.bayes.VarSet.newVarSet;

public class BayesianNetwork {
    private final ProbabilitySpace space;
    private final Graph graph;
    private final Factor[] factors;

    public BayesianNetwork(ProbabilitySpace space, Graph graph,
                           Factor[] factors) {
        this.space = space;
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
        // Validate graph against space
        checkState(space.getNumberOfVariables() == graph.V());

        // Validate factors
        checkState(factors.length == graph.V());

        final BitSet[] factorVariables = new BitSet[graph.V()];
        for (int i = 0; i < factorVariables.length; i++) {
            factorVariables[i] = new BitSet();
            factorVariables[i].set(i);
        }

        graph.getDfs().depthFirstSearch(new AbstractDfsVisitor() {
            @Override
            public void examineEdge(Edge e, Graph graph) {
                factorVariables[e.w()].set(e.v());
            }
        });

        for (int i = 0; i < factors.length; i++) {
            Factor factor = factors[i];
            validateFactor(i, factor, factorVariables[i]);
        }
    }

    private void validateFactor(int v, Factor factor, BitSet factorVariables) {
        checkState(factor.getVarSet().getVariables().equals(factorVariables),
                "Factor variables for vertex %s do not match graph structure. Expected: %s, actual: %s",
                v,
                factorVariables,
                factor.getVarSet().getVariables());

        Factor marginalizedFactor = factor.marginalize(newVarSet(space, new int[]{v}));

        double[] values = marginalizedFactor.getValues();
        for (int i = 0; i < values.length; i++) {
            double value = values[i];
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

    public double computeProbability(int[] assignment) {
        int[] vertices = TopologicalSort.sort(graph);
        double result = 1;

        for (int v : vertices) {
            Factor factor = factors[v];
            double value = factor.getValue(assignment);
            result *= value;
        }

        return result;
    }
}
