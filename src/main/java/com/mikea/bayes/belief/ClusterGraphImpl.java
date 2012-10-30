package com.mikea.bayes.belief;

import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.VarSet;
import org.gga.graph.impl.DataGraphImpl;
import org.gga.graph.maps.DataGraph;

/**
 * @author mike.aizatsky@gmail.com
 */
public class ClusterGraphImpl extends DataGraphImpl<VarSet, VarSet> implements ClusterGraph {
    private final ProbabilitySpace space;

    public ClusterGraphImpl(ProbabilitySpace space, int size) {
        super(VarSet.class, size, false);
        this.space = space;
    }

    public ClusterGraphImpl(ProbabilitySpace space, DataGraph<VarSet, VarSet> graph) {
        super(graph);
        this.space = space;
    }

    @Override
    public ProbabilitySpace getProbabilitySpace() {
        return space;
    }

    public static class Builder extends DataGraphImpl.Builder<VarSet, VarSet> {
        private final ProbabilitySpace space;

        public Builder(ProbabilitySpace space, boolean isDirected) {
            super(VarSet.class, VarSet.class, isDirected);
            this.space = space;
        }

        public ClusterGraph build() {
            return new ClusterGraphImpl(space, super.build());
        }
    }
}
