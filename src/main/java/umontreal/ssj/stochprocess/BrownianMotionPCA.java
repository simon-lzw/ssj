/*
 * Class:        BrownianMotionPCA
 * Description:  
 * Environment:  Java
 * Software:     SSJ 
 * Copyright (C) 2001  Pierre L'Ecuyer and Universite de Montreal
 * Organization: DIRO, Universite de Montreal
 * @author       
 * @since

 * SSJ is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License (GPL) as published by the
 * Free Software Foundation, either version 3 of the License, or
 * any later version.

 * SSJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * A copy of the GNU General Public License is available at
   <a href="http://www.gnu.org/licenses">GPL licence site</a>.
 */
package umontreal.ssj.stochprocess;
import umontreal.ssj.rng.*;
import umontreal.ssj.probdist.*;
import umontreal.ssj.randvar.*;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.*;

/**
 * A Brownian motion process @f$\{X(t) : t \geq0 \}@f$ sampled using the
 * *principal component* decomposition (PCA) @cite fGLA04a, @cite fIMA06a,
 * @cite fLEC04a&thinsp;.
 *
 * <div class="SSJ-bigskip"></div><div class="SSJ-bigskip"></div>
 */
public class BrownianMotionPCA extends BrownianMotion {

    protected double[][]  sigmaCov; // Matrice de covariance du vecteur des observ.
                                 // sigmaCov [i][j] = Cov[X(t_{i+1}),X(t_{j+1})].
    protected double[][]  A;     // sigmaCov = AA' (PCA decomposition).
    protected double[]    z;     // vector of standard normals.
    protected double[]    sortedEigenvalues;
    protected boolean     isDecompPCA;

   /**
    * Constructs a new `BrownianMotionBridge` with parameters @f$\mu=
    * \mathtt{mu}@f$, @f$\sigma= \mathtt{sigma}@f$ and initial value
    * @f$X(t_0) = \mathtt{x0}@f$. The normal variates will be generated by
    * inversion using `stream`.
    */
   public BrownianMotionPCA (double x0, double mu, double sigma,
                             RandomStream stream) {
        super (x0, mu, sigma, stream);
        isDecompPCA = false;
    }

   /**
    * Constructs a new `BrownianMotionBridge` with parameters @f$\mu=
    * \mathtt{mu}@f$, @f$\sigma= \mathtt{sigma}@f$ and initial value
    * @f$X(t_0) = \mathtt{x0}@f$. The normal variates will be generated by
    * `gen`.
    */
   public BrownianMotionPCA (double x0, double mu, double sigma,
                             NormalGen gen) {
        super (x0, mu, sigma, gen);
        isDecompPCA = false;
    }


   public double nextObservation() {
        throw new UnsupportedOperationException ("nextObservation() is not defined in BrownianMotionPCA");
    }

   public void setParams (double x0, double mu, double sigma) {
        super.setParams(x0, mu, sigma);
        isDecompPCA = true;  // the setParams method defined in BrownianMotion calls init() which does
                             // the PCA decompostion
    }

   public double[] generatePath() {
       if(!isDecompPCA) {init();}  // if the decomposition is not done, do it...
       for (int j = 0; j < d; j++)
           z[j] = gen.nextDouble ();
       for (int j = 0; j < d; j++) {
           double sum = 0.0;
           for (int k = 0; k < d; k++)
               sum += A[j][k] * z[k];
           path[j+1] = x0 + mu * t[j+1] + sum;
       }
       observationIndex   = d;
       observationCounter = d;
       return path;
    }

   public double[] generatePath (double[] uniform01) {
       if(!isDecompPCA) {init();}  // if the decomposition is not done, do it...
       for (int j = 0; j < d; j++)
           z[j] = NormalDist.inverseF01(uniform01[j]);
       for (int j = 0; j < d; j++) {
           double sum = 0.0;
           for (int k = 0; k < d; k++)
               sum += A[j][k] * z[k];
           path[j+1] = x0 + mu * t[j+1] + sum;
       }
       observationIndex   = d;
       observationCounter = d;
       return path;
    }

   public double[][] decompPCA (double[][] sigma){
      // L'objet SingularValueDecomposition permet de recuperer la matrice
      // des valeurs propres en ordre decroissant et celle des vecteurs propres de
      // sigma (pour une matrice symetrique et definie-positive seulement).
      SingularValueDecomposition sv =
         new SingularValueDecomposition (new DenseDoubleMatrix2D (sigma));
      DoubleMatrix2D D = sv.getS ();    // diagonal
      // Calculer la racine carree des valeurs propres
      for (int i = 0; i < D.rows(); i++){
         sortedEigenvalues[i] = D.getQuick (i, i);
         D.setQuick (i, i, Math.sqrt (D.getQuick (i, i)));
      }
      DoubleMatrix2D P = sv.getV();   // right factor matrix
      return P.zMult (D, null).toArray ();
   }

/**
 * Returns the sorted eigenvalues obtained in the PCA decomposition.
 */
public double[] getSortedEigenvalues() {
        return sortedEigenvalues;
   }


   protected void init() {
        super.init();
        z = new double[d];
        sortedEigenvalues = new double[d];

        // Initialize sigmaCov, based on the observation times.
        sigmaCov = new double[d][d];
        for (int i = 0; i < d; i++) {
           for (int j = i; j < d; j++) {
              sigmaCov[i][j] = sigma * sigma * t[i+1];
              sigmaCov[j][i] = sigmaCov[i][j];
           }
        }
        A = decompPCA (sigmaCov);
        isDecompPCA = true;
    }
}