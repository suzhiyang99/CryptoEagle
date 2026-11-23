package org.apache.wicket.util.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.wicket.util.diff.myers.MyersDiff;

public class Diff extends ToString {
   public static final String NL = System.getProperty("line.separator");
   public static final String RCS_EOL = "\n";
   protected final Object[] orig;
   protected DiffAlgorithm algorithm;

   public Diff(Object[] original) {
      this(original, (DiffAlgorithm)null);
   }

   public Diff(Object[] original, DiffAlgorithm algorithm) {
      if (original == null) {
         throw new IllegalArgumentException();
      } else {
         this.orig = original;
         if (algorithm != null) {
            this.algorithm = algorithm;
         } else {
            this.algorithm = this.defaultAlgorithm();
         }

      }
   }

   protected DiffAlgorithm defaultAlgorithm() {
      return new MyersDiff();
   }

   public static Revision diff(Object[] orig, Object[] rev) throws DifferentiationFailedException {
      if (orig != null && rev != null) {
         return diff(orig, rev, (DiffAlgorithm)null);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static Revision diff(Object[] orig, Object[] rev, DiffAlgorithm algorithm) throws DifferentiationFailedException {
      if (orig != null && rev != null) {
         return (new Diff(orig, algorithm)).diff(rev);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Revision diff(Object[] rev) throws DifferentiationFailedException {
      return this.orig.length == 0 && rev.length == 0 ? new Revision() : this.algorithm.diff(this.orig, rev);
   }

   public static boolean compare(Object[] orig, Object[] rev) {
      if (orig.length != rev.length) {
         return false;
      } else {
         for(int i = 0; i < orig.length; ++i) {
            if (!orig[i].equals(rev[i])) {
               return false;
            }
         }

         return true;
      }
   }

   public static String arrayToString(Object[] o) {
      return arrayToString(o, NL);
   }

   public static Object[] editAll(Object[] text) {
      Object[] result = new String[text.length];

      for(int i = 0; i < text.length; ++i) {
         result[i] = text[i] + " <edited>";
      }

      return result;
   }

   public static Object[] randomEdit(Object[] text) {
      return randomEdit(text, (long)text.length);
   }

   public static Object[] randomEdit(Object[] text, long seed) {
      List<Object> result = new ArrayList(Arrays.asList(text));
      Random r = new Random(seed);
      int nops = r.nextInt(10);

      for(int i = 0; i < nops; ++i) {
         boolean del = r.nextBoolean();
         int pos = r.nextInt(result.size() + 1);
         int len = Math.min(result.size() - pos, 1 + r.nextInt(4));
         if (del && result.size() > 0) {
            result.subList(pos, pos + len).clear();
         } else {
            for(int k = 0; k < len; ++pos) {
               result.add(pos, "[" + i + "] random edit[" + i + "][" + i + "]");
               ++k;
            }
         }
      }

      return result.toArray();
   }

   public static Object[] shuffle(Object[] text) {
      return shuffle(text, (long)text.length);
   }

   public static Object[] shuffle(Object[] text, long seed) {
      List<Object> result = new ArrayList(Arrays.asList(text));
      Collections.shuffle(result);
      return result.toArray();
   }

   public static Object[] randomSequence(int size) {
      return randomSequence(size, (long)size);
   }

   public static Object[] randomSequence(int size, long seed) {
      Integer[] result = new Integer[size];
      Random r = new Random(seed);

      for(int i = 0; i < result.length; ++i) {
         result[i] = r.nextInt(size);
      }

      return result;
   }
}
