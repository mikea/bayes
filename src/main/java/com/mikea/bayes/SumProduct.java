package com.mikea.bayes;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mikea.bayes.belief.ClusterGraph;
import com.mikea.bayes.belief.ClusterGraphImpl;
import com.mikea.bayes.belief.PruneClusterGraph;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static com.mikea.bayes.VarSet.newVarSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public final class SumProduct {
    public static final VarOrderStrategy DEFAULT_STRATEGY = new MinNeighborsStrategy();

    public static Factor sumProductVariableElimination(
            Iterable<Var> vars,
            List<Factor> factors) {
        return sumProductVariableElimination(vars, factors, DEFAULT_STRATEGY);
    }

    public static Factor sumProductVariableElimination(
            Iterable<Var> vars,
            List<Factor> factors,
            VarOrderStrategy strategy) {
        return sumProductVariableElimination(ProbabilitySpace.get(vars), vars, factors, strategy);
    }

    public static ClusterGraph buildCliqueGraph(
            Iterable<Var> vars,
            List<Factor> factors) {
        return buildCliqueGraph(vars, factors, DEFAULT_STRATEGY);
    }

    private static ClusterGraph buildCliqueGraph(Iterable<Var> vars, List<Factor> factors, VarOrderStrategy strategy) {
        ProbabilitySpace space = ProbabilitySpace.get(vars);
        List<VarSetHolder> scopes = newArrayList(Lists.transform(newArrayList(Factor.getScopes(factors)), new Function<VarSet, VarSetHolder>() {
            @Nullable
            @Override
            public VarSetHolder apply(@Nullable VarSet input) {
                return new VarSetHolder(checkNotNull(input), null);
            }
        }));

        ClusterGraphImpl.Builder builder = new ClusterGraphImpl.Builder(false);
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

    private static Factor sumProductVariableElimination(ProbabilitySpace space, Iterable<Var> vars, List<Factor> factors, VarOrderStrategy strategy) {
        Set<Var> varSet = newHashSet(vars);
        while (!varSet.isEmpty()) {
            Var var = strategy.pickVar(space, varSet, Factor.getScopes(factors));
            factors = sumProductEliminateVar(factors, var);
            varSet.remove(var);
        }

        return Factor.product(factors);
    }

    private static List<Factor> sumProductEliminateVar(Iterable<Factor> factors, Var var) {
        List<Factor> result = newArrayList();
        List<Factor> product = newArrayList();
        for (Factor factor : factors) {
            VarSet scope = factor.getScope();
            if (scope.contains(var)) {
                product.add(factor);
            } else {
                result.add(factor);
            }
        }

        result.add(Factor.product(product).marginalize(newVarSet(var)));
        return result;
    }

    public interface VarOrderStrategy {
        Var pickVar(ProbabilitySpace space, Set<Var> vars, Iterable<VarSet> factorScopes);
    }

    public abstract static class GreedyOrderStrategy implements VarOrderStrategy {
        public abstract void computeCosts(int[] costs, Set<Var> vars, Iterable<VarSet> factorScopes);

        @Override
        public Var pickVar(ProbabilitySpace space, Set<Var> vars, Iterable<VarSet> factorScopes) {
            int[] costs = new int[space.getNumVars()];
            Arrays.fill(costs, 1);

            computeCosts(costs, vars, factorScopes);


            Var minVar = null;
            int minCost = Integer.MAX_VALUE;

            for (Var var : vars) {
                int cost = costs[var.getIndex()];
                if (cost < minCost) {
                    minCost = cost;
                    minVar = var;
                }
            }

            return checkNotNull(minVar);
        }
    }

    public static class MinNeighborsStrategy extends GreedyOrderStrategy {
        @Override
        public void computeCosts(int[] costs, Set<Var> vars, Iterable<VarSet> factorScopes) {
            Multimap<Var, Var> neighbors = HashMultimap.create();

            for (VarSet scope : factorScopes) {
                for (Var var : scope) {
                    neighbors.putAll(var, scope);
                }
            }

            for (Var var : neighbors.keySet()) {
                costs[var.getIndex()] = neighbors.get(var).size();
            }
        }
    }

    public static class MinWeightStrategy extends GreedyOrderStrategy {
        @Override
        public void computeCosts(int[] costs, Set<Var> vars, Iterable<VarSet> factorScopes) {
            for (VarSet scope : factorScopes) {
                int factorCardinality = scope.getCardinality();

                for (Var var : scope) {
                    costs[var.getIndex()] *= factorCardinality;
                }
            }
        }
    }
}
