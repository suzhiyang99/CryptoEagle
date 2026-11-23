package org.apache.openejb.math.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import org.apache.openejb.math.MathRuntimeException;

public final class MathUtils {
   public static final double EPSILON = (double)1.110223E-16F;
   public static final double SAFE_MIN = Double.MIN_NORMAL;
   public static final double TWO_PI = (Math.PI * 2D);
   private static final byte NB = -1;
   private static final short NS = -1;
   private static final byte PB = 1;
   private static final short PS = 1;
   private static final byte ZB = 0;
   private static final short ZS = 0;
   private static final int NAN_GAP = 4194304;
   private static final long SGN_MASK = Long.MIN_VALUE;
   private static final long[] FACTORIALS = new long[]{1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L};

   private MathUtils() {
   }

   public static int addAndCheck(int x, int y) {
      long s = (long)x + (long)y;
      if (s >= -2147483648L && s <= 2147483647L) {
         return (int)s;
      } else {
         throw new ArithmeticException("overflow: add");
      }
   }

   public static long addAndCheck(long a, long b) {
      return addAndCheck(a, b, "overflow: add");
   }

   private static long addAndCheck(long a, long b, String msg) {
      long ret;
      if (a > b) {
         ret = addAndCheck(b, a, msg);
      } else if (a < 0L) {
         if (b < 0L) {
            if (Long.MIN_VALUE - b > a) {
               throw new ArithmeticException(msg);
            }

            ret = a + b;
         } else {
            ret = a + b;
         }
      } else {
         if (a > Long.MAX_VALUE - b) {
            throw new ArithmeticException(msg);
         }

         ret = a + b;
      }

      return ret;
   }

   public static long binomialCoefficient(int n, int k) {
      checkBinomial(n, k);
      if (n != k && k != 0) {
         if (k != 1 && k != n - 1) {
            if (k > n / 2) {
               return binomialCoefficient(n, n - k);
            } else {
               long result = 1L;
               if (n <= 61) {
                  int i = n - k + 1;

                  for(int j = 1; j <= k; ++j) {
                     result = result * (long)i / (long)j;
                     ++i;
                  }
               } else if (n <= 66) {
                  int i = n - k + 1;

                  for(int j = 1; j <= k; ++j) {
                     long d = (long)gcd(i, j);
                     result = result / ((long)j / d) * (long)i / d;
                     ++i;
                  }
               } else {
                  int i = n - k + 1;

                  for(int j = 1; j <= k; ++j) {
                     long d = (long)gcd(i, j);
                     result = mulAndCheck(result / ((long)j / d), (long)i / d);
                     ++i;
                  }
               }

               return result;
            }
         } else {
            return (long)n;
         }
      } else {
         return 1L;
      }
   }

   public static double binomialCoefficientDouble(int n, int k) {
      checkBinomial(n, k);
      if (n != k && k != 0) {
         if (k != 1 && k != n - 1) {
            if (k > n / 2) {
               return binomialCoefficientDouble(n, n - k);
            } else if (n < 67) {
               return (double)binomialCoefficient(n, k);
            } else {
               double result = (double)1.0F;

               for(int i = 1; i <= k; ++i) {
                  result *= (double)(n - k + i) / (double)i;
               }

               return Math.floor(result + (double)0.5F);
            }
         } else {
            return (double)n;
         }
      } else {
         return (double)1.0F;
      }
   }

   public static double binomialCoefficientLog(int n, int k) {
      checkBinomial(n, k);
      if (n != k && k != 0) {
         if (k != 1 && k != n - 1) {
            if (n < 67) {
               return Math.log((double)binomialCoefficient(n, k));
            } else if (n < 1030) {
               return Math.log(binomialCoefficientDouble(n, k));
            } else if (k > n / 2) {
               return binomialCoefficientLog(n, n - k);
            } else {
               double logSum = (double)0.0F;

               for(int i = n - k + 1; i <= n; ++i) {
                  logSum += Math.log((double)i);
               }

               for(int i = 2; i <= k; ++i) {
                  logSum -= Math.log((double)i);
               }

               return logSum;
            }
         } else {
            return Math.log((double)n);
         }
      } else {
         return (double)0.0F;
      }
   }

   private static void checkBinomial(int n, int k) throws IllegalArgumentException {
      if (n < k) {
         throw MathRuntimeException.createIllegalArgumentException("must have n >= k for binomial coefficient (n,k), got n = {0}, k = {1}", n, k);
      } else if (n < 0) {
         throw MathRuntimeException.createIllegalArgumentException("must have n >= 0 for binomial coefficient (n,k), got n = {0}", n);
      }
   }

   public static int compareTo(double x, double y, double eps) {
      if (equals(x, y, eps)) {
         return 0;
      } else {
         return x < y ? -1 : 1;
      }
   }

   public static double cosh(double x) {
      return (Math.exp(x) + Math.exp(-x)) / (double)2.0F;
   }

   public static boolean equals(double x, double y) {
      return Double.isNaN(x) && Double.isNaN(y) || x == y;
   }

   public static boolean equals(double x, double y, double eps) {
      return equals(x, y) || Math.abs(y - x) <= eps;
   }

   public static boolean equals(double x, double y, int maxUlps) {
      assert maxUlps > 0 && maxUlps < 4194304;

      long xInt = Double.doubleToLongBits(x);
      long yInt = Double.doubleToLongBits(y);
      if (xInt < 0L) {
         xInt = Long.MIN_VALUE - xInt;
      }

      if (yInt < 0L) {
         yInt = Long.MIN_VALUE - yInt;
      }

      return Math.abs(xInt - yInt) <= (long)maxUlps;
   }

   public static boolean equals(double[] x, double[] y) {
      if (x != null && y != null) {
         if (x.length != y.length) {
            return false;
         } else {
            for(int i = 0; i < x.length; ++i) {
               if (!equals(x[i], y[i])) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return !(x == null ^ y == null);
      }
   }

   public static long factorial(int n) {
      if (n < 0) {
         throw MathRuntimeException.createIllegalArgumentException("must have n >= 0 for n!, got n = {0}", n);
      } else if (n > 20) {
         throw new ArithmeticException("factorial value is too large to fit in a long");
      } else {
         return FACTORIALS[n];
      }
   }

   public static double factorialDouble(int n) {
      if (n < 0) {
         throw MathRuntimeException.createIllegalArgumentException("must have n >= 0 for n!, got n = {0}", n);
      } else {
         return n < 21 ? (double)factorial(n) : Math.floor(Math.exp(factorialLog(n)) + (double)0.5F);
      }
   }

   public static double factorialLog(int n) {
      if (n < 0) {
         throw MathRuntimeException.createIllegalArgumentException("must have n >= 0 for n!, got n = {0}", n);
      } else if (n < 21) {
         return Math.log((double)factorial(n));
      } else {
         double logSum = (double)0.0F;

         for(int i = 2; i <= n; ++i) {
            logSum += Math.log((double)i);
         }

         return logSum;
      }
   }

   public static int gcd(int p, int q) {
      int u = p;
      int v = q;
      if (p != 0 && q != 0) {
         if (p > 0) {
            u = -p;
         }

         if (q > 0) {
            v = -q;
         }

         int k;
         for(k = 0; (u & 1) == 0 && (v & 1) == 0 && k < 31; ++k) {
            u /= 2;
            v /= 2;
         }

         if (k == 31) {
            throw MathRuntimeException.createArithmeticException("overflow: gcd({0}, {1}) is 2^31", p, q);
         } else {
            int t = (u & 1) == 1 ? v : -(u / 2);

            while(true) {
               while((t & 1) != 0) {
                  if (t > 0) {
                     u = -t;
                  } else {
                     v = t;
                  }

                  t = (v - u) / 2;
                  if (t == 0) {
                     return -u * (1 << k);
                  }
               }

               t /= 2;
            }
         }
      } else if (p != Integer.MIN_VALUE && q != Integer.MIN_VALUE) {
         return Math.abs(p) + Math.abs(q);
      } else {
         throw MathRuntimeException.createArithmeticException("overflow: gcd({0}, {1}) is 2^31", p, q);
      }
   }

   public static long gcd(long p, long q) {
      long u = p;
      long v = q;
      if (p != 0L && q != 0L) {
         if (p > 0L) {
            u = -p;
         }

         if (q > 0L) {
            v = -q;
         }

         int k;
         for(k = 0; (u & 1L) == 0L && (v & 1L) == 0L && k < 63; ++k) {
            u /= 2L;
            v /= 2L;
         }

         if (k == 63) {
            throw MathRuntimeException.createArithmeticException("overflow: gcd({0}, {1}) is 2^63", p, q);
         } else {
            long t = (u & 1L) == 1L ? v : -(u / 2L);

            while(true) {
               while((t & 1L) != 0L) {
                  if (t > 0L) {
                     u = -t;
                  } else {
                     v = t;
                  }

                  t = (v - u) / 2L;
                  if (t == 0L) {
                     return -u * (1L << k);
                  }
               }

               t /= 2L;
            }
         }
      } else if (p != Long.MIN_VALUE && q != Long.MIN_VALUE) {
         return Math.abs(p) + Math.abs(q);
      } else {
         throw MathRuntimeException.createArithmeticException("overflow: gcd({0}, {1}) is 2^63", p, q);
      }
   }

   public static int hash(double value) {
      return (new Double(value)).hashCode();
   }

   public static int hash(double[] value) {
      return Arrays.hashCode(value);
   }

   public static byte indicator(byte x) {
      return (byte)(x >= 0 ? 1 : -1);
   }

   public static double indicator(double x) {
      if (Double.isNaN(x)) {
         return Double.NaN;
      } else {
         return x >= (double)0.0F ? (double)1.0F : (double)-1.0F;
      }
   }

   public static float indicator(float x) {
      if (Float.isNaN(x)) {
         return Float.NaN;
      } else {
         return x >= 0.0F ? 1.0F : -1.0F;
      }
   }

   public static int indicator(int x) {
      return x >= 0 ? 1 : -1;
   }

   public static long indicator(long x) {
      return x >= 0L ? 1L : -1L;
   }

   public static short indicator(short x) {
      return (short)(x >= 0 ? 1 : -1);
   }

   public static int lcm(int a, int b) {
      if (a != 0 && b != 0) {
         int lcm = Math.abs(mulAndCheck(a / gcd(a, b), b));
         if (lcm == Integer.MIN_VALUE) {
            throw MathRuntimeException.createArithmeticException("overflow: lcm({0}, {1}) is 2^31", a, b);
         } else {
            return lcm;
         }
      } else {
         return 0;
      }
   }

   public static long lcm(long a, long b) {
      if (a != 0L && b != 0L) {
         long lcm = Math.abs(mulAndCheck(a / gcd(a, b), b));
         if (lcm == Long.MIN_VALUE) {
            throw MathRuntimeException.createArithmeticException("overflow: lcm({0}, {1}) is 2^63", a, b);
         } else {
            return lcm;
         }
      } else {
         return 0L;
      }
   }

   public static double log(double base, double x) {
      return Math.log(x) / Math.log(base);
   }

   public static int mulAndCheck(int x, int y) {
      long m = (long)x * (long)y;
      if (m >= -2147483648L && m <= 2147483647L) {
         return (int)m;
      } else {
         throw new ArithmeticException("overflow: mul");
      }
   }

   public static long mulAndCheck(long a, long b) {
      String msg = "overflow: multiply";
      long ret;
      if (a > b) {
         ret = mulAndCheck(b, a);
      } else if (a < 0L) {
         if (b < 0L) {
            if (a < Long.MAX_VALUE / b) {
               throw new ArithmeticException("overflow: multiply");
            }

            ret = a * b;
         } else if (b > 0L) {
            if (Long.MIN_VALUE / b > a) {
               throw new ArithmeticException("overflow: multiply");
            }

            ret = a * b;
         } else {
            ret = 0L;
         }
      } else if (a > 0L) {
         if (a > Long.MAX_VALUE / b) {
            throw new ArithmeticException("overflow: multiply");
         }

         ret = a * b;
      } else {
         ret = 0L;
      }

      return ret;
   }

   public static double nextAfter(double d, double direction) {
      if (!Double.isNaN(d) && !Double.isInfinite(d)) {
         if (d == (double)0.0F) {
            return direction < (double)0.0F ? -Double.MIN_VALUE : Double.MIN_VALUE;
         } else {
            long bits = Double.doubleToLongBits(d);
            long sign = bits & Long.MIN_VALUE;
            long exponent = bits & 9218868437227405312L;
            long mantissa = bits & 4503599627370495L;
            if (d * (direction - d) >= (double)0.0F) {
               return mantissa == 4503599627370495L ? Double.longBitsToDouble(sign | exponent + 4503599627370496L) : Double.longBitsToDouble(sign | exponent | mantissa + 1L);
            } else {
               return mantissa == 0L ? Double.longBitsToDouble(sign | exponent - 4503599627370496L | 4503599627370495L) : Double.longBitsToDouble(sign | exponent | mantissa - 1L);
            }
         }
      } else {
         return d;
      }
   }

   public static double scalb(double d, int scaleFactor) {
      if (d != (double)0.0F && !Double.isNaN(d) && !Double.isInfinite(d)) {
         long bits = Double.doubleToLongBits(d);
         long exponent = bits & 9218868437227405312L;
         long rest = bits & -9218868437227405313L;
         long newBits = rest | exponent + ((long)scaleFactor << 52);
         return Double.longBitsToDouble(newBits);
      } else {
         return d;
      }
   }

   public static double normalizeAngle(double a, double center) {
      return a - (Math.PI * 2D) * Math.floor((a + Math.PI - center) / (Math.PI * 2D));
   }

   public static double[] normalizeArray(double[] values, double normalizedSum) throws ArithmeticException, IllegalArgumentException {
      if (Double.isInfinite(normalizedSum)) {
         throw MathRuntimeException.createIllegalArgumentException("Cannot normalize to an infinite value");
      } else if (Double.isNaN(normalizedSum)) {
         throw MathRuntimeException.createIllegalArgumentException("Cannot normalize to NaN");
      } else {
         double sum = (double)0.0F;
         int len = values.length;
         double[] out = new double[len];

         for(int i = 0; i < len; ++i) {
            if (Double.isInfinite(values[i])) {
               throw MathRuntimeException.createArithmeticException("Array contains an infinite element, {0} at index {1}", values[i], i);
            }

            if (!Double.isNaN(values[i])) {
               sum += values[i];
            }
         }

         if (sum == (double)0.0F) {
            throw MathRuntimeException.createArithmeticException("Array sums to zero");
         } else {
            for(int i = 0; i < len; ++i) {
               if (Double.isNaN(values[i])) {
                  out[i] = Double.NaN;
               } else {
                  out[i] = values[i] * normalizedSum / sum;
               }
            }

            return out;
         }
      }
   }

   public static double round(double x, int scale) {
      return round(x, scale, 4);
   }

   public static double round(double x, int scale, int roundingMethod) {
      try {
         return (new BigDecimal(Double.toString(x))).setScale(scale, roundingMethod).doubleValue();
      } catch (NumberFormatException var5) {
         return Double.isInfinite(x) ? x : Double.NaN;
      }
   }

   public static float round(float x, int scale) {
      return round(x, scale, 4);
   }

   public static float round(float x, int scale, int roundingMethod) {
      float sign = indicator(x);
      float factor = (float)Math.pow((double)10.0F, (double)scale) * sign;
      return (float)roundUnscaled((double)(x * factor), (double)sign, roundingMethod) / factor;
   }

   private static double roundUnscaled(double unscaled, double sign, int roundingMethod) {
      switch (roundingMethod) {
         case 0:
            unscaled = Math.ceil(nextAfter(unscaled, Double.POSITIVE_INFINITY));
            break;
         case 1:
            unscaled = Math.floor(nextAfter(unscaled, Double.NEGATIVE_INFINITY));
            break;
         case 2:
            if (sign == (double)-1.0F) {
               unscaled = Math.floor(nextAfter(unscaled, Double.NEGATIVE_INFINITY));
            } else {
               unscaled = Math.ceil(nextAfter(unscaled, Double.POSITIVE_INFINITY));
            }
            break;
         case 3:
            if (sign == (double)-1.0F) {
               unscaled = Math.ceil(nextAfter(unscaled, Double.POSITIVE_INFINITY));
            } else {
               unscaled = Math.floor(nextAfter(unscaled, Double.NEGATIVE_INFINITY));
            }
            break;
         case 4:
            unscaled = nextAfter(unscaled, Double.POSITIVE_INFINITY);
            double fraction = unscaled - Math.floor(unscaled);
            if (fraction >= (double)0.5F) {
               unscaled = Math.ceil(unscaled);
            } else {
               unscaled = Math.floor(unscaled);
            }
            break;
         case 5:
            unscaled = nextAfter(unscaled, Double.NEGATIVE_INFINITY);
            double fraction = unscaled - Math.floor(unscaled);
            if (fraction > (double)0.5F) {
               unscaled = Math.ceil(unscaled);
            } else {
               unscaled = Math.floor(unscaled);
            }
            break;
         case 6:
            double fraction = unscaled - Math.floor(unscaled);
            if (fraction > (double)0.5F) {
               unscaled = Math.ceil(unscaled);
            } else if (fraction < (double)0.5F) {
               unscaled = Math.floor(unscaled);
            } else if (Math.floor(unscaled) / (double)2.0F == Math.floor(Math.floor(unscaled) / (double)2.0F)) {
               unscaled = Math.floor(unscaled);
            } else {
               unscaled = Math.ceil(unscaled);
            }
            break;
         case 7:
            if (unscaled != Math.floor(unscaled)) {
               throw new ArithmeticException("Inexact result from rounding");
            }
            break;
         default:
            throw MathRuntimeException.createIllegalArgumentException("invalid rounding method {0}, valid methods: {1} ({2}), {3} ({4}), {5} ({6}), {7} ({8}), {9} ({10}), {11} ({12}), {13} ({14}), {15} ({16})", roundingMethod, "ROUND_CEILING", 2, "ROUND_DOWN", 1, "ROUND_FLOOR", 3, "ROUND_HALF_DOWN", 5, "ROUND_HALF_EVEN", 6, "ROUND_HALF_UP", 4, "ROUND_UNNECESSARY", 7, "ROUND_UP", 0);
      }

      return unscaled;
   }

   public static byte sign(byte x) {
      return (byte)(x == 0 ? 0 : (x > 0 ? 1 : -1));
   }

   public static double sign(double x) {
      if (Double.isNaN(x)) {
         return Double.NaN;
      } else {
         return x == (double)0.0F ? (double)0.0F : (x > (double)0.0F ? (double)1.0F : (double)-1.0F);
      }
   }

   public static float sign(float x) {
      if (Float.isNaN(x)) {
         return Float.NaN;
      } else {
         return x == 0.0F ? 0.0F : (x > 0.0F ? 1.0F : -1.0F);
      }
   }

   public static int sign(int x) {
      return x == 0 ? 0 : (x > 0 ? 1 : -1);
   }

   public static long sign(long x) {
      return x == 0L ? 0L : (x > 0L ? 1L : -1L);
   }

   public static short sign(short x) {
      return (short)(x == 0 ? 0 : (x > 0 ? 1 : -1));
   }

   public static double sinh(double x) {
      return (Math.exp(x) - Math.exp(-x)) / (double)2.0F;
   }

   public static int subAndCheck(int x, int y) {
      long s = (long)x - (long)y;
      if (s >= -2147483648L && s <= 2147483647L) {
         return (int)s;
      } else {
         throw new ArithmeticException("overflow: subtract");
      }
   }

   public static long subAndCheck(long a, long b) {
      String msg = "overflow: subtract";
      long ret;
      if (b == Long.MIN_VALUE) {
         if (a >= 0L) {
            throw new ArithmeticException("overflow: subtract");
         }

         ret = a - b;
      } else {
         ret = addAndCheck(a, -b, "overflow: subtract");
      }

      return ret;
   }

   public static int pow(int k, int e) throws IllegalArgumentException {
      if (e < 0) {
         throw MathRuntimeException.createIllegalArgumentException("cannot raise an integral value to a negative power ({0}^{1})", k, e);
      } else {
         int result = 1;

         for(int k2p = k; e != 0; e >>= 1) {
            if ((e & 1) != 0) {
               result *= k2p;
            }

            k2p *= k2p;
         }

         return result;
      }
   }

   public static int pow(int k, long e) throws IllegalArgumentException {
      if (e < 0L) {
         throw MathRuntimeException.createIllegalArgumentException("cannot raise an integral value to a negative power ({0}^{1})", k, e);
      } else {
         int result = 1;

         for(int k2p = k; e != 0L; e >>= 1) {
            if ((e & 1L) != 0L) {
               result *= k2p;
            }

            k2p *= k2p;
         }

         return result;
      }
   }

   public static long pow(long k, int e) throws IllegalArgumentException {
      if (e < 0) {
         throw MathRuntimeException.createIllegalArgumentException("cannot raise an integral value to a negative power ({0}^{1})", k, e);
      } else {
         long result = 1L;

         for(long k2p = k; e != 0; e >>= 1) {
            if ((e & 1) != 0) {
               result *= k2p;
            }

            k2p *= k2p;
         }

         return result;
      }
   }

   public static long pow(long k, long e) throws IllegalArgumentException {
      if (e < 0L) {
         throw MathRuntimeException.createIllegalArgumentException("cannot raise an integral value to a negative power ({0}^{1})", k, e);
      } else {
         long result = 1L;

         for(long k2p = k; e != 0L; e >>= 1) {
            if ((e & 1L) != 0L) {
               result *= k2p;
            }

            k2p *= k2p;
         }

         return result;
      }
   }

   public static BigInteger pow(BigInteger k, int e) throws IllegalArgumentException {
      if (e < 0) {
         throw MathRuntimeException.createIllegalArgumentException("cannot raise an integral value to a negative power ({0}^{1})", k, e);
      } else {
         return k.pow(e);
      }
   }

   public static BigInteger pow(BigInteger k, long e) throws IllegalArgumentException {
      if (e < 0L) {
         throw MathRuntimeException.createIllegalArgumentException("cannot raise an integral value to a negative power ({0}^{1})", k, e);
      } else {
         BigInteger result = BigInteger.ONE;

         for(BigInteger k2p = k; e != 0L; e >>= 1) {
            if ((e & 1L) != 0L) {
               result = result.multiply(k2p);
            }

            k2p = k2p.multiply(k2p);
         }

         return result;
      }
   }

   public static BigInteger pow(BigInteger k, BigInteger e) throws IllegalArgumentException {
      if (e.compareTo(BigInteger.ZERO) < 0) {
         throw MathRuntimeException.createIllegalArgumentException("cannot raise an integral value to a negative power ({0}^{1})", k, e);
      } else {
         BigInteger result = BigInteger.ONE;

         for(BigInteger k2p = k; !BigInteger.ZERO.equals(e); e = e.shiftRight(1)) {
            if (e.testBit(0)) {
               result = result.multiply(k2p);
            }

            k2p = k2p.multiply(k2p);
         }

         return result;
      }
   }

   public static double distance1(double[] p1, double[] p2) {
      double sum = (double)0.0F;

      for(int i = 0; i < p1.length; ++i) {
         sum += Math.abs(p1[i] - p2[i]);
      }

      return sum;
   }

   public static int distance1(int[] p1, int[] p2) {
      int sum = 0;

      for(int i = 0; i < p1.length; ++i) {
         sum += Math.abs(p1[i] - p2[i]);
      }

      return sum;
   }

   public static double distance(double[] p1, double[] p2) {
      double sum = (double)0.0F;

      for(int i = 0; i < p1.length; ++i) {
         double dp = p1[i] - p2[i];
         sum += dp * dp;
      }

      return Math.sqrt(sum);
   }

   public static double distance(int[] p1, int[] p2) {
      double sum = (double)0.0F;

      for(int i = 0; i < p1.length; ++i) {
         double dp = (double)(p1[i] - p2[i]);
         sum += dp * dp;
      }

      return Math.sqrt(sum);
   }

   public static double distanceInf(double[] p1, double[] p2) {
      double max = (double)0.0F;

      for(int i = 0; i < p1.length; ++i) {
         max = Math.max(max, Math.abs(p1[i] - p2[i]));
      }

      return max;
   }

   public static int distanceInf(int[] p1, int[] p2) {
      int max = 0;

      for(int i = 0; i < p1.length; ++i) {
         max = Math.max(max, Math.abs(p1[i] - p2[i]));
      }

      return max;
   }

   public static void checkOrder(double[] val, int dir, boolean strict) {
      double previous = val[0];
      int max = val.length;

      for(int i = 1; i < max; ++i) {
         if (dir > 0) {
            if (strict) {
               if (val[i] <= previous) {
                  throw MathRuntimeException.createIllegalArgumentException("points {0} and {1} are not strictly increasing ({2} >= {3})", i - 1, i, previous, val[i]);
               }
            } else if (val[i] < previous) {
               throw MathRuntimeException.createIllegalArgumentException("points {0} and {1} are not increasing ({2} > {3})", i - 1, i, previous, val[i]);
            }
         } else if (strict) {
            if (val[i] >= previous) {
               throw MathRuntimeException.createIllegalArgumentException("points {0} and {1} are not strictly decreasing ({2} <= {3})", i - 1, i, previous, val[i]);
            }
         } else if (val[i] > previous) {
            throw MathRuntimeException.createIllegalArgumentException("points {0} and {1} are not decreasing ({2} < {3})", i - 1, i, previous, val[i]);
         }

         previous = val[i];
      }

   }
}
