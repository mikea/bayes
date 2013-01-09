package com.mikea.bayes;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;

/**
 * @author mike.aizatsky@gmail.com
 */
public class VarSet implements Iterable<Var> {
    // todo: use ImmutableSortedSet.
    private final Var[] vars;
    private final Set<Var> set;

    private VarSet(Var... vars) {
        this.vars = vars;
        set = ImmutableSet.copyOf(vars);
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

    public VarSet add(Var...vars) {
        return union(this, newVarSet(vars));
    }

    public VarSet add(VarSet varSet) {
        return union(this, varSet);
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

        result.append('}');
        return result.toString();
    }



    public boolean contains(Var var) {
        return set.contains(var);
    }

    public int getCardinality() {
        long result = 1;
        for (Var var : this) {
            result *= var.getCardinality();
        }
        checkState(result < Integer.MAX_VALUE);
        return (int) result;
    }

    /**
     * Transform an index from varSet into our own value index.
     */
    public int transformIndex(int i, VarSet varSet) {
        VarAssignment assignment = varSet.getAssignment(i);
        return getIndex(assignment);
    }

    int getIndex(VarAssignment assignment) {
        Preconditions.checkArgument(assignment.containsAll(vars),
                "Assignment %s do not match this %s", assignment, this);

        int index = 0;
        for (Var var : vars) {
            int cardinality = var.getCardinality();
            int val = assignment.get(var);
            Preconditions.checkArgument(val >= 0, "Bad assignment %s@%s for set %s (full assignment: %s)",
                    val, var, this, assignment);
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
            idx /= cardinality;
        }

        return new VarAssignment(vars, values);
    }

    public VarSet removeVars(VarSet otherVars) {
        return newVarSet(Sets.difference(set, otherVars.set));
    }

    public VarSet removeVars(Var...vars) {
        return newVarSet(Sets.difference(set, newHashSet(vars)));
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        if (obj instanceof VarSet) {
            VarSet other = (VarSet) obj;
            return set.equals(other.set);
        }
        if (obj instanceof Set) {
            return set.equals(obj);
        }

        throw new IllegalArgumentException(obj.getClass().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(set);
    }

    public Iterable<VarAssignment> assignments() {
        VarAssignment[] assignments = new VarAssignment[(int) getCardinality()];
        for (int i = 0; i < assignments.length; i++) {
            assignments[i] = getAssignment(i);
        }
        return Arrays.asList(assignments);
    }

    public int size() {
        return vars.length;
    }

    public Set<Var> getVarSet() {
        return Collections.unmodifiableSet(set);
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public VarSet intersect(VarSet varSet) {
        return intersect(this, varSet);
    }

    public static VarSet intersect(VarSet... varSets) {
        Set<Var> vars = varSets[0].getVarSet();

        for (int i = 1; i < varSets.length; i++) {
            VarSet varSet = varSets[i];
            vars = Sets.intersection(vars, varSet.getVarSet());
        }
        return newVarSet(vars);
    }

    public static VarSet intersect(Iterable<VarSet> varSets) {
        return intersect(Iterables.toArray(varSets, VarSet.class));
    }

    public boolean containsAll(VarSet other) {
        return set.containsAll(other.set);
    }
}
