package com.mikea.bayes.examples

import org.junit.Test
import org.junit.Assert.assertEquals
import com.mikea.bayes.{VarAssignment, Factor, ProbabilitySpace, Var}
import com.mikea.bayes.VarAssignment.at
import com.mikea.bayes.Factor.Builder

/**
 *
 * Programming assignment 2 for PGM.
 *
 * @author mike.aizatsky@gmail.com
 */
class ProgrammingAssignment2 {
  @Test def testPhenotypeGivenGenotypeMendelianFactor() {
    val space: ProbabilitySpace = new ProbabilitySpace
    val genotype: Var = space.newVar("genotype", 3, List("FF", "Ff", "ff"))
    val phenotype: Var = space.newVar("phenotype", 2, List("F", "T"))
    assertEquals("Factor({phenotype, genotype}):\n" + "{genotype=FF}: 0.0 1.0\n" + "{genotype=Ff}: 0.0 1.0\n" + "{genotype=ff}: 1.0 0.0\n", getPhenotypeGivenGenotypeMendelianFactor(isDominant = true, genotype, phenotype).toStringAsTable(phenotype, "%.1f"))
    assertEquals("Factor({phenotype, genotype}):\n" + "{genotype=FF}: 1.0 0.0\n" + "{genotype=Ff}: 1.0 0.0\n" + "{genotype=ff}: 0.0 1.0\n", getPhenotypeGivenGenotypeMendelianFactor(isDominant = false, genotype, phenotype).toStringAsTable(phenotype, "%.1f"))
  }

  @Test def testPhenotypeGivenGenotypeFactor() {
    val space: ProbabilitySpace = new ProbabilitySpace
    val genotype: Var = space.newVar("genotype", 3)
    val phenotype: Var = space.newVar("phenotype", 2)
    assertEquals("Factor({phenotype, genotype}):\n" + "{genotype=0}: 0.2 0.8\n" + "{genotype=1}: 0.4 0.6\n" + "{genotype=2}: 0.9 0.1\n", getPhenotypeGivenGenotypeFactor(genotype, phenotype, Array[Double](0.8, 0.6, 0.1)).toStringAsTable(phenotype, "%.1f"))
  }

  @Test def testGenotypeGivenAlleleFreqsFactor() {
    val space: ProbabilitySpace = new ProbabilitySpace
    val genotype: Var = space.newVar("genotype", 3)
    assertEquals("Factor({genotype}, [0.010000000000000002, 0.18000000000000002, 0.81])", getGenotypeGivenAlleleFreqsFactor(genotype, Array[Double](0.1, 0.9)).toString)
  }

  @Test def testGenotypeGivenParentsGenotypesFactor() {
    val space: ProbabilitySpace = new ProbabilitySpace
    val genotypeParent1: Var = space.newVar("p1", 3)
    val genotypeParent2: Var = space.newVar("p2", 3)
    val genotypeChild: Var = space.newVar("c", 3)
    assertEquals("Factor({c, p1, p2}):\n" + "{p1=0, p2=0}: 1.00 0.00 0.00\n" + "{p1=0, p2=1}: 0.50 0.50 0.00\n" + "{p1=0, p2=2}: 0.00 1.00 0.00\n" + "{p1=1, p2=0}: 0.50 0.50 0.00\n" + "{p1=1, p2=1}: 0.25 0.50 0.25\n" + "{p1=1, p2=2}: 0.00 0.50 0.50\n" + "{p1=2, p2=0}: 0.00 1.00 0.00\n" + "{p1=2, p2=1}: 0.00 0.50 0.50\n" + "{p1=2, p2=2}: 0.00 0.00 1.00\n", getGenotypeGivenParentsGenotypesFactor(genotypeChild, genotypeParent1, genotypeParent2, 2).toStringAsTable(genotypeChild, "%1.2f"))
  }

  @Test def testGenotypeAllelesMap() {
    val numAlleles: Int = 5
    val numGenotypes: Int = getNumGenotypes(numAlleles)
    val allelesToGenotypeMap: Array[Array[Int]] = getAllelesToGenotypeMap(numAlleles)
    val genotypeToAllelesMap: Array[Array[Int]] = getGenotypeToAllelesMap(numAlleles)
    for (i <- 0 until numGenotypes) {
      assertEquals(i, allelesToGenotypeMap(genotypeToAllelesMap(i)(0))(genotypeToAllelesMap(i)(1)))
    }
    for (i <- 0 until numAlleles) {
      for (j <- i until numAlleles) {
        val idx: Int = allelesToGenotypeMap(i)(j)
        assertEquals(genotypeToAllelesMap(idx)(0), i)
        assertEquals(genotypeToAllelesMap(idx)(1), j)
      }
    }
  }

  private def getGenotypeGivenParentsGenotypesFactor(genotypeChild: Var, genotypeParent1: Var, genotypeParent2: Var, numAlleles: Int): Factor = {
    val numGenotypes: Int = getNumGenotypes(numAlleles)
    assert(genotypeChild.cardinality == numGenotypes)
    assert(genotypeParent1.cardinality == numGenotypes)
    assert(genotypeParent2.cardinality == numGenotypes)
    assert(genotypeChild ne genotypeParent1)
    assert(genotypeChild ne genotypeParent2)
    assert(genotypeParent1 ne genotypeParent2)
    var builder: Builder = Factor.withVariables(genotypeChild, genotypeParent1, genotypeParent2)
    val allelesToGenotypeMap: Array[Array[Int]] = getAllelesToGenotypeMap(numAlleles)
    val genotypeToAllelesMap: Array[Array[Int]] = getGenotypeToAllelesMap(numAlleles)
    for (genotype1 <- 0 until numGenotypes) {
      val allele11: Int = genotypeToAllelesMap(genotype1)(0)
      val allele12: Int = genotypeToAllelesMap(genotype1)(1)
      for (genotype2 <- 0 until numGenotypes) {
        val allele21: Int = genotypeToAllelesMap(genotype2)(0)
        val allele22: Int = genotypeToAllelesMap(genotype2)(1)
        val childGenotype1: Int = allelesToGenotypeMap(allele11)(allele21)
        val childGenotype2: Int = allelesToGenotypeMap(allele12)(allele21)
        val childGenotype3: Int = allelesToGenotypeMap(allele11)(allele22)
        val childGenotype4: Int = allelesToGenotypeMap(allele12)(allele22)
        builder = builder.add(at(genotypeParent1, genotype1).at(genotypeParent2, genotype2).at(genotypeChild, childGenotype1), 1)
        builder = builder.add(at(genotypeParent1, genotype1).at(genotypeParent2, genotype2).at(genotypeChild, childGenotype2), 1)
        builder = builder.add(at(genotypeParent1, genotype1).at(genotypeParent2, genotype2).at(genotypeChild, childGenotype3), 1)
        builder = builder.add(at(genotypeParent1, genotype1).at(genotypeParent2, genotype2).at(genotypeChild, childGenotype4), 1)
      }
    }

    builder.build().normalizeBy(genotypeParent1, genotypeParent2)
  }

  private def getNumGenotypes(numAlleles: Int): Int = { numAlleles * (numAlleles - 1) / 2 + numAlleles }

  private def getGenotypeGivenAlleleFreqsFactor(genotype: Var, alleleFreqs: Array[Double]): Factor = {
    val numAlleles: Int = alleleFreqs.length
    val numGenotypes: Int = getNumGenotypes(numAlleles)
    val allelesToGenotypeMap: Array[Array[Int]] = getAllelesToGenotypeMap(numAlleles)
    val values: Array[Double] = new Array[Double](numGenotypes)
    for (i <- 0 until numAlleles) {
      for (j <- 0 until numAlleles) {
        values(allelesToGenotypeMap(i)(j)) = alleleFreqs(i) * alleleFreqs(j) * (if (i == j) 1 else 2)
      }
    }
    Factor.withVariables(genotype).row(genotype, new VarAssignment.Builder(), values).build()
  }

  private def getAllelesToGenotypeMap(numAlleles: Int): Array[Array[Int]] = {
    val map: Array[Array[Int]] = new Array[Array[Int]](numAlleles)
    for (i <- 0 until numAlleles) {
      map(i) = new Array[Int](numAlleles)
    }

    var idx: Int = 0

    for (i <- 0 until numAlleles) {
      for (j <- i until numAlleles) {
        map(i)(j) = idx
        map(j)(i) = idx
        idx += 1
      }
    }

    map
  }

  private def getGenotypeToAllelesMap(numAlleles: Int): Array[Array[Int]] = {
    val numGenotypes: Int = getNumGenotypes(numAlleles)
    val map: Array[Array[Int]] = new Array[Array[Int]](numGenotypes)
    var idx: Int = 0

    for (i <- 0 until numAlleles) {
      for (j <- i until numAlleles) {
                map(idx) = new Array[Int](2)
                map(idx)(0) = i
                map(idx)(1) = j
        idx += 1
      }
    }

    map
  }

  private def getPhenotypeGivenGenotypeFactor(genotype: Var, phenotype: Var, alphas: Array[Double]): Factor = {
    assert(phenotype.cardinality == 2)
    assert(alphas.length == genotype.cardinality)
    val alphasCompliments: Array[Double] = new Array[Double](alphas.length)

    for (i <- 0 until alphas.length) {
          alphasCompliments(i) = 1 - alphas(i)
    }

    Factor.withVariables(phenotype, genotype).row(genotype, at(phenotype, 0), alphasCompliments).row(genotype, at(phenotype, 1), alphas).build()
  }

  private def getPhenotypeGivenGenotypeMendelianFactor(isDominant: Boolean, genotype: Var, phenotype: Var): Factor = {
    assert(genotype.cardinality == 3)
    assert(phenotype.cardinality == 2)
    if (isDominant) {
      Factor.withVariables(phenotype, genotype)
        .row(phenotype, at(genotype, "FF"), List("T", "F"), Array[Double](1, 0))
        .row(phenotype, at(genotype, "Ff"), List("T", "F"), Array[Double](1, 0))
        .row(phenotype, at(genotype, "ff"), List("T", "F"), Array[Double](0, 1))
        .build()
    }
    else {
      Factor.withVariables(phenotype, genotype)
        .row(phenotype, at(genotype, "FF"), List("T", "F"), Array[Double](0, 1))
        .row(phenotype, at(genotype, "Ff"), List("T", "F"), Array[Double](0, 1))
        .row(phenotype, at(genotype, "ff"), List("T", "F"), Array[Double](1, 0))
        .build()
    }
  }
}