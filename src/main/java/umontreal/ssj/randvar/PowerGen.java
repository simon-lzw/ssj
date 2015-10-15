/*
 * Class:        PowerGen
 * Description:  random variate generators for the power distribution
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
import umontreal.ssj.rng.*;
import umontreal.ssj.probdist.*;

/**
 * This class implements random variate generators for the *power*
 * distribution with shape parameter @f$c > 0@f$, over the interval
 * @f$[a,b]@f$. Its density is
 * @anchor REF_randvar_PowerGen_eq_fpower
 * @f[
 *   f(x) = \frac{c(x-a)^{c - 1}}{(b - a)^c}, \tag{fpower}
 * @f]
 * for @f$a \le x \le b@f$, and 0 elsewhere.
 *
 * <div class="SSJ-bigskip"></div>
 *
 * @ingroup randvar_continuous
 */
public class PowerGen extends RandomVariateGen {
   private double a;
   private double b;
   private double c;

   /**
    * Creates a Power random variate generator with parameters @f$a =@f$
    * `a`, @f$b =@f$ `b` and @f$c =@f$ `c`, using stream `s`.
    */
   public PowerGen (RandomStream s, double a, double b, double c) {
      super (s, new PowerDist(a, b, c));
      setParams (a,  b, c);
   }

   /**
    * Creates a Power random variate generator with parameters @f$a =0@f$,
    * @f$b =1@f$ and @f$c =@f$ `c`, using stream `s`.
    */
   public PowerGen (RandomStream s, double c) {
      super (s, new PowerDist(0.0, 1.0, c));
      setParams (0.0, 1.0, c);
   }

   /**
    * Creates a new generator for the power distribution `dist` and stream
    * `s`.
    */
   public PowerGen (RandomStream s, PowerDist dist) {
      super (s, dist);
      if (dist != null)
         setParams (dist.getA(), dist.getB(), dist.getC());
   }

   /**
    * Uses inversion to generate a new variate from the power distribution
    * with parameters @f$a = @f$&nbsp;`a`, @f$b = @f$&nbsp;`b`, and @f$c =
    * @f$&nbsp;`c`, using stream `s`.
    */
   public static double nextDouble (RandomStream s, double a, double b,
                                    double c) {
       return PowerDist.inverseF (a, b, c, s.nextDouble());
   }

   /**
    * Returns the parameter @f$a@f$.
    */
   public double getA() {
      return a;
   }

   /**
    * Returns the parameter @f$b@f$.
    */
   public double getB() {
      return b;
   }

   /**
    * Returns the parameter @f$c@f$.
    */
   public double getC() {
      return c;
   }

   /**
    * Sets the parameters @f$a@f$, @f$b@f$ and @f$c@f$ for this object.
    */
   public void setParams (double a, double b, double c) {
      this.a  = a;
      this.b  = b;
      this.c  = c;
   }
}