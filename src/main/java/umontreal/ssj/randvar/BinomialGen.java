/*
 * Class:        BinomialGen
 * Description:  random variate generators for the binomial distribution
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
package umontreal.ssj.randvar;
import umontreal.ssj.probdist.*;
import umontreal.ssj.rng.*;

/**
 * This class implements random variate generators for the *binomial*
 * distribution. It has parameters @f$n@f$ and @f$p@f$ with mass function
 * @anchor REF_randvar_BinomialGen_eq_fmass_binomial
 * @f[
 *   p(x) = {n \choose x} p^x (1-p)^{n-x} = \frac{n!}{x!(n-x)!}\; p^x (1-p)^{n-x} \qquad\mbox{for } x=0,1,2,…, n \tag{fmass-binomial}
 * @f]
 * where
 *  @f$n@f$ is a positive integer, and @f$0\le p\le1@f$.
 *
 * The (non-static) `nextInt` method simply calls `inverseF` on the
 * distribution.
 *
 * <div class="SSJ-bigskip"></div>
 *
 * @ingroup randvar_discrete
 */
public class BinomialGen extends RandomVariateGenInt {
   protected int    n = -1;
   protected double p = -1.0;

   /**
    * Creates a binomial random variate generator with parameters @f$n@f$
    * and @f$p@f$, using stream `s`.
    */
   public BinomialGen (RandomStream s, int n, double p) {
      super (s, new BinomialDist (n, p));
      setParams (n, p);
   }

   /**
    * Creates a random variate generator for the *binomial* distribution
    * `dist` and the random stream `s`.
    */
   public BinomialGen (RandomStream s, BinomialDist dist) {
      super (s, dist);
      if (dist != null)
         setParams (dist.getN(), dist.getP());
   }

   /**
    * Generates a new integer from the *binomial* distribution with
    * parameters @f$n = @f$&nbsp;`n` and @f$p = @f$&nbsp;`p`, using the
    * given stream `s`.
    */
   public static int nextInt (RandomStream s, int n, double p) {
      return BinomialDist.inverseF (n, p, s.nextDouble());
   }

   /**
    * Returns the parameter @f$n@f$ of this object.
    */
   public int getN() {
      return n;
   }

   /**
    * Returns the parameter @f$p@f$ of this object.
    */
   public double getP() {
      return p;
   }

   /**
    * Sets the parameter @f$n@f$ and @f$p@f$ of this object.
    */
   protected void setParams (int n, double p) {
      if (p < 0.0 || p > 1.0)
         throw new IllegalArgumentException ("p not in range [0, 1]");
      if (n <= 0)
         throw new IllegalArgumentException ("n <= 0");
      this.p = p;
      this.n = n;
   }
}