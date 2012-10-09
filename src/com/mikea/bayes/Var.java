package com.mikea.bayes;

import com.google.common.collect.Ordering;

import javax.annotation.Nullable;

/**
 * @author mike.aizatsky@gmail.com
 */
public class Var {
    public static final Ordering<Var> BY_NAME = new Ordering<Var>() {
        @Override
        public int compare(@Nullable Var left, @Nullable Var right) {
            assert left != null;
            assert right != null;
            return left.getName().compareTo(right.getName());
        }
    };

    private final String name;
    private final int card;

    public Var(String name, int card) {
        this.name = name;
        this.card = card;
    }

    public int getCardinality() {
        return card;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean showCardinality) {
        if (!showCardinality) return name;
        else return name + "(" + card + ")";
    }

    /**
     * Protect from override.
     */
    @Override
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    public String getName() {
        return name;
    }

    public static Var[] vars(Var...vv) {
        return vv;
    }
}
