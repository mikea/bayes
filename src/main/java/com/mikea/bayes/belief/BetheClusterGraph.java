package com.mikea.bayes.belief;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.mikea.bayes.ProbabilitySpace;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarSet;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public class BetheClusterGraph {
    public static ClusterGraph buildBetheClusterGraph(List<VarSet> varSets) {

        Map<Var, VarSet> individualVarSets = newHashMap();

        for (Var var : VarSet.union(varSets)) {
            individualVarSets.put(var, VarSet.newVarSet(var));
        }

        final Set<VarSet> uniqueVarSets = newHashSet(Iterables.filter(varSets, new Predicate<VarSet>() {
            @Override
            public boolean apply(@Nullable VarSet varSet) {
                return checkNotNull(varSet).size() != 1;
            }
        }));

        // Preserve the original varsets order.
        List<VarSet> filteredVarSets = newArrayList(Iterables.filter(varSets, new Predicate<VarSet>() {
            @Override
            public boolean apply(@Nullable VarSet input) {
                return uniqueVarSets.contains(input);
            }
        }));

        ClusterGraphImpl result = new ClusterGraphImpl(
                ProbabilitySpace.fromVarSets(varSets), filteredVarSets.size() + individualVarSets.size());

        // set up vertices.
        {
            int idx = 0;
            for (VarSet varSet : filteredVarSets) {
                result.setNode(idx, varSet);
                idx++;
            }

            for (VarSet varSet : individualVarSets.values()) {
                result.setNode(idx, varSet);
                idx++;
            }
        }

        // set up edges.
        for (VarSet varSet : filteredVarSets) {
            for (Var var : varSet) {
                VarSet individualVarSet = individualVarSets.get(var);
                result.insert(varSet, individualVarSet, individualVarSet);
            }
        }

        return result;
    }
}
