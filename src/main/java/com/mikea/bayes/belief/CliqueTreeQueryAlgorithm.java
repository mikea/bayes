package com.mikea.bayes.belief;

import com.mikea.bayes.BayesianNetwork;

/**
 * @author mike.aizatsky@gmail.com
 */
public class CliqueTreeQueryAlgorithm extends BeliefPropagationAlgorithm {

    public CliqueTreeQueryAlgorithm() {
        super(1);
    }

    @Override
    public BPResult run(BayesianNetwork network) {
        return new BPResult(network.getFactors(), CliqueTree.buildCliqueTree(network), iterations);
    }
}
