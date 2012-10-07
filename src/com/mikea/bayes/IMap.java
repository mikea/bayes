package com.mikea.bayes;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.gga.graph.util.Pair;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.mikea.bayes.VarSet.newVarSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public class IMap {
    private final Set<Independence> independences;  // todo: independencies?

    public IMap(Set<Independence> independences) {
        this.independences = independences;
    }

    public static IMap computeIMap(BayesianNetwork network) {
        Set<Var> vars = network.getVars();

        Set<Set<Var>> allPossibleObservations = Sets.powerSet(vars);
        Set<Independence> independences = newHashSet();


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
        Iterable<String> strings = Iterables.transform(independences, new Function<Independence, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Independence input) {
                return input != null ? input.toString() : null;
            }
        });

        String[] stringsArray = Iterables.toArray(strings, String.class);
        Arrays.sort(stringsArray);
        return Arrays.toString(stringsArray);
    }

    public boolean contains(IMap otherMap) {
        return independences.containsAll(otherMap.independences);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IMap iMap = (IMap) o;

        if (!independences.equals(iMap.independences)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return independences.hashCode();
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Independence that = (Independence) o;

            if (!observation.equals(that.observation)) return false;
            if (!var1.equals(that.var1)) return false;
            if (!var2.equals(that.var2)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = var1.hashCode();
            result = 31 * result + var2.hashCode();
            result = 31 * result + observation.hashCode();
            return result;
        }
    }
}
