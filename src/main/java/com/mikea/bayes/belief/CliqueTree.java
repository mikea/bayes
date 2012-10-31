package com.mikea.bayes.belief;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Factor;
import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.SumProduct;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarSet;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.mikea.bayes.VarSet.newVarSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public final class CliqueTree {
    public static ClusterGraph buildCliqueTree(
            BayesianNetwork network) {
        return buildCliqueTree(network.getVarList(), network.getFactorList());
    }


    public static ClusterGraph buildCliqueTree(
            Iterable<Var> vars,
            List<Factor> factors) {
        return buildCliqueTreeFromFactors(vars, factors, SumProduct.DEFAULT_STRATEGY);
    }

    private static ClusterGraph buildCliqueTreeFromFactors(Iterable<Var> vars, List<Factor> factors, SumProduct.VarOrderStrategy strategy) {
        List<VarSet> factorScopes = Factor.getScopes(factors);
        return buildCliqueTree(vars, factorScopes, strategy);
    }

    public static ClusterGraph buildCliqueTree(Iterable<Var> vars, Iterable<VarSet> factorScopes, SumProduct.VarOrderStrategy strategy) {
        ProbabilitySpace space = ProbabilitySpace.fromVars(vars);
        List<VarSetHolder> scopes = newArrayList(Lists.transform(newArrayList(factorScopes), new Function<VarSet, VarSetHolder>() {
            @Nullable
            @Override
            public VarSetHolder apply(@Nullable VarSet input) {
                return new VarSetHolder(checkNotNull(input), null);
            }
        }));

        ClusterGraphImpl.Builder builder = new ClusterGraphImpl.Builder(space, false);
        Set<Var> varsToEliminate = newLinkedHashSet(vars);

        while (!varsToEliminate.isEmpty()) {
            Var var = strategy.pickVar(space, varsToEliminate, Lists.transform(scopes, new Function<VarSetHolder, VarSet>() {
                @Nullable
                @Override
                public VarSet apply(@Nullable VarSetHolder input) {
                    return checkNotNull(input).varSet;
                }
            }));
            varsToEliminate.remove(var);

            VarSet clique = newVarSet(var);

            List<VarSet> srcs = newArrayList();

            for (Iterator<VarSetHolder> i = scopes.iterator(); i.hasNext(); ) {
                VarSetHolder holder = i.next();
                VarSet scope = holder.varSet;
                if (scope.contains(var)) {
                    i.remove();
                    clique = clique.add(scope);
                    if (holder.clique != null) {
                        srcs.add(holder.clique);
                    }
                }
            }


            builder.addNode(clique);
            for (VarSet src : srcs) {
                builder.addEdge(src, clique, VarSet.intersect(src, clique));
            }

            VarSet newScope = clique.removeVars(var);
            if (!newScope.isEmpty()) {
                scopes.add(new VarSetHolder(newScope, clique));
            }
        }

        return PruneClusterGraph.prune(builder.build());
    }

    private static class VarSetHolder {
        private final VarSet varSet;
        private final VarSet clique;

        private VarSetHolder(VarSet varSet, @Nullable VarSet clique) {
            this.varSet = varSet;
            this.clique = clique;
        }

        @Override
        public String toString() {
            return "VarSetHolder{" +
                    "varSet=" + varSet +
                    ", clique=" + clique +
                    '}';
        }
    }
}
