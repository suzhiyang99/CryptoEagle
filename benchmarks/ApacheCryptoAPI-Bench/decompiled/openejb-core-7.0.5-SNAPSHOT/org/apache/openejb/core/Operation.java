package org.apache.openejb.core;

public enum Operation {
   INJECTION(true),
   POST_CONSTRUCT(true),
   BUSINESS(false),
   BUSINESS_WS(false),
   TIMEOUT(true),
   AFTER_BEGIN(true),
   AFTER_COMPLETION(true),
   BEFORE_COMPLETION(true),
   PRE_DESTROY(true),
   REMOVE(false),
   SET_CONTEXT(true),
   UNSET_CONTEXT(true),
   CREATE(true),
   POST_CREATE(true),
   ACTIVATE(true),
   PASSIVATE(true),
   FIND(true),
   HOME(true),
   LOAD(true),
   STORE(true);

   private final boolean callback;

   private Operation(boolean callback) {
      this.callback = callback;
   }

   public boolean isCallback() {
      return this.callback;
   }
}
