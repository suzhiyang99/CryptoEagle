package org.apache.openejb.util;

import java.util.List;

public class CircularReferencesException extends RuntimeException {
   private final List<List> circuits;

   public CircularReferencesException(List<List> circuits) {
      this.circuits = circuits;
   }

   public List<List> getCircuits() {
      return this.circuits;
   }
}
