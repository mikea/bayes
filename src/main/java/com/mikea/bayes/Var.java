package com.mikea.bayes;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * @author mike.aizatsky@gmail.com
 */
public class Var {
    public static final Function<Var, String> TO_STRING = new Function<Var, String>() {
        @Nullable
        @Override
        public String apply(@Nullable Var var) {
            assert var != null;
            return var.toString(true);
        }
    };

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
    private final String[] stateNames;

    public Var(String name, int card) {
        this(name, card, null);
    }

    public Var(String name, int card, @Nullable String[] stateNames) {
        this.name = name;
        this.card = card;
        this.stateNames = stateNames;
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
        else {
            StringBuilder result = new StringBuilder();
            result.append(name);
            result.append("(");
            result.append(card);

            if (stateNames != null) {
                result.append(", ");
                result.append(Arrays.toString(stateNames));
            }

            result.append(")");
            return result.toString();
        }
    }

    /**
     * Protect from override.
     */
    @SuppressWarnings("EmptyMethod")
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

    public String getValue(int value) {
        if (stateNames == null) {
            return String.valueOf(value);
        } else {
            return stateNames[value];
        }
    }

    public int getValueIndex(String value) {
        if (stateNames == null) {
            return Integer.parseInt(value);
        } else {
            for (int i = 0; i < stateNames.length; i++) {
                if (stateNames[i].equals(value)) return i;
            }

            throw new NoSuchElementException();
        }
    }
}
