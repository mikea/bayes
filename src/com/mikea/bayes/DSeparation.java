package com.mikea.bayes;

import org.gga.graph.Edge;
import org.gga.graph.Graph;
import org.gga.graph.impl.SparseGraphImpl;
import org.gga.graph.sort.TopologicalSort;
import org.gga.graph.util.IntQueue;
import org.gga.graph.util.Pair;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.mikea.bayes.VarSet.newVarSet;
import static org.gga.graph.util.Pair.newPairOf;

/**
 * @author mike.aizatsky@gmail.com
 */
public class DSeparation {
    /**
     * Find all variables that are d-separated from sourceVariable given observations.
     */
    public static VarSet findDSeparation(BayesianNetwork network,
                                         int sourceVariable,
                                         VarSet observations) {
        BitSet observationBitSet = new BitSet();
        for (Var observation : observations) {
            observationBitSet.set(network.getVarIndex(observation));
        }
        BitSet bitSet = findDSeparated(network.getGraph(), sourceVariable, observationBitSet);
        Set<Var> vars = newHashSet();
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            vars.add(network.getVar(i));
        }
        return newVarSet(vars);
    }

    /**
     * Find all variables that are d-separated from sourceVariable given observations.
     */
    public static BitSet findDSeparated(Graph g,
                                        int sourceVariable,
                                        BitSet observations) {
        BitSet activeNodes = new BitSet();
        activeNodes.or(observations);
        int[] topologicalSort = TopologicalSort.sort(g);

        for (int i = topologicalSort.length - 1; i >= 0; --i) {
            int v = topologicalSort[i];
            for (Edge edge : g.getEdges(v)) {
                if (activeNodes.get(edge.w())) {
                    activeNodes.set(v);
                }
            }
        }

        Graph undirectedGraph = new SparseGraphImpl(g, false);

        BitSet visitedUp = new BitSet();
        BitSet visitedDown = new BitSet();
        BitSet reachable = new BitSet();
        int V = undirectedGraph.V();

        // Insert v for (v, up) and (v + V()) for (v, down)
        IntQueue vertexQueue = new IntQueue();
        vertexQueue.push(sourceVariable);

        while (!vertexQueue.isEmpty()) {
            int v = vertexQueue.pop();
            int direction = 1;
            if (v >= V) {
                v -= V;
                direction = -1;
            }
            BitSet visited = direction > 0 ? visitedUp : visitedDown;

            if (visited.get(v)) continue;

            if (!observations.get(v)) {
                reachable.set(v);
            }

            visited.set(v);

            if (direction > 0 && !observations.get(v)) {
                // trail through v is active only if v is not observed.
                for (Edge edge : undirectedGraph.getEdges(v)) {
                    if (edge.v() == v) {
                        // edge to child
                        vertexQueue.push(edge.w() + V); // visit child down
                    } else {
                        // edge to parent
                        vertexQueue.push(edge.v()); // visit parent up
                    }
                }
            } else if (direction < 0) {
                if (!observations.get(v)) {
                    // trails down to v children are active
                    for (Edge edge : g.getEdges(v)) {
                        vertexQueue.push(edge.w() + V); // visit child down
                    }
                }
                if (activeNodes.get(v)) {
                    // v-structure trails are active
                    for (Edge edge : undirectedGraph.getEdges(v)) {
                        if (edge.v() != v) {
                            // edge to parent
                            vertexQueue.push(edge.v()); // visit parent up
                        }
                    }
                }
            }
        }

        reachable.flip(0, V);
        return reachable;
    }

    public static List<Pair<Integer, Integer>> findAllDSeparatedPairs(BayesianNetwork network, VarSet observation) {
        List<Pair<Integer, Integer>> result = newArrayList();

        for (int i = 0; i < network.getGraph().V(); ++i) {
            VarSet dSeparation = findDSeparation(network, i, observation);

            for (int j = i + 1; j < network.getGraph().V(); j++) {
                if (dSeparation.hasVariable(network.getVar(j))) {
                    result.add(newPairOf(i, j));
                }
            }
        }

        return result;
    }
}
