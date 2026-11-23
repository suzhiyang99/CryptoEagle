package org.apache.wicket.util.string.interpolator;

import java.util.Map;
import org.apache.wicket.util.string.Strings;

public class MapVariableInterpolator extends VariableInterpolator {
   private Map<?, ?> variables;

   public MapVariableInterpolator(String string, Map<?, ?> variables) {
      super(string);
      this.variables = variables;
   }

   public MapVariableInterpolator(String string, Map<?, ?> variables, boolean exceptionOnNullVarValue) {
      super(string, exceptionOnNullVarValue);
      this.variables = variables;
   }

   public final void setVariables(Map<?, ?> variables) {
      this.variables = variables;
   }

   protected String getValue(String variableName) {
      return Strings.toString(this.variables.get(variableName));
   }

   public static String interpolate(String string, Map<?, ?> variables) {
      return (new MapVariableInterpolator(string, variables)).toString();
   }
}
