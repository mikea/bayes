package com.mikea.bayes;

/**
 * @author mike.aizatsky@gmail.com
 */
// todo: introduce builder.
// todo: merge with VarAssigment?
public class Evidence {
    private final Var[] observedVars;
    private final int[] observedValues;

    public Evidence(Var[] observedVars, int[] observedValues) {
        this.observedVars = observedVars;
        this.observedValues = observedValues;
    }

    public Var[] getObservedVars() {
        return observedVars;
    }

    public int[] getObservedValues() {
        return observedValues;
    }
}
