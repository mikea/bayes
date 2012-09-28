package com.mikea.bayes;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;

/**
 * @author mike.aizatsky@gmail.com
 */
public class VarSet implements Iterable<Integer> {
    private final ProbabilitySpace space;
    private final BitSet variables;

    private VarSet(ProbabilitySpace space, BitSet variables) {
        this.space = space;
        Preconditions.checkArgument(variables.length() <= space.getNumberOfVariables());
        this.variables = variables;
    }

    public static VarSet product(Iterable<VarSet> varSets) {
        Preconditions.checkArgument(!Iterables.isEmpty(varSets));
        ProbabilitySpace space = Iterables.getFirst(varSets, null).space;
        BitSet variables = new BitSet();
        for (VarSet varSet : varSets) {
            variables.or(varSet.variables);
        }

        return new VarSet(space, variables);
    }

    public static VarSet product(VarSet...varSets) {
        return product(Arrays.asList(varSets));
    }

    public static VarSet newVarSet(ProbabilitySpace space, int[] variables) {
        BitSet vars = new BitSet();
        for (int variable : variables) {
            vars.set(variable);
        }
        return new VarSet(space, vars);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        result.append(Joiner.on(", ").join(this));
        result.append("}");
        return result.toString();
    }

    public boolean hasVariable(int var) {
        return variables.get(var);
    }

    public int getMaxIndex() {
        int result = 1;
        for (Integer var : this) {
            result *= space.getCardinality(var);
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
        Integer[] vars = Iterables.toArray(this, Integer.class);

        for (int i = vars.length - 1; i >= 0; --i) {
            int var = vars[i];
            int cardinality = space.getCardinality(var);
            int val = assignment[var];
            Preconditions.checkArgument(val >= 0, "Bad assignment %s@%s for varset %s", val, var, this);
            index *= cardinality;
            index += val;
        }
        return index;
    }

    int[] getAssignment(int idx) {
        Preconditions.checkArgument(idx < getMaxIndex());
        int[] assignment = new int[space.getNumberOfVariables()];
        Arrays.fill(assignment, -1);

        for (Integer var : this) {
            int cardinality = space.getCardinality(var);
            int varValue = idx % cardinality;
            assignment[var] = varValue;
            idx = idx / cardinality;
        }

        return assignment;
    }

    public VarSet removeVars(VarSet otherVars) {
        BitSet variables = new BitSet();
        variables.or(this.variables);
        variables.andNot(otherVars.variables);
        return new VarSet(space, variables);
    }

    public VarSet removeVars(int[] vars) {
        BitSet variables = new BitSet();
        variables.or(this.variables);
        for (int var : vars) {
            variables.clear(var);
        }
        return new VarSet(space, variables);
    }

    @Override
    public Iterator<Integer> iterator() {
        return new AbstractIterator<Integer>() {
            private int idx = -1;
            @Override
            protected Integer computeNext() {
                idx = variables.nextSetBit(idx + 1);
                if (idx < 0) return endOfData();
                return idx;
            }
        };
    }

    public ProbabilitySpace getProbabilitySpace() {
        return space;
    }

    public BitSet getVariables() {
        return variables;
    }
}
