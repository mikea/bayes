package com.mikea.bayes.belief;

import com.google.common.base.Function;
import com.mikea.bayes.VarSet;
import com.mikea.bayes.util.WeightedUnionFind;
import org.gga.graph.Edge;
import org.gga.graph.transform.Morph;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public final class PruneClusterGraph {
    public static ClusterGraph prune(ClusterGraph graph) {
        WeightedUnionFind unionFind = new WeightedUnionFind(graph.V());

        Set<VarSet> prunedNodes = newHashSet();

        for (int v = 0; v < graph.V(); ++v) {
            for (Edge edge : graph.getIntGraph().getEdges(v)) {
                int w = edge.other(v);

                VarSet vSet = graph.getNode(v);
                VarSet wSet = graph.getNode(w);

                if (vSet.containsAll(wSet) && !prunedNodes.contains(wSet)) {
                    unionFind.union(v, w);
                    prunedNodes.add(wSet);
                } else if (wSet.containsAll(vSet) && !prunedNodes.contains(vSet)) {
                    unionFind.union(v, w);
                    prunedNodes.add(vSet);
                }
            }
        }

        VarSet[] newNodes = new VarSet[graph.V()];
        for (int v = 0; v < graph.V(); ++v) {
            VarSet oldVarSet = graph.getNode(v);
            int v1 = unionFind.getComponent(v);
            if (newNodes[v1] == null) {
                newNodes[v1] = oldVarSet;
            } else {
                newNodes[v1] = VarSet.union(newNodes[v1], oldVarSet);
            }
        }

        final Map<VarSet, VarSet> oldToNewNodes = newHashMap();
        for (int v = 0; v < graph.V(); ++v) {
            VarSet oldVarSet = graph.getNode(v);
            int v1 = unionFind.getComponent(v);
            oldToNewNodes.put(oldVarSet, newNodes[v1]);
        }

        return new ClusterGraphImpl(graph.getProbabilitySpace(), Morph.morph(graph, new Function<VarSet, VarSet>() {
                    @Nullable
                    @Override
                    public VarSet apply(@Nullable VarSet input) {
                        return oldToNewNodes.get(input);
                    }
                }, new Function<List<VarSet>, VarSet>() {
                    @Nullable
                    @Override
                    public VarSet apply(@Nullable List<VarSet> input) {
                        return VarSet.intersect(input);
                    }
                }, VarSet.class, false
        ));
    }
}
