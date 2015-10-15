/*
 * Class:        GammaProcessPCA
 * Description:
 * Environment:  Java
 * Software:     SSJ
 * Copyright (C) 2001  Pierre L'Ecuyer and Universite de Montreal
 * Organization: DIRO, Universite de Montreal
 * @authors      Jean-Sebastien Parent and Maxime Dion
 * @since        july 2008

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
import umontreal.ssj.stat.*;
import umontreal.ssj.stat.list.*;

/**
 * Represents a *gamma* process sampled using the principal component
 * analysis (PCA). To simulate the gamma process at times @f$t_0 < t_1 <
 * \cdots< t_d@f$ by PCA sampling, a Brownian motion @f$\{ W(t), t \geq0
 * \}@f$ with mean @f$0@f$ and variance parameter @f$\nu@f$ is first
 * generated at times @f$t_0 < t_1 < \cdots< t_d@f$ by PCA sampling (see
 * class  @ref BrownianMotionPCA ). The independent increments @f$W(t_j) -
 * W(t_{j-1})@f$ of this process are then transformed into independent
 * @f$U(0, 1)@f$ random variates @f$V_j@f$ via
 * @f[
 *   V_j = \Phi\left(\sqrt{\tau_j-\tau_{j-1}} [W(\tau_j)-W(\tau_{j-1})]\right), \quad j=1,…,s
 * @f]
 * Finally, the increments of the Gamma process are computed as @f$ Y(t_j) -
 * Y(t_{j-1}) = G^{-1}(V_j)@f$, where @f$G@f$ is the gamma distribution
 * function.
 *
 * <div class="SSJ-bigskip"></div><div class="SSJ-bigskip"></div>
 */
public class GammaProcessPCA extends GammaProcess {
    double[] arrayTime;
    BrownianMotionPCA BMPCA;

   /**
    * Constructs a new `GammaProcessPCA` with parameters @f$\mu=
    * \mathtt{mu}@f$, @f$\nu= \mathtt{nu}@f$ and initial value @f$S(t_0)
    * = \mathtt{s0}@f$. The random variables are created using `stream`.
    * Note that the same  @ref umontreal.ssj.rng.RandomStream is used for
    * the `GammaProcessPCA` and for the  @ref BrownianMotionPCA included
    * in this class. Both the  @ref GammaProcessPCA and the
    * @ref BrownianMotionPCA are generated by inversion.
    */
   public GammaProcessPCA (double s0, double mu, double nu,
                           RandomStream stream) {
        super (s0, mu, nu,  new GammaGen (stream, new GammaDist (1.0)));
        this.BMPCA = new BrownianMotionPCA(0.0, 0.0, Math.sqrt(nu), stream);
    }

   /**
    * Constructs a new `GammaProcessPCA` with parameters @f$\mu=
    * \mathtt{mu}@f$, @f$\nu= \mathtt{nu}@f$ and initial value @f$S(t_0)
    * = \mathtt{s0}@f$. All the random variables, i.e. the gamma ones and
    * the normal ones, are created using the
    * @ref umontreal.ssj.rng.RandomStream included in the
    * @ref umontreal.ssj.randvar.GammaGen `Ggen`. Note that the parameters
    * of the  @ref umontreal.ssj.randvar.GammaGen object are not important
    * since the implementation forces the generator to use the correct
    * parameters (as defined above).
    */
   public GammaProcessPCA (double s0, double mu, double nu, GammaGen Ggen) {
        super(s0, mu, nu, Ggen);
        this.BMPCA = new BrownianMotionPCA(0.0, 0.0, Math.sqrt(nu), Ggen.getStream());
    }


   public double[] generatePath() {
        double[] uniformsV = new double[d];
        arrayTime = BMPCA.getObservationTimes();
        int i;
        double[] BMpath = BMPCA.generatePath();
        double sigma;
        for(i = 0; i < d; i++){
            sigma = BMPCA.getSigma() * Math.sqrt(arrayTime[i + 1] - arrayTime[i]);
            uniformsV[i] = NormalDist.cdf01( ( BMpath[i+1] - BMpath[i] )/sigma);
        }
        path[0] = x0;
        for(i = 0; i < d; i++){
            path[i+1] = path[i] +
               GammaDist.inverseF(mu2dtOverNu[i], muOverNu, 10, uniformsV[i]);
        }
        observationIndex   = d;
        observationCounter = d;
        return path;
    }


   public double[] generatePath (double[] uniform01)  {
        double[] uniformsV = new double[d];
        arrayTime = BMPCA.getObservationTimes();
        int i;
        double[] BMpath = BMPCA.generatePath(uniform01);
        double sigma;
        for(i = 0; i < d; i++){
            sigma = BMPCA.getSigma() * Math.sqrt(arrayTime[i + 1] - arrayTime[i]);
            uniformsV[i] = NormalDist.cdf01( ( BMpath[i+1] - BMpath[i] )/sigma);
        }
        path[0] = x0;
        for(i = 0; i < d; i++){
            path[i+1] = path[i] +
               GammaDist.inverseF(mu2dtOverNu[i], muOverNu, 10, uniformsV[i]);
        }
        observationIndex   = d;
        observationCounter = d;
        return path;
    }

/**
 * This method is not implemented in this class since the path cannot be
 * generated sequentially.
 */
public double nextObservation() {
       throw new UnsupportedOperationException ("nextObservation is not implemented in GammaProcessPCA");
    }

   /**
    * This method is not implemented in this class since the path cannot
    * be generated sequentially.
    */
   public double nextObservation (double nextT) {
       throw new UnsupportedOperationException ("nextObservation is not implemented in GammaProcessPCA");
    }

   /**
    * Returns the  @ref BrownianMotionPCA that is included in the
    * @ref GammaProcessPCA object.
    */
   public BrownianMotionPCA getBMPCA() {
        return BMPCA;
    }

   /**
    * Sets the observation times of the  @ref GammaProcessPCA and the
    * @ref BrownianMotionPCA.
    */
   public void setObservationTimes (double[] t, int d) {
        super.setObservationTimes(t, d);
        BMPCA.setObservationTimes(t, d);
    }

   /**
    * Sets the parameters `s0`, @f$\mu@f$ and @f$\nu@f$ to new values,
    * and sets the variance parameters of the  @ref BrownianMotionPCA to
    * @f$\nu@f$.
    */
   public void setParams (double s0, double mu, double nu) {
        super.setParams(s0, mu, nu);
        BMPCA.setParams(0.0, 0.0, Math.sqrt(nu));
    }

   /**
    * Resets the  @ref umontreal.ssj.rng.RandomStream of the gamma
    * generator and the  @ref umontreal.ssj.rng.RandomStream of the inner
    * @ref BrownianMotionPCA to `stream`.
    */
   public void setStream (RandomStream stream) {
        super.setStream(stream);
        this.BMPCA.setStream(stream);
}

}