package com.mikea.bayes.belief;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Evidence;
import com.mikea.bayes.Factor;
import com.mikea.bayes.VarSet;
import com.mikea.bayes.query.QueryAlgorithm;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.gga.graph.Edge;
import org.gga.graph.impl.EdgeMapImpl;
import org.gga.graph.maps.EdgeMap;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public class BeliefPropagationAlgorithm implements QueryAlgorithm<BeliefPropagationAlgorithm.BPResult> {
    private final int iterations;

    public BeliefPropagationAlgorithm(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public BPResult run(BayesianNetwork network) {
        return new BPResult(iterations);
    }

    public static class BPResult extends QueryAlgorithm.Result {
        private final int iterations;
        private final Factor[] factors;
        private final ClusterGraphImpl clusterGraph;
        private EdgeMap<Factor> messages1;
        private EdgeMap<Factor> messages2;
        private TIntObjectMap<Factor> potentials;

        public BPResult(int iterations) {
            this.iterations = iterations;
            throw new UnsupportedOperationException();
        }

        public BPResult(Factor[] factors, ClusterGraphImpl clusterGraph, int iterations) {
            this.factors = factors;
            this.clusterGraph = clusterGraph;
            this.iterations = iterations;
            iterate();
        }

        private void iterate() {
            messages1 = new EdgeMapImpl<Factor>();
            messages2 = new EdgeMapImpl<Factor>();
            potentials = new TIntObjectHashMap<Factor>();

            nextFactor: for (Factor factor : factors) {
                for (int i = 0; i < clusterGraph.V(); ++i) {
                    if (clusterGraph.getNode(i).containsAll(factor.getScope())) {
                        Factor potential = potentials.get(i);
                        if (potential == null) {
                            potential = factor;
                        } else {
                            potential = potential.product(factor);
                        }
                        potentials.put(i, potential);
                        continue nextFactor;
                    }
                }
                throw new IllegalStateException();
            }

            Set<Edge> edges = newHashSet();
            for (int i = 0; i < clusterGraph.V(); ++i) {
                for (Edge edge : clusterGraph.getIntGraph().getEdges(i)) {
                    edges.add(edge);
                }
            }

            // Message loop
            List<Edge> edgeList = newArrayList(edges);
            for (int iteration = 0; iteration < iterations; ++iteration) {
                Collections.shuffle(edgeList);

                // Forward messages
                for (int i = 0; i < edgeList.size(); i++) {
                    Edge edge = edgeList.get(i);
                    sendMessage(edge.v(), edge.w(), edge);
                }

                // Backward messages
                for (int i = edgeList.size() - 1; i >= 0; i--) {
                    Edge edge = edgeList.get(i);
                    sendMessage(edge.w(), edge.v(), edge);
                }
            }

            // Compute final potentials.
            for (Edge edge : edgeList) {
                incorporateMessage(edge.v(), edge.w(), edge);
                incorporateMessage(edge.w(), edge.v(), edge);
            }
        }

        private void incorporateMessage(int from, int to, Edge edge) {
            Factor message = getMessage(from, to, edge);
            Factor potential = potentials.get(to);
            potential = potential.product(checkNotNull(message));
            potentials.put(to, potential);
        }

        private void sendMessage(int from, int to, Edge edge) {
            Factor newMessage = potentials.get(from);
            for (Edge e : clusterGraph.getIntGraph().getEdges(from)) {
                if (e.other(from) != to) {
                    Factor incomingMessage = getMessage(e.other(from), from, e);
                    if (incomingMessage != null) {
                        newMessage = newMessage.product(incomingMessage);
                    }
                }
            }
            VarSet toVars = clusterGraph.getNode(to);
            VarSet fromVars = clusterGraph.getNode(from);
            VarSet commonVars = fromVars.intersect(toVars);
            VarSet varsToEliminate = fromVars.removeVars(commonVars);
            newMessage = newMessage.marginalize(varsToEliminate);
            newMessage = newMessage.normalize();
            setMessage(newMessage, from, to, edge);
        }

        private void setMessage(Factor newMessage, int from, int to, Edge edge) {
            if (from > to) messages1.put(edge, newMessage);
            else messages2.put(edge, newMessage);
        }

        @Nullable
        private Factor getMessage(int v, int w, Edge edge) {
            if (v > w) return messages1.get(edge);
            else return messages2.get(edge);
        }


        @Override
        public Factor query(VarSet query, @Nullable Evidence evidence) {
            checkArgument(evidence == null, "Not implemented");

            for (int i = 0; i < clusterGraph.V(); ++i) {
                VarSet varSet = clusterGraph.getNode(i);
                if (varSet.containsAll(query)) {
                    Factor potential = potentials.get(i);
                    VarSet varsToMarginalize = potential.getScope().removeVars(query);
                    potential = potential.marginalize(varsToMarginalize);
                    return potential.normalize();
                }
            }

            throw new UnsupportedOperationException("No cluster node covering evidence. Not implemented.");
        }
    }
}
