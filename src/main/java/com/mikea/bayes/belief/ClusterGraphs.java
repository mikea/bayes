package com.mikea.bayes.belief;

import com.mikea.bayes.Factor;
import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarSet;
import org.gga.graph.Edge;
import org.gga.graph.connection.StrongComponents;
import org.gga.graph.impl.SubGraph;

/**
 * @author mike.aizatsky@gmail.com
 */
public final class ClusterGraphs {
    public static void validate(ClusterGraph clusterGraph, Iterable<Factor> factors) {
        validateFamilyPreservation(clusterGraph, factors);
        validateRunningIntersection(clusterGraph);
    }

    private static void validateRunningIntersection(final ClusterGraph clusterGraph) {
        ProbabilitySpace probabilitySpace = clusterGraph.getProbabilitySpace();

        for (final Var var : probabilitySpace.getVariables()) {
            SubGraph filteredGraph = new SubGraph(clusterGraph.getIntGraph(), new SubGraph.GraphFilter() {
                @Override
                public boolean acceptVertex(int v) {
                    return clusterGraph.getNode(v).contains(var);
                }

                @Override
                public boolean acceptEdge(Edge e) {
                    return clusterGraph.getEdge(e).contains(var);
                }
            });

            if (filteredGraph.V() != 0 && !StrongComponents.isConnected(filteredGraph)) {
                throw new IllegalArgumentException("Running intersection property failed. Original graph: " + clusterGraph + " var: " + var + " filtered graph: " + filteredGraph);
            }
        }
    }

    private static void validateFamilyPreservation(ClusterGraph clusterGraph, Iterable<Factor> factors) {
        nextFactor: for (Factor factor : factors) {
            for (int v = 0; v < clusterGraph.V(); ++v) {
                VarSet varSet = clusterGraph.getNode(v);
                if (varSet.containsAll(factor.getScope())) {
                    continue nextFactor;
                }
            }

            throw new IllegalArgumentException("Family preservation property failed.");
        }
    }
}
