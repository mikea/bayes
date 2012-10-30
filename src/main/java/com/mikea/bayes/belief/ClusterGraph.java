package com.mikea.bayes.belief;

import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.VarSet;
import org.gga.graph.maps.DataGraph;

/**
 * @author mike.aizatsky@gmail.com
 */
public interface ClusterGraph extends DataGraph<VarSet, VarSet> {
    ProbabilitySpace getProbabilitySpace();
}
