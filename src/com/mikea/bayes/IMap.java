package com.mikea.bayes;

import com.google.common.collect.Sets;
import org.gga.graph.util.Pair;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.mikea.bayes.VarSet.newVarSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public class IMap {
    private final List<Independence> independences;  // todo: independencies?

    public IMap(List<Independence> independences) {
        this.independences = independences;
    }

    public static IMap computeIMap(BayesianNetwork network) {
        Set<Var> vars = network.getVars();

        Set<Set<Var>> allPossibleObservations = Sets.powerSet(vars);
        List<Independence> independences = newArrayList();


        for (Set<Var> observation : allPossibleObservations) {
            VarSet varSet = newVarSet(observation);
            List<Pair<Integer,Integer>> dSeparatedPairs = DSeparation.findAllDSeparatedPairs(network, varSet);
            for (Pair<Integer, Integer> pair : dSeparatedPairs) {
                independences.add(new Independence(network.getVar(pair.first), network.getVar(pair.second), varSet));
            }
        }

        return new IMap(independences);
    }

    @Override
    public String toString() {
        return independences.toString();
    }

    public static class Independence {
        private final Var var1;
        private final Var var2;
        private final VarSet observation;

        public Independence(Var var1, Var var2, VarSet observation) {
            this.var1 = var1;
            this.var2 = var2;
            this.observation = observation;
        }

        @Override
        public String toString() {
            return "(" + var1.getName() + " \u22A5 " + var2.getName() + " | " + observation.toString(false) + ")";
        }
    }
}
