package org.apache.wicket.util.string.interpolator;

import org.apache.wicket.util.io.IClusterable;

public abstract class VariableInterpolator implements IClusterable {
   protected final String string;
   private final boolean exceptionOnNullVarValue;

   public VariableInterpolator(String string) {
      this(string, false);
   }

   public VariableInterpolator(String string, boolean exceptionOnNullVarValue) {
      this.string = string;
      this.exceptionOnNullVarValue = exceptionOnNullVarValue;
   }

   protected abstract String getValue(String var1);

   private int lowerPositive(int i1, int i2) {
      if (i2 < 0) {
         return i1;
      } else if (i1 < 0) {
         return i2;
      } else {
         return i1 < i2 ? i1 : i2;
      }
   }

   public String toString() {
      if (!this.string.contains("${")) {
         return this.string;
      } else {
         StringBuilder buffer = new StringBuilder();
         int pos = 0;

         int start;
         while((start = this.lowerPositive(this.string.indexOf("$$", pos), this.string.indexOf("${", pos))) != -1) {
            buffer.append(this.string.substring(pos, start));
            if (this.string.charAt(start + 1) == '$') {
               buffer.append("$");
               pos = start + 2;
            } else {
               pos = start;
               int startVariableName = start + 2;
               int endVariableName = this.string.indexOf(125, startVariableName);
               if (endVariableName == -1) {
                  break;
               }

               String variableName = this.string.substring(startVariableName, endVariableName);
               String value = this.getValue(variableName);
               if (value == null) {
                  if (this.exceptionOnNullVarValue) {
                     throw new IllegalArgumentException("Value of variable [[" + variableName + "]] could not be resolved while interpolating [[" + this.string + "]]");
                  }

                  buffer.append("${").append(variableName).append("}");
               } else {
                  buffer.append(value);
               }

               pos = endVariableName + 1;
            }
         }

         if (pos < this.string.length()) {
            buffer.append(this.string.substring(pos));
         }

         return buffer.toString();
      }
   }
}
