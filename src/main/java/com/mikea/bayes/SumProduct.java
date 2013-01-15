package com.mikea.bayes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
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
        return sumProductVariableElimination(ProbabilitySpace.fromVars(vars), vars, factors, strategy);
    }

    private static Factor sumProductVariableElimination(ProbabilitySpace space, Iterable<Var> vars, List<Factor> factors, VarOrderStrategy strategy) {
        Set<Var> varSet = newHashSet(vars);
        while (!varSet.isEmpty()) {
            System.out.println("varSet.size() = " + varSet.size());
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

        Factor newFactor = Factor.product(product).marginalize(newVarSet(var));
        System.out.println("newFactor.getCardinality = " + newFactor.getScope().getCardinality());
        result.add(newFactor);
        return result;
    }

    public interface VarOrderStrategy {
        Var pickVar(ProbabilitySpace space, Set<Var> vars, Iterable<VarSet> factorScopes);
    }

    public abstract static class GreedyOrderStrategy implements VarOrderStrategy {
        public abstract void computeCosts(long[] costs, Set<Var> vars, Iterable<VarSet> factorScopes);

        @Override
        public Var pickVar(ProbabilitySpace space, Set<Var> vars, Iterable<VarSet> factorScopes) {
            long[] costs = new long[space.getNumVars()];
            Arrays.fill(costs, 1);

            computeCosts(costs, vars, factorScopes);

            System.out.println("Arrays.toString(costs) = " + Arrays.toString(costs));

            Var minVar = null;
            long minCost = Long.MAX_VALUE;

            for (Var var : vars) {
                long cost = costs[var.getIndex()];
                if (cost < 0) cost = Long.MAX_VALUE;
                if (cost < minCost) {
                    minCost = cost;
                    minVar = var;
                }
            }
            System.out.println("minCost = " + minCost);
            System.out.println("minVar = " + minVar);

            return checkNotNull(minVar);
        }
    }

    public static class MinNeighborsStrategy extends GreedyOrderStrategy {
        @Override
        public void computeCosts(long[] costs, Set<Var> vars, Iterable<VarSet> factorScopes) {
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
        public void computeCosts(long[] costs, Set<Var> vars, Iterable<VarSet> factorScopes) {
            for (VarSet scope : factorScopes) {
                int factorCardinality = scope.getCardinality();
                checkState(factorCardinality != 0, "Zero cardinality factor: %s", scope);

                for (Var var : scope) {
                    costs[var.getIndex()] *= factorCardinality;
                }
            }
        }
    }

    public static class MinFillStrategy extends GreedyOrderStrategy {
        @Override
        public void computeCosts(long[] costs, Set<Var> vars, Iterable<VarSet> factorScopes) {
            Multimap<Var, Var> neighbors = HashMultimap.create();

            for (VarSet scope : factorScopes) {
                for (Var var : scope) {
                    neighbors.putAll(var, scope);
                }
            }

            for (Var var : neighbors.keySet()) {
                Collection<Var> varNeighbors = neighbors.get(var);

                int count = 0;
                for (Var var1 : varNeighbors) {
                    for (Var var2 : varNeighbors) {
                        if (var1.equals(var2) || var1.equals(var) || var2.equals(var)) continue;

                        if (!neighbors.containsEntry(var1, var2)) {
                            count++;
                        }
                    }
                }

                costs[var.getIndex()] = count / 2;
            }
        }
    }
}
