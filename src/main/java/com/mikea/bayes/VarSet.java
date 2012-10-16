package com.mikea.bayes;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public class VarSet implements Iterable<Var> {
    // todo: use ImmutableSortedSet.
    private final Var[] vars;
    private final Set<Var> set;

    private VarSet(Var[] vars) {
        this.vars = vars;
        this.set = ImmutableSet.copyOf(vars);
    }

    public static VarSet union(Iterable<VarSet> varSets) {
        Preconditions.checkArgument(!Iterables.isEmpty(varSets));
        Set<Var> variables = newHashSet();
        for (VarSet varSet : varSets) {
            variables.addAll(varSet.set);
        }

        return newVarSet(variables);
    }

    public static VarSet union(VarSet... varSets) {
        return union(Arrays.asList(varSets));
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean showCardinalities) {
        StringBuilder result = new StringBuilder("{");

        for (int i = 0; i < vars.length; i++) {
            if (i > 0) result.append(", ");
            Var var = vars[i];
            result.append(var.toString(showCardinalities));
        }

        result.append("}");
        return result.toString();
    }



    public boolean contains(Var var) {
        return set.contains(var);
    }

    public int getCardinality() {
        int result = 1;
        for (Var var : this) {
            result *= var.getCardinality();
        }
        return result;
    }

    /**
     * Transform an index from varSet into our own value index.
     */
    public int transformIndex(int i, VarSet varSet) {
        VarAssignment assignment = varSet.getAssignment(i);
        return getIndex(assignment);
    }

    int getIndex(VarAssignment assignment) {
        int index = 0;

        Preconditions.checkArgument(assignment.containsAll(this.vars),
                "Assignment %s do not match this %s", assignment, this);

        for (Var var : vars) {
            int cardinality = var.getCardinality();
            int val = assignment.get(var);
            Preconditions.checkArgument(val >= 0, "Bad assignment %s@%s for set %s (full assignment: %s)", val, var, this, assignment);
            index *= cardinality;
            index += val;
        }
        return index;
    }

    VarAssignment getAssignment(int idx) {
        Preconditions.checkArgument(idx < getCardinality());
        int[] values = new int[vars.length];
        Arrays.fill(values, -1);

        for (int i = vars.length - 1; i >= 0; --i) {
            Var var = vars[i];
            int cardinality = var.getCardinality();
            int varValue = idx % cardinality;
            values[i] = varValue;
            idx = idx / cardinality;
        }

        return new VarAssignment(vars, values);
    }

    public VarSet removeVars(VarSet otherVars) {
        return newVarSet(Sets.difference(set, otherVars.set));
    }

    public VarSet removeVars(Var...vars) {
        return newVarSet(Sets.difference(set, Sets.newHashSet(vars)));
    }

    @Override
    public Iterator<Var> iterator() {
        return Arrays.asList(vars).iterator();
    }

    public static VarSet newVarSet(Var...vars) {
        return new VarSet(vars);
    }

    public static VarSet newVarSet(Iterable<Var> vars) {
        return new VarSet(Iterables.toArray(vars, Var.class));
    }

    public static VarSet newVarSet(Set<Var> vars) {
        Var[] varArray = vars.toArray(new Var[vars.size()]);
        Arrays.sort(varArray, Var.BY_NAME);
        return new VarSet(varArray);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof VarSet) {
            VarSet vars = (VarSet) o;
            return set.equals(vars.set);
        }
        if (o instanceof Set) {
            return set.equals(o);
        }

        throw new IllegalArgumentException(o.getClass().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(set);
    }

    public Iterable<VarAssignment> assignments() {
        VarAssignment[] assignments = new VarAssignment[getCardinality()];
        for (int i = 0; i < assignments.length; i++) {
            assignments[i] = getAssignment(i);
        }
        return Arrays.asList(assignments);
    }

    public int size() {
        return vars.length;
    }

    public Set<Var> getVarSet() {
        return set;
    }
}
