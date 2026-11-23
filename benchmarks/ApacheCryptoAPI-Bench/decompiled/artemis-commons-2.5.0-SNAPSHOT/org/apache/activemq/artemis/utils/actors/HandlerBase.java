package org.apache.activemq.artemis.utils.actors;

public abstract class HandlerBase {
   private static final Object DUMMY;
   private final ThreadLocal<Object> inHandler = new ThreadLocal();

   protected void enter() {
      assert this.inHandler.get() == null : "should be null";

      this.inHandler.set(DUMMY);
   }

   public boolean inHandler() {
      Object dummy = this.inHandler.get();
      return dummy != null;
   }

   protected void leave() {
      assert this.inHandler.get() != null : "marker not set";

      this.inHandler.set((Object)null);
   }

   static {
      DUMMY = Boolean.TRUE;
   }
}
