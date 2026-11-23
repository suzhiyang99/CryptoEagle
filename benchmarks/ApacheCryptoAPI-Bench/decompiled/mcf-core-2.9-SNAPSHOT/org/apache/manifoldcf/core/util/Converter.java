package org.apache.manifoldcf.core.util;

import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class Converter {
   public static final String _rcsid = "@(#)$Id$";

   public static double asDouble(Object o) throws ManifoldCFException {
      if (o instanceof Double) {
         return (Double)o;
      } else if (o instanceof String) {
         return new Double((String)o);
      } else if (o instanceof Float) {
         return (double)(Float)o;
      } else if (o instanceof Long) {
         return (double)(Long)o;
      } else {
         throw new ManifoldCFException("Can't convert to double");
      }
   }

   public static long asLong(Object o) throws ManifoldCFException {
      if (o instanceof Long) {
         return (Long)o;
      } else if (o instanceof Double) {
         return (long)(Double)o;
      } else if (o instanceof String) {
         return new Long((String)o);
      } else if (o instanceof Float) {
         return (long)(Float)o;
      } else {
         throw new ManifoldCFException("Can't convert to long");
      }
   }
}
