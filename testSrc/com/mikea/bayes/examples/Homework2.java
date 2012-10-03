package com.mikea.bayes.examples;

import com.google.common.base.Preconditions;
import com.mikea.bayes.Factor;
import com.mikea.bayes.Var;
import org.junit.Test;

import static com.mikea.bayes.Var.newVar;
import static org.junit.Assert.assertEquals;

/**
 *
 * Programming assignment 2 for PGM.
 *
 * @author mike.aizatsky@gmail.com
 */
public class Homework2 {
    @Test
    public void testPhenotypeGivenGenotypeMendelianFactor() throws Exception {
        Var genotype = newVar("genotype", 3);
        Var phenotype = newVar("phenotype", 2);

        Factor f = getPhenotypeGivenGenotypeMendelianFactor(true, genotype, phenotype);

        assertEquals("Factor({genotype(3), phenotype(2)}, [0.0, 0.0, 0.0, 0.0, 0.0, 0.0])", f.toString());
    }

    private Factor getPhenotypeGivenGenotypeMendelianFactor(boolean isDominant,
                                                            Var genotypeVar,
                                                            Var phenotypeVar) {
        Preconditions.checkArgument(genotypeVar.getCardinality() == 3);
        Preconditions.checkArgument(phenotypeVar.getCardinality() == 2);

        return Factor.newFactor(new Var[]{genotypeVar, phenotypeVar},
                isDominant ? new double[]{0, 0, 0, 0, 0, 0} : new double[]{0, 0, 0, 0, 0, 0});
    }


}
