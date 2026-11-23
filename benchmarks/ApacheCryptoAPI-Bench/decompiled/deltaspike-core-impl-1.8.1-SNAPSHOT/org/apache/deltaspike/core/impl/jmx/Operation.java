package org.apache.deltaspike.core.impl.jmx;

import java.lang.reflect.Method;

class Operation {
   private final Method operation;
   private final boolean presentAsTabularIfPossible;

   public Operation(Method operation, boolean presentAsTabularIfPossible) {
      this.operation = operation;
      this.presentAsTabularIfPossible = presentAsTabularIfPossible;
   }

   public boolean isPresentAsTabularIfPossible() {
      return this.presentAsTabularIfPossible;
   }

   public Method getOperation() {
      return this.operation;
   }
}
