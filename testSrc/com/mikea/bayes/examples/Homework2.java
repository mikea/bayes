package com.mikea.bayes.examples;

import com.google.common.base.Preconditions;
import com.mikea.bayes.Factor;
import com.mikea.bayes.Var;
import org.junit.Test;

import static com.mikea.bayes.Var.newVar;
import static com.mikea.bayes.VarAssignment.at;
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

        assertEquals("Factor({phenotype(2), genotype(3)}, [0.0, 1.0, 0.0, 1.0, 1.0, 0.0])",
                getPhenotypeGivenGenotypeMendelianFactor(true, genotype, phenotype).toString());

        assertEquals("Factor({phenotype(2), genotype(3)}, [1.0, 0.0, 1.0, 0.0, 0.0, 1.0])",
                getPhenotypeGivenGenotypeMendelianFactor(false, genotype, phenotype).toString());
    }

    private Factor getPhenotypeGivenGenotypeMendelianFactor(boolean isDominant,
                                                            Var genotypeVar,
                                                            Var phenotypeVar) {
        Preconditions.checkArgument(genotypeVar.getCardinality() == 3);
        Preconditions.checkArgument(phenotypeVar.getCardinality() == 2);

        // 0 = AA
        // 1 = Aa
        // 2 = aa
        if (isDominant) {
            return Factor
                    .withVariables(phenotypeVar, genotypeVar)
                    .line(phenotypeVar, at(genotypeVar, 0), new double[]{0, 1})
                    .line(phenotypeVar, at(genotypeVar, 1), new double[]{0, 1})
                    .line(phenotypeVar, at(genotypeVar, 2), new double[]{1, 0})
                    .build();
        } else {
            return Factor
                    .withVariables(phenotypeVar, genotypeVar)
                    .line(phenotypeVar, at(genotypeVar, 0), new double[]{1, 0})
                    .line(phenotypeVar, at(genotypeVar, 1), new double[]{1, 0})
                    .line(phenotypeVar, at(genotypeVar, 2), new double[]{0, 1})
                    .build();
        }
    }


}
