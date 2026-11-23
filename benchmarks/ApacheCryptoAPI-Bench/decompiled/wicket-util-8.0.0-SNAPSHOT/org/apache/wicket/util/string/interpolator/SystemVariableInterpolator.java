package org.apache.wicket.util.string.interpolator;

public final class SystemVariableInterpolator extends MapVariableInterpolator {
   public SystemVariableInterpolator(String string) {
      super(string, System.getProperties());
   }
}
