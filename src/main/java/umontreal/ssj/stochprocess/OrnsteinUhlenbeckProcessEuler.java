/*
 * Class:        OrnsteinUhlenbeckProcessEuler
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

/**
 * This class represents an *Ornstein-Uhlenbeck* process as in
 * @ref OrnsteinUhlenbeckProcess, but the process is generated using the
 * simple Euler scheme
 * @anchor REF_stochprocess_OrnsteinUhlenbeckProcessEuler_eq_ornstein_seqEuler
 * @f[
 *   X(t_j) - X(t_{j-1}) = \alpha(b - X(t_{j-1}))(t_j - t_{j-1}) + \sigma\sqrt{t_j - t_{j-1}}  Z_j \tag{ornstein-seqEuler}
 * @f]
 * where @f$Z_j \sim N(0,1)@f$. This is a good approximation only for small
 * time intervals @f$t_j - t_{j-1}@f$.
 *
 * <div class="SSJ-bigskip"></div><div class="SSJ-bigskip"></div>
 */
public class OrnsteinUhlenbeckProcessEuler extends OrnsteinUhlenbeckProcess {

   /**
    * Constructor with parameters @f$\alpha=@f$ `alpha`, @f$b@f$,
    * @f$\sigma=@f$ `sigma` and initial value @f$X(t_0) =@f$ `x0`. The
    * normal variates @f$Z_j@f$ will be generated by inversion using the
    * stream `stream`.
    */
   public OrnsteinUhlenbeckProcessEuler (double x0, double alpha, double b,
                                         double sigma, RandomStream stream) {
      this (x0, alpha, b, sigma, new NormalGen (stream));
   }

   /**
    * Here, the normal variate generator is specified directly instead of
    * specifying the stream. The normal generator `gen` can use another
    * method than inversion.
    */
   public OrnsteinUhlenbeckProcessEuler (double x0, double alpha, double b,
                                         double sigma, NormalGen gen) {
      super (x0, alpha, b, sigma, gen);
   }
public double nextObservation() {
      double xOld = path[observationIndex];
      double x = xOld + (beta - xOld) * alphadt[observationIndex]
                 + sigmasqrdt[observationIndex] * gen.nextDouble();
      observationIndex++;
      path[observationIndex] = x;
      return x;
   }

/**
 * Generates and returns the next observation at time @f$t_{j+1} =@f$
 * `nextTime`. Assumes the previous observation time is @f$t_j@f$ defined
 * earlier (either by this method or by <tt>setObservationTimes</tt>), as
 * well as the value of the previous observation @f$X(t_j)@f$. *Warning*:
 * This method will reset the observations time @f$t_{j+1}@f$ for this
 * process to `nextTime`. The user must make sure that the @f$t_{j+1}@f$
 * supplied is @f$\geq t_j@f$.
 */
public double nextObservation (double nextTime) {
      double previousTime = t[observationIndex];
      double xOld = path[observationIndex];
      observationIndex++;
      t[observationIndex] = nextTime;
      double dt = nextTime - previousTime;
      double x = xOld + alpha * (beta - xOld) * dt
           + sigma * Math.sqrt (dt) * gen.nextDouble();
      path[observationIndex] = x;
      return x;
   }

   /**
    * Generates and returns an observation of the process in `dt` time
    * units, assuming that the process has value @f$x@f$ at the current
    * time. Uses the process parameters specified in the constructor. Note
    * that this method does not affect the sample path of the process
    * stored internally (if any).
    */
   public double nextObservation (double x, double dt) {
      x = x + alpha * (beta - x) * dt
            + sigma * Math.sqrt (dt) * gen.nextDouble();
      return x;
    }

   /**
    * Generates a sample path of the process at all observation times,
    * which are provided in array `t`. Note that `t[0]` should be the
    * observation time of `x0`, the initial value of the process, and
    * `t[]` should have at least @f$d+1@f$ elements (see the
    * `setObservationTimes` method).
    */
   public double[] generatePath() {
      double x;
      double xOld = x0;
      for (int j = 0; j < d; j++) {
          x = xOld + (beta - xOld)*alphadt[j] + sigmasqrdt[j]*gen.nextDouble();
          path[j + 1] = x;
          xOld = x;
      }
      observationIndex = d;
      return path;
   }
   protected void initArrays(int d) {
      double dt;
      for (int j = 0; j < d; j++) {
          dt = t[j+1] - t[j];
          alphadt[j]      = alpha * (dt);
          sigmasqrdt[j]   = sigma * Math.sqrt (dt);
      }
   }
}