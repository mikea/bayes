package com.mikea.bayes.sampling;

import com.mikea.bayes.BayesianNetwork;
import com.mikea.bayes.Factor;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarAssignment;
import com.mikea.bayes.VarSet;
import org.gga.graph.Graph;
import org.gga.graph.sort.TopologicalSort;

import java.util.Arrays;
import java.util.Random;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author mike.aizatsky@gmail.com
 */
public final class ForwardSampler {
    public static VarAssignment sample(BayesianNetwork network) {
        Random random = new Random();
        Graph graph = network.getGraph();
        int[] vertices = TopologicalSort.sort(graph);
        return sample(network, random, vertices);
    }

    public static VarAssignment[] sample(BayesianNetwork network, int samples) {
        Random random = new Random();
        Graph graph = network.getGraph();
        int[] vertices = TopologicalSort.sort(graph);
        VarAssignment[] result = new VarAssignment[samples];
        for (int i = 0; i < result.length; i++) {
            result[i] = sample(network, random, vertices);
        }
        return result;
    }


    private static VarAssignment sample(BayesianNetwork network, Random random, int[] topoVertices) {
        int[] values = new int[topoVertices.length];
        Arrays.fill(values, -1);

        for (int v : topoVertices) {
            Var var = network.getVar(v);
            Factor f = network.getFactor(var);
            VarSet scope = f.getScope();

            VarAssignment.Builder builder = new VarAssignment.Builder();
            for (Var scopeVar : scope) {
                if (scopeVar.equals(var)) continue;
                int idx = scopeVar.getIndex();
                checkState(values[idx] != -1, "Wrong var order. var=%s scopeVar=%s", var, scopeVar);
                builder = builder.at(scopeVar, values[idx]);
            }
            VarAssignment assignment = builder.build();

            double r = random.nextDouble();

            for (int j = 0; j < var.getCardinality(); ++j) {
                double p = f.getValue(assignment.set(var, j));
                r -= p;
                if (r <= 0) {
                    values[v] = j;
                    break;
                }
            }
            checkState(values[v] >= 0);
        }

        VarAssignment.Builder builder = new VarAssignment.Builder();
        for (int i = 0; i < values.length; i++) {
            builder = builder.at(network.getVar(i), values[i]);
        }
        return builder.build();
    }


}
