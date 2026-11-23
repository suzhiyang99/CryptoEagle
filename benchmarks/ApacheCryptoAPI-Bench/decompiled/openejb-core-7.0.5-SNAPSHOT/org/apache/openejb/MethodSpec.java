package org.apache.openejb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodSpec implements Comparable, Serializable {
   private static final long serialVersionUID = -1623511701541770312L;
   private static final int AFTER_OTHER = 1;
   private static final int BEFORE_OTHER = -1;
   private final String methodIntf;
   private final String methodName;
   private final String[] parameterTypes;

   public MethodSpec(String methodIntf, String methodName, String[] parameterTypes) {
      this.methodIntf = methodIntf;
      this.methodName = methodName;
      this.parameterTypes = parameterTypes;
   }

   public MethodSpec(String text) {
      Pattern p = Pattern.compile("(\\S+) (\\S+)\\((\\S*)\\)");
      Matcher m = p.matcher(text);
      if (!m.matches()) {
         throw new IllegalArgumentException("Text must match (\\S+) (\\S+)\\((\\S*)\\) : " + text);
      } else {
         String intfString = m.group(1);
         if (intfString.equals("all")) {
            this.methodIntf = null;
         } else {
            this.methodIntf = intfString;
         }

         this.methodName = m.group(2);
         String parameters = m.group(3);
         if (parameters.length() > 0) {
            this.parameterTypes = parameters.split(" *, *");
         } else {
            this.parameterTypes = null;
         }

      }
   }

   public String getMethodIntf() {
      return this.methodIntf;
   }

   public String getMethodName() {
      return this.methodName;
   }

   public String[] getParameterTypes() {
      return this.parameterTypes;
   }

   public int hashCode() {
      int result = 17;
      result = 37 * result + this.methodIntf.hashCode();
      result = 37 * result + this.methodName.hashCode();

      for(int i = 0; i < this.parameterTypes.length; ++i) {
         result = 37 * result + this.parameterTypes[i].hashCode();
      }

      return result;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof MethodSpec)) {
         return false;
      } else {
         MethodSpec methodSpec = (MethodSpec)obj;
         return this.methodIntf.equals(methodSpec.methodIntf) && this.methodName.equals(methodSpec.methodName) && Arrays.equals(this.parameterTypes, methodSpec.parameterTypes);
      }
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      if (this.methodIntf != null) {
         buffer.append(this.methodIntf);
      } else {
         buffer.append("all");
      }

      buffer.append(" ").append(this.methodName).append('(');
      if (this.parameterTypes != null) {
         for(int i = 0; i < this.parameterTypes.length; ++i) {
            String parameterType = this.parameterTypes[i];
            if (i > 0) {
               buffer.append(',');
            }

            buffer.append(parameterType);
         }
      }

      buffer.append(')');
      return buffer.toString();
   }

   public boolean matches(String methodIntf, String methodName, String[] parameterTypes) {
      assert methodIntf != null;

      assert methodName != null;

      assert parameterTypes != null;

      if (this.methodIntf != null && !methodIntf.equals(this.methodIntf)) {
         return false;
      } else if (this.methodName.equals("*")) {
         return true;
      } else if (!methodName.equals(this.methodName)) {
         return false;
      } else {
         return this.parameterTypes == null ? true : Arrays.equals(parameterTypes, this.parameterTypes);
      }
   }

   public int compareTo(Object o) {
      if (!(o instanceof MethodSpec)) {
         return -1;
      } else if (this == o) {
         return 0;
      } else {
         MethodSpec other = (MethodSpec)o;
         if (this.parameterTypes != null) {
            if (other.parameterTypes == null) {
               return -1;
            } else if (this.methodIntf != null) {
               if (other.methodIntf == null) {
                  return -1;
               } else {
                  int intfOrder = this.methodIntf.compareTo(other.methodIntf);
                  return intfOrder != 0 ? intfOrder : this.compareMethod(other);
               }
            } else {
               return other.methodIntf != null ? 1 : this.compareMethod(other);
            }
         } else if (other.parameterTypes != null) {
            return 1;
         } else if (!this.methodName.equals("*")) {
            if (other.methodName.equals("*")) {
               return -1;
            } else if (this.methodIntf != null) {
               if (other.methodIntf == null) {
                  return -1;
               } else {
                  int intfOrder = this.methodIntf.compareTo(other.methodIntf);
                  return intfOrder != 0 ? intfOrder : this.methodName.compareTo(other.methodName);
               }
            } else {
               return other.methodIntf != null ? 1 : this.methodName.compareTo(other.methodName);
            }
         } else if (!other.methodName.equals("*")) {
            return 1;
         } else if (this.methodIntf != null) {
            return other.methodIntf == null ? -1 : this.methodIntf.compareTo(other.methodIntf);
         } else if (other.methodIntf != null) {
            return 1;
         } else {
            throw new IllegalStateException("Cannot compare " + this + " and " + other);
         }
      }
   }

   private int compareMethod(MethodSpec other) {
      int methodOrder = this.methodName.compareTo(other.methodName);
      if (methodOrder != 0) {
         return methodOrder;
      } else {
         for(int i = 0; i < this.parameterTypes.length; ++i) {
            if (i == other.parameterTypes.length) {
               return 1;
            }

            int paramOrder = this.parameterTypes[i].compareTo(other.parameterTypes[i]);
            if (paramOrder != 0) {
               return paramOrder;
            }
         }

         return -1;
      }
   }
}
