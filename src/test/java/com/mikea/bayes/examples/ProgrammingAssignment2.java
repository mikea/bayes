package com.mikea.bayes.examples;

import com.mikea.bayes.Factor;
import com.mikea.bayes.Var;
import com.mikea.bayes.VarAssignment;
import org.junit.Test;

import static com.google.common.base.Preconditions.checkArgument;
import static com.mikea.bayes.Utils.strings;
import static com.mikea.bayes.VarAssignment.at;
import static org.junit.Assert.assertEquals;

/**
 *
 * Programming assignment 2 for PGM.
 *
 * @author mike.aizatsky@gmail.com
 */
public class ProgrammingAssignment2 {
    @Test
    public void testPhenotypeGivenGenotypeMendelianFactor() throws Exception {
        // 0 - FF
        // 1 - Ff
        // 2 - ff
        Var genotype = new Var("genotype", 3, strings("FF", "Ff", "ff"));
        Var phenotype = new Var("phenotype", 2, strings("F", "T"));

        assertEquals(
                "Factor({phenotype(2, [F, T]), genotype(3, [FF, Ff, ff])}):\n" +
                        "{genotype=FF}: 0.0 1.0\n" +
                        "{genotype=Ff}: 0.0 1.0\n" +
                        "{genotype=ff}: 1.0 0.0\n",
                getPhenotypeGivenGenotypeMendelianFactor(true, genotype, phenotype).toStringAsTable(phenotype, "%.1f"));

        assertEquals(
                "Factor({phenotype(2, [F, T]), genotype(3, [FF, Ff, ff])}):\n" +
                        "{genotype=FF}: 1.0 0.0\n" +
                        "{genotype=Ff}: 1.0 0.0\n" +
                        "{genotype=ff}: 0.0 1.0\n",
                getPhenotypeGivenGenotypeMendelianFactor(false, genotype, phenotype).toStringAsTable(phenotype, "%.1f"));
    }

    @Test
    public void testPhenotypeGivenGenotypeFactor() throws Exception {
        Var genotype = new Var("genotype", 3);
        Var phenotype = new Var("phenotype", 2);

        assertEquals("Factor({phenotype(2), genotype(3)}):\n" +
                "{genotype=0}: 0.2 0.8\n" +
                "{genotype=1}: 0.4 0.6\n" +
                "{genotype=2}: 0.9 0.1\n",
                getPhenotypeGivenGenotypeFactor(genotype, phenotype, new double[] {0.8, 0.6, 0.1}).toStringAsTable(phenotype, "%.1f"));
    }

    @Test
    public void testGenotypeGivenAlleleFreqsFactor() throws Exception {
        Var genotype = new Var("genotype", 3);
        assertEquals("Factor({genotype(3)}, [0.010000000000000002, 0.18000000000000002, 0.81])",
                getGenotypeGivenAlleleFreqsFactor(genotype, new double[]{0.1, 0.9}).toString());
    }

    @Test
    public void testGenotypeGivenParentsGenotypesFactor() throws Exception {
        Var genotypeParent1 = new Var("p1", 3);
        Var genotypeParent2 = new Var("p2", 3);
        Var genotypeChild = new Var("c", 3);

        assertEquals(
                "Factor({c(3), p1(3), p2(3)}):\n" +
                        "{p1=0, p2=0}: 1.00 0.00 0.00\n" +
                        "{p1=0, p2=1}: 0.50 0.50 0.00\n" +
                        "{p1=0, p2=2}: 0.00 1.00 0.00\n" +
                        "{p1=1, p2=0}: 0.50 0.50 0.00\n" +
                        "{p1=1, p2=1}: 0.25 0.50 0.25\n" +
                        "{p1=1, p2=2}: 0.00 0.50 0.50\n" +
                        "{p1=2, p2=0}: 0.00 1.00 0.00\n" +
                        "{p1=2, p2=1}: 0.00 0.50 0.50\n" +
                        "{p1=2, p2=2}: 0.00 0.00 1.00\n",
                getGenotypeGivenParentsGenotypesFactor(genotypeChild, genotypeParent1, genotypeParent2, 2).toStringAsTable(genotypeChild, "%1.2f"));
    }

    @Test
    public void testGenotypeAllelesMap() throws Exception {
        int numAlleles = 5;
        int numGenotypes = getNumGenotypes(numAlleles);
        int[][] allelesToGenotypeMap = getAllelesToGenotypeMap(numAlleles);
        int[][] genotypeToAllelesMap = getGenotypeToAllelesMap(numAlleles);


        for (int i = 0; i < numGenotypes; ++i) {
            assertEquals(i, allelesToGenotypeMap[genotypeToAllelesMap[i][0]][genotypeToAllelesMap[i][1]]);
        }

        for (int i = 0; i < numAlleles; ++i) {
            for (int j = i; j < numAlleles; ++j) {
                int idx = allelesToGenotypeMap[i][j];
                assertEquals(genotypeToAllelesMap[idx][0], i);
                assertEquals(genotypeToAllelesMap[idx][1], j);
            }
        }

    }

    private Factor getGenotypeGivenParentsGenotypesFactor(Var genotypeChild, Var genotypeParent1, Var genotypeParent2, int numAlleles) {
        int numGenotypes = getNumGenotypes(numAlleles);

        checkArgument(genotypeChild.getCardinality() == numGenotypes);
        checkArgument(genotypeParent1.getCardinality() == numGenotypes);
        checkArgument(genotypeParent2.getCardinality() == numGenotypes);
        checkArgument(genotypeChild != genotypeParent1);
        checkArgument(genotypeChild != genotypeParent2);
        checkArgument(genotypeParent1 != genotypeParent2);

        Factor.Builder builder = Factor.withVariables(genotypeChild, genotypeParent1, genotypeParent2);


        int[][] allelesToGenotypeMap = getAllelesToGenotypeMap(numAlleles);
        int[][] genotypeToAllelesMap = getGenotypeToAllelesMap(numAlleles);


        for (int genotype1 = 0; genotype1 < numGenotypes; ++genotype1) {
            int allele11 = genotypeToAllelesMap[genotype1][0];
            int allele12 = genotypeToAllelesMap[genotype1][1];

            for (int genotype2 = 0; genotype2 < numGenotypes; ++genotype2) {
                int allele21 = genotypeToAllelesMap[genotype2][0];
                int allele22 = genotypeToAllelesMap[genotype2][1];

                int childGenotype1 = allelesToGenotypeMap[allele11][allele21];
                int childGenotype2 = allelesToGenotypeMap[allele12][allele21];
                int childGenotype3 = allelesToGenotypeMap[allele11][allele22];
                int childGenotype4 = allelesToGenotypeMap[allele12][allele22];

                builder = builder.add(
                        at(genotypeParent1, genotype1).at(genotypeParent2, genotype2).at(genotypeChild, childGenotype1),
                        1
                );
                builder = builder.add(
                        at(genotypeParent1, genotype1).at(genotypeParent2, genotype2).at(genotypeChild, childGenotype2),
                        1
                );
                builder = builder.add(
                        at(genotypeParent1, genotype1).at(genotypeParent2, genotype2).at(genotypeChild, childGenotype3),
                        1
                );
                builder = builder.add(
                        at(genotypeParent1, genotype1).at(genotypeParent2, genotype2).at(genotypeChild, childGenotype4),
                        1
                );
            }
        }

        return builder.build().normalizeBy(genotypeParent1, genotypeParent2);
    }

    private int getNumGenotypes(int numAlleles) {
        return numAlleles * (numAlleles - 1) / 2 + numAlleles;
    }

    private Factor getGenotypeGivenAlleleFreqsFactor(Var genotype, double[] alleleFreqs) {
        int numAlleles = alleleFreqs.length;
        int numGenotypes = getNumGenotypes(numAlleles);

        int[][] allelesToGenotypeMap = getAllelesToGenotypeMap(numAlleles);

        double[] values = new double[numGenotypes];
        for (int i = 0; i < numAlleles; ++i) {
            for (int j = i; j < numAlleles; ++j) {
                values[allelesToGenotypeMap[i][j]] = alleleFreqs[i] * alleleFreqs[j] * (i == j ? 1 : 2);
            }
        }

        return Factor
                .withVariables(genotype)
                .row(genotype, new VarAssignment.Builder(), values)  // todo: this new VarAssignment.Builder() doesn't look good.
                .build();
    }

    private int[][] getAllelesToGenotypeMap(int numAlleles) {
        int[][] map = new int[numAlleles][];

        for (int i = 0; i < numAlleles; ++i) {
            map[i] = new int[numAlleles];
        }

        for (int idx = 0, i = 0; i < numAlleles; ++i) {
            for (int j = i; j < numAlleles; ++j, ++idx) {
                map[i][j] = idx;
                map[j][i] = idx;
            }
        }

        return map;
    }

    private int[][] getGenotypeToAllelesMap(int numAlleles) {
        int numGenotypes = getNumGenotypes(numAlleles);
        int[][] map = new int[numGenotypes][];

        for (int idx = 0, i = 0; i < numAlleles; ++i) {
            for (int j = i; j < numAlleles; ++j, ++idx) {
                map[idx] = new int[2];
                map[idx][0] = i;
                map[idx][1] = j;
            }
        }

        return map;
    }

    private Factor getPhenotypeGivenGenotypeFactor(Var genotype, Var phenotype, double[] alphas) {
        checkArgument(phenotype.getCardinality() == 2);
        checkArgument(alphas.length == genotype.getCardinality());

        double[] alphasCompliments = new double[alphas.length];
        for (int i = 0; i < alphas.length; i++) {
            alphasCompliments[i] = 1 - alphas[i];
        }

        return Factor
                .withVariables(phenotype, genotype)
                .row(genotype, at(phenotype, 0), alphasCompliments)
                .row(genotype, at(phenotype, 1), alphas)
                .build();
    }

    private Factor getPhenotypeGivenGenotypeMendelianFactor(boolean isDominant,
                                                            Var genotype,
                                                            Var phenotype) {
        checkArgument(genotype.getCardinality() == 3);
        checkArgument(phenotype.getCardinality() == 2);

        if (isDominant) {
            return Factor
                    .withVariables(phenotype, genotype)
                    .row(phenotype, at(genotype, "FF"), strings("T", "F"), new double[]{1, 0})
                    .row(phenotype, at(genotype, "Ff"), strings("T", "F"), new double[]{1, 0})
                    .row(phenotype, at(genotype, "ff"), strings("T", "F"), new double[]{0, 1})
                    .build();
        } else {
            return Factor
                    .withVariables(phenotype, genotype)
                    .row(phenotype, at(genotype, "FF"), strings("T", "F"), new double[]{0, 1})
                    .row(phenotype, at(genotype, "Ff"), strings("T", "F"), new double[]{0, 1})
                    .row(phenotype, at(genotype, "ff"), strings("T", "F"), new double[]{1, 0})
                    .build();
        }
    }

}
