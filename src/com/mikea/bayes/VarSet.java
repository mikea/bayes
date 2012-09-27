package com.mikea.bayes;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;

/**
 * @author mike.aizatsky@gmail.com
 */
public class VarSet {
    private final int[] variables;
    private final int[] cardinalities;

    public VarSet(int[] variables, int[] cardinalities) {
        Preconditions.checkArgument(variables.length == cardinalities.length);
        this.variables = variables;
        this.cardinalities = cardinalities;
    }

    public static VarSet product(Iterable<VarSet> varSets) {
        BitSet variables = new BitSet();
        for (VarSet varSet : varSets) {
            for (int v : varSet.variables) {
                variables.set(v);
            }
        }

        return createFromBitset(variables, varSets);
    }

    private static VarSet createFromBitset(BitSet variables, Iterable<VarSet> cardinalitySets) {
        int[] vars = new int[variables.cardinality()];
        for (int i = 0, var = variables.nextSetBit(0); i < vars.length; ++i, var = variables.nextSetBit(var + 1)) {
            vars[i] = var;
        }

        int[] cardinalities = new int[variables.cardinality()];
        Arrays.fill(cardinalities, -1);
        for (int i = 0; i < cardinalities.length; ++i) {
            int var = vars[i];
            int cardinality = -1;

            for (VarSet varSet : cardinalitySets) {
                if (varSet.hasVariable(var)) {
                    if (cardinality == -1) cardinality = varSet.getCardinality(var);
                    else Preconditions.checkState(cardinality == varSet.getCardinality(var));
                }
            }

            Preconditions.checkState(cardinality > 0);
            cardinalities[i] = cardinality;
        }

        return new VarSet(vars, cardinalities);
    }

    public static VarSet product(VarSet...varSets) {
        return product(Arrays.asList(varSets));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        for (int i = 0; i < variables.length; i++) {
            if (i > 0) result.append(", ");
            result.append(variables[i]);
            result.append("(");
            result.append(cardinalities[i]);
            result.append(")");
        }

        result.append("}");
        return result.toString();
    }

    public int getCardinality(int var) {
        for (int i = 0; i < variables.length; i++) {
            if (variables[i] == var) return cardinalities[i];
        }

        throw new IllegalArgumentException("VarSet " + this + " doesn't contain variable " + var);
    }

    public boolean hasVariable(int var) {
        for (int variable : variables) {
            if (variable == var) return true;
        }

        return false;
    }

    public int getMaxIndex() {
        int result = 1;
        for (int cardinality : cardinalities) {
            result *= cardinality;
        }
        return result;
    }

    /**
     * Transform an index from varSet into our own value index.
     */
    public int transformIndex(int i, VarSet varSet) {
        int[] assignment = varSet.getAssignment(i);
        return getIndex(assignment);
    }

    int getIndex(int[] assignment) {
        int index = 0;
        for (int i = cardinalities.length - 1; i >= 0; --i) {
            int cardinality = cardinalities[i];
            int var = variables[i];
            index *= cardinality;
            index += assignment[var];
        }
        return index;
    }

    int[] getAssignment(int idx) {
        int[] assignment = new int[getMaxVar() + 1];
        Arrays.fill(assignment, -1);

        for (int i = 0; i < cardinalities.length; i++) {
            int cardinality = cardinalities[i];
            int var = variables[i];
            int varValue = idx % cardinality;
            assignment[var] = varValue;
            idx = idx / cardinality;
        }

        return assignment;
    }

    private int getMaxVar() {
        int result = variables[0];

        for (int i = 1; i < variables.length; i++) {
            result = Math.max(result, variables[i]);
        }

        return result;
    }

    public VarSet removeVars(VarSet otherVars) {
        BitSet variables = new BitSet();
        for (int v : this.variables) {
            variables.set(v);
        }
        for (int v : otherVars.variables) {
            variables.clear(v);
        }

        return createFromBitset(variables, Collections.singletonList(this));
    }
}
