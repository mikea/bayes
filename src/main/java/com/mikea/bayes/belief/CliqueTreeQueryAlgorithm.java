package com.mikea.bayes.belief;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Evidence;
import com.mikea.bayes.Factor;
import com.mikea.bayes.VarSet;
import com.mikea.bayes.query.QueryAlgorithm;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.gga.graph.sort.TopologicalSort;

import javax.annotation.Nullable;

/**
 * @author mike.aizatsky@gmail.com
 */
public class CliqueTreeQueryAlgorithm implements QueryAlgorithm<QueryAlgorithm.Result> {

    @Override
    public Result run(final BayesianNetwork network) {
        return new Result() {
            @Override
            public Factor query(VarSet query, @Nullable Evidence evidence) {
                return queryImpl(network, query, evidence);
            }
        };
    }

    private static Factor queryImpl(BayesianNetwork network, VarSet query, @Nullable Evidence evidence) {
        ClusterGraph cliqueTree = CliqueTree.buildCliqueTree(network);

        TIntObjectMap<Factor> potentials = new TIntObjectHashMap<Factor>();

        for (Factor factor : network.getFactors()) {
            for (int i = 0; i < cliqueTree.V(); ++i) {
                if (cliqueTree.getNode(i).containsAll(factor.getScope())) {
                    Factor potential = potentials.get(i);
                    if (potential == null) {
                        potential = factor;
                    } else {
                        potential = potential.product(factor);
                    }
                    potentials.put(i, potential);
                }
            }
        }

        int[] order = TopologicalSort.sort(cliqueTree.getIntGraph());

        throw new UnsupportedOperationException();
    }
}
