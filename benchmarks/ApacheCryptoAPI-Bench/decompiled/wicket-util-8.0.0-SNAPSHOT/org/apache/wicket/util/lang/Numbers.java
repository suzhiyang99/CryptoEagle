package org.apache.wicket.util.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Numbers {
   private static final Logger LOG = LoggerFactory.getLogger(Numbers.class);

   private Numbers() {
   }

   public static Number getMinValue(Class<? extends Number> numberType) {
      Number result;
      if (Integer.class != numberType && Integer.TYPE != numberType) {
         if (Long.class != numberType && Long.TYPE != numberType) {
            if (Float.class != numberType && Float.TYPE != numberType) {
               if (Double.class != numberType && Double.TYPE != numberType) {
                  if (Byte.class != numberType && Byte.TYPE != numberType) {
                     if (Short.class != numberType && Short.TYPE != numberType) {
                        LOG.debug("'{}' has no minimum value. Falling back to Double.", numberType);
                        result = -Double.MAX_VALUE;
                     } else {
                        result = Short.MIN_VALUE;
                     }
                  } else {
                     result = -128;
                  }
               } else {
                  result = -Double.MAX_VALUE;
               }
            } else {
               result = -Float.MAX_VALUE;
            }
         } else {
            result = Long.MIN_VALUE;
         }
      } else {
         result = Integer.MIN_VALUE;
      }

      return result;
   }

   public static Number getMaxValue(Class<? extends Number> numberType) {
      Number result;
      if (Integer.class != numberType && Integer.TYPE != numberType) {
         if (Long.class != numberType && Long.TYPE != numberType) {
            if (Float.class != numberType && Float.TYPE != numberType) {
               if (Double.class != numberType && Double.TYPE != numberType) {
                  if (Byte.class != numberType && Byte.TYPE != numberType) {
                     if (Short.class != numberType && Short.TYPE != numberType) {
                        LOG.debug("'{}' has no maximum value. Falling back to Double.MAX_VALUE.");
                        result = Double.MAX_VALUE;
                     } else {
                        result = 32767;
                     }
                  } else {
                     result = 127;
                  }
               } else {
                  result = Double.MAX_VALUE;
               }
            } else {
               result = Float.MAX_VALUE;
            }
         } else {
            result = Long.MAX_VALUE;
         }
      } else {
         result = Integer.MAX_VALUE;
      }

      return result;
   }
}
