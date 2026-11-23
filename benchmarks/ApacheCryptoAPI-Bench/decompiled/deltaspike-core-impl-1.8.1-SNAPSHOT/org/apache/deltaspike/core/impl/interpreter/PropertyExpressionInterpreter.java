package org.apache.deltaspike.core.impl.interpreter;

import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.api.interpreter.BasePropertyExpressionInterpreter;

public class PropertyExpressionInterpreter extends BasePropertyExpressionInterpreter {
   protected String getConfiguredValue(String key) {
      return ConfigResolver.getProjectStageAwarePropertyValue(key);
   }
}
