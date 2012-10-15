package com.mikea.bayes;

import com.google.common.base.Objects;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

/**
 * @author mike.aizatsky@gmail.com
 */
public class ProbabilitySpace {
    private final String name;
    private int nextVarIndex = 0;
    private boolean mutable = true;
    private final List<Var> vars = newArrayList();

    public ProbabilitySpace() {
        this("Anonymous");
    }

    public ProbabilitySpace(String name) {
        this.name = name;
    }

    public Var newVar(String name, int card) {
        return newVar(name, card, null);
    }

    public synchronized Var newVar(String name, int card, @Nullable String[] stateNames) {
        checkState(mutable);
        int idx = nextVarIndex;
        nextVarIndex++;
        Var var = new Var(this, idx, name, card, stateNames);
        vars.add(var);
        checkState(vars.size() == nextVarIndex);
        return var;
    }

    public static ProbabilitySpace get(Iterable<Var> vars) {
        ProbabilitySpace space = null;

        for (Var var : vars) {
            if (space == null) {
                space = var.getProbabilitySpace();
            } else {
                checkArgument(space == var.getProbabilitySpace());
            }
        }

        return checkNotNull(space);
    }

    public int getNumVars() {
        mutable = false;
        return nextVarIndex;
    }

    public Var getVar(int index) {
        return vars.get(index);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("name", name).toString();
    }
}
