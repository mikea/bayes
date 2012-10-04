package com.mikea.bayes;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author mike.aizatsky@gmail.com
 */
public class VarAssignment implements Iterable<Var> {
    // TODO: varset?
    final Var[] vars;
    private final int[] values;

    public VarAssignment(Var[] vars, int[] values) {
        this.vars = vars;
        this.values = values;
    }

    public int get(Var var) {
        for (int i = 0; i < vars.length; i++) {
            if (vars[i] == var) {
                return values[i];
            }
        }
        throw new NoSuchElementException("Var " + var + " not found in " + this);
    }

    @Override
    public Iterator<Var> iterator() {
        return Arrays.asList(vars).iterator();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");

        for (int i = 0; i < vars.length; i++) {
            if (i > 0) result.append(", ");
            Var var = vars[i];
            result.append(var.getName());
            result.append("=");
            result.append(values[i]);
        }

        result.append("}");
        return result.toString();
    }

    public boolean containsAll(Var[] otherVars) {
        nextVar: for (Var otherVar : otherVars) {
            for (Var var : vars) {
                if (var == otherVar) continue nextVar;
            }
            return false;
        }
        return true;
    }

    public static Builder at(Var var, int value) {
        return new Builder().at(var, value);
    }

    public boolean contains(Var var) {
        for (Var v : vars) {
            if (v == var) return true;
        }
        return false;
    }

    public static class Builder {
        private List<Var> vars = newArrayList();
        private List<Integer> values = newArrayList();

        public Builder at(Var var, int value) {
            for (int i = 0; i < vars.size(); i++) {
                Var v =  vars.get(i);
                if (v == var) {
                    values.set(i, value);
                    return this;
                }
            }

            vars.add(var);
            values.add(value);
            return this;
        }

        public VarAssignment build() {
            Var[] v = new Var[vars.size()];
            int[] va = new int[vars.size()];

            for (int i = 0; i < vars.size(); ++i) {
                v[i] = vars.get(i);
                va[i] = values.get(i);
            }

            return new VarAssignment(v, va);
        }
    }
}
