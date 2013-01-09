package com.mikea.bayes.belief;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Evidence;
import com.mikea.bayes.Factor;
import com.mikea.bayes.VarSet;
import com.mikea.bayes.query.QueryAlgorithm;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.gga.graph.Edge;
import org.gga.graph.Graph;
import org.gga.graph.Graphs;
import org.gga.graph.impl.EdgeMapImpl;
import org.gga.graph.maps.EdgeMap;
import org.gga.graph.search.dfs.AbstractDfsVisitor;
import org.gga.graph.tree.Trees;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

/**
 * @author mike.aizatsky@gmail.com
 */
public abstract class BeliefPropagationAlgorithm implements QueryAlgorithm<BeliefPropagationAlgorithm.BPResult> {
    protected final int iterations;

    protected BeliefPropagationAlgorithm(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public abstract BPResult prepare(BayesianNetwork network);

    public static class BPResult extends QueryAlgorithm.Result {
        private final int iterations;
        private final Factor[] factors;
        private final ClusterGraph clusterGraph;
        private EdgeMap<Factor> messages1;
        private EdgeMap<Factor> messages2;
        private TIntObjectMap<Factor> potentials;

        public BPResult(int iterations) {
            this.iterations = iterations;
            throw new UnsupportedOperationException();
        }

        public BPResult(Factor[] factors, ClusterGraph clusterGraph, int iterations) {
            this.factors = factors;
            this.clusterGraph = clusterGraph;
            this.iterations = iterations;
            iterate();
        }

        private void iterate() {
            messages1 = new EdgeMapImpl<Factor>();
            messages2 = new EdgeMapImpl<Factor>();
            potentials = new TIntObjectHashMap<Factor>();

            assignFactorsToClusters();

            Set<Edge> edges = Graphs.getAllEdges(clusterGraph.getIntGraph());

            // Message loop
            passMessages(edges);

            // Compute final potentials.
            computeFinalPotentials(edges);
        }

        private void passMessages(Iterable<Edge> edges) {
            Iterator<MessageDirection> i = getDirectionIterator(edges);
            while (i.hasNext()) {
                MessageDirection direction = i.next();
                sendMessage(direction.from, direction.to, direction.edge);
            }
        }

        private Iterator<MessageDirection> getDirectionIterator(Iterable<Edge> edges) {
            if (Trees.isTree(clusterGraph.getIntGraph())) {
                return getTreeIterator();
            } else {
                return getRandomEdgeOrderIterator(edges);
            }
        }

        private Iterator<MessageDirection> getTreeIterator() {
            final List<Edge> edgesInOrder = newArrayList();
            clusterGraph.getIntGraph().getDfs().depthFirstSearch(new AbstractDfsVisitor() {
                @Override
                public void treeEdge(Edge e, Graph graph) {
                    edgesInOrder.add(e);
                }

                @Override
                public void backEdge(Edge e, Graph graph) {
                    throw new IllegalStateException();
                }
            });

            List<MessageDirection> result = newArrayList();
            for (Edge edge : Lists.reverse(edgesInOrder)) {
                result.add(new MessageDirection(edge.w(), edge.v(), edge));
            }
            for (Edge edge : edgesInOrder) {
                result.add(new MessageDirection(edge.v(), edge.w(), edge));
            }
            return result.iterator();
        }

        private Iterator<MessageDirection> getRandomEdgeOrderIterator(Iterable<Edge> edges) {
            final List<Edge> edgeList = newArrayList(edges);

            return new AbstractIterator<MessageDirection>() {
                private boolean direction = false;
                private int iteration = 0;
                private  Iterator<Edge> i = null;
                @Override
                protected MessageDirection computeNext() {
                    if (i == null || !i.hasNext()) {
                        if (iteration > iterations * 2) {
                            endOfData();
                        }
                        direction = !direction;
                        if (direction) {
                            Collections.shuffle(edgeList);
                            i = edgeList.iterator();
                        } else {
                            i = Lists.reverse(edgeList).iterator();
                        }
                        ++iteration;
                    }

                    Edge edge = i.next();
                    int from = direction ? edge.v() : edge.w();
                    int to = direction ? edge.w() : edge.v();

                    return new MessageDirection(from, to, edge);
                }
            };
        }

        private void computeFinalPotentials(Iterable<Edge> edges) {
            for (Edge edge : edges) {
                incorporateMessage(edge.v(), edge.w(), edge);
                incorporateMessage(edge.w(), edge.v(), edge);
            }
        }

        private void assignFactorsToClusters() {
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

            for (int i = 0; i < clusterGraph.V(); ++i) {
                if (potentials.containsKey(i)) continue;
                potentials.put(i, Factor.constant(clusterGraph.getNode(i), 1));
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

    private static class MessageDirection {
        private final int from;
        private final int to;
        private final Edge edge;

        private MessageDirection(int from, int to, Edge edge) {
            this.from = from;
            this.to = to;
            this.edge = edge;
        }
    }
}
