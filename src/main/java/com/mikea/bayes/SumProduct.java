package com.mikea.bayes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.mikea.bayes.VarSet.newVarSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public class SumProduct {
    public static Factor sumProductVariableElimination(
            Iterable<Var> vars,
            List<Factor> factors) {
        return sumProductVariableElimination(vars, factors, new MinNeighborsStrategy());
    }

    public static Factor sumProductVariableElimination(
            Iterable<Var> vars,
            List<Factor> factors,
            VarOrderStrategy strategy) {
        Set<Var> varSet = newHashSet(vars);
        while (!varSet.isEmpty()) {
            Var var = strategy.pickVar(varSet, factors);
            factors = sumProductEliminateVar(factors, var);
            varSet.remove(var);
        }

        return Factor.product(factors);
    }

    private static List<Factor> sumProductEliminateVar(List<Factor> factors, Var var) {
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

    // todo: allow supplying order strategy. Implement min-neighbors, min-weight, min-fill, weighted-min-fill
    public static interface VarOrderStrategy {
        Var pickVar(Set<Var> vars, List<Factor> factors);
    }

    public static abstract class GreedyOrderStrategy implements VarOrderStrategy {
        public abstract void computeCosts(TObjectIntMap<Var> costMap, List<Factor> factors);

        @Override
        public Var pickVar(Set<Var> vars, List<Factor> factors) {
            Var[] varArray = vars.toArray(new Var[vars.size()]);
            TObjectIntHashMap<Var> costs = new TObjectIntHashMap<Var>();

            for (Var var : varArray) {
                costs.put(var, 1);
            }
            computeCosts(costs, factors);


            Var minVar = varArray[0];
            int minCost = costs.get(minVar);

            for (int i = 1; i < varArray.length; i++) {
                Var var = varArray[i];
                int cost = costs.get(var);
                if (cost < minCost) {
                    minVar = var;
                    minCost = cost;
                }
            }

            return minVar;
        }
    }

    public static class MinNeighborsStrategy extends GreedyOrderStrategy {
        @Override
        public void computeCosts(TObjectIntMap<Var> costMap, List<Factor> factors) {
            Multimap<Var, Var> neighbors = HashMultimap.create();

            for (Factor factor : factors) {
                VarSet scope = factor.getScope();

                for (Var var : scope) {
                    neighbors.putAll(var, scope);
                }
            }

            for (Var var : neighbors.keySet()) {
                costMap.put(var, neighbors.get(var).size());
            }
        }
    }

    public static class MinWeightStrategy extends GreedyOrderStrategy {
        @Override
        public void computeCosts(TObjectIntMap<Var> costMap, List<Factor> factors) {
            Multimap<Var, Var> neighbors = HashMultimap.create();

            for (Factor factor : factors) {
                VarSet scope = factor.getScope();

                for (Var var : scope) {
                    neighbors.putAll(var, scope);
                }
            }

            for (Var var : neighbors.keySet()) {
                int card = 1;
                for (Var v : neighbors.get(var)) {
                    card *= v.getCardinality();
                }
                costMap.put(var, card);
            }
        }
    }
}
