package com.mikea;

import com.mikea.bayes.BayesianGraph;
import org.gga.graph.MutableGraph;
import org.gga.graph.impl.SparseGraphImpl;

public class Tutorial1 {
    public static void main(String[] args) {
        // [c, e, s, d, f]
        MutableGraph graph = new SparseGraphImpl(5, true);

        graph.insert(0, 1);
        graph.insert(0, 4);
        graph.insert(1, 2);
        graph.insert(1, 3);

        int[] nValues = {2, 3, 7, 4, 20};

        int[][] samples = {
                {0, 0, 1, 0, 4},
                {0, 0, 1, 1, 5},
                {1, 0, 2, 1, 3},
                {0, 0, 1, 2, 2},
                {0, 0, 2, 0, 3},
                {1, 1, 0, 0, 6},
                {0, 1, 2, 1, 9},
                {0, 1, 3, 2, 3},
                {0, 2, 4, 0, 7},
                {1, 2, 3, 1, 2},
                {1, 2, 5, 2, 0},
                {0, 2, 6, 0, 1},
                {1, 2, 0, 3, 8},
                {1, 2, 1, 3, 7},
                {1, 2, 6, 3, 0},
        };

        BayesianGraph bayesianGraph = BayesianGraph.buildFromSamples(graph, nValues, samples);

        System.out.println();
    }
}
