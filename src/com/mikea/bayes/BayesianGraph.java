package com.mikea.bayes;

import org.gga.graph.Graph;
import org.gga.graph.search.bfs.ParentRecorder;
import org.gga.graph.tree.Trees;

public class BayesianGraph {
    private final Graph graph;

    /**
     * Number of values each possible graph node can have.
     */
    private final int[] numberOfValues;

    /**
     * Probability matrices.
     *
     * probabilities[vertex][parentValue][value];
     */
    private final double[][][] probabilities;

    public BayesianGraph(Graph graph, int[] numberOfValues, double[][][] probabilities) {
        this.graph = graph;
        this.numberOfValues = numberOfValues;
        this.probabilities = probabilities;
    }

    public static BayesianGraph buildFromSamples(Graph graph, int[] nValues, int[][] samples) {
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

        return new BayesianGraph(graph, nValues, probabilities);
    }
}
