package com.mikea.bayes.belief;

import com.mikea.bayes.VarSet;
import org.gga.graph.impl.DataGraphImpl;

/**
 * @author mike.aizatsky@gmail.com
 */
public class ClusterGraphImpl extends DataGraphImpl<VarSet, VarSet> implements ClusterGraph {
    public ClusterGraphImpl(int size) {
        super(size, false);
    }

    @Override
    public void validate() {
        throw new UnsupportedOperationException();
    }
}
