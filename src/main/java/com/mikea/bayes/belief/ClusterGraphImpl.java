package com.mikea.bayes.belief;

import com.mikea.bayes.VarSet;
import org.gga.graph.impl.DataGraphImpl;
import org.gga.graph.maps.DataGraph;

/**
 * @author mike.aizatsky@gmail.com
 */
public class ClusterGraphImpl extends DataGraphImpl<VarSet, VarSet> implements ClusterGraph {
    public ClusterGraphImpl(int size) {
        super(VarSet.class, size, false);
    }

    public ClusterGraphImpl(DataGraph<VarSet, VarSet> graph) {
        super(graph);
    }

    @Override
    public void validate() {
        throw new UnsupportedOperationException();
    }

    public static class Builder extends DataGraphImpl.Builder<VarSet, VarSet> {
        public Builder(boolean isDirected) {
            super(VarSet.class, VarSet.class, isDirected);
        }

        public ClusterGraph build() {
            return new ClusterGraphImpl(super.build());
        }
    }
}
