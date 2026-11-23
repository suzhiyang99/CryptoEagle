package org.apache.wicket.util.time;

import org.apache.wicket.util.value.LongValue;

abstract class AbstractTimeValue extends LongValue {
   private static final long serialVersionUID = 1L;

   AbstractTimeValue(long milliseconds) {
      super(milliseconds);
   }

   public final long getMilliseconds() {
      return this.value;
   }
}
