package org.apache.openejb.core.ivm;

public final class IntraVmCopyMonitor {
   private static final ThreadLocal<IntraVmCopyMonitor> threadMonitor = new ThreadLocal();
   private State state;

   private IntraVmCopyMonitor() {
      this.state = IntraVmCopyMonitor.State.NONE;
   }

   public static boolean exists() {
      return threadMonitor.get() != null;
   }

   public static void release() {
      threadMonitor.set((Object)null);
   }

   private static IntraVmCopyMonitor getMonitor() {
      IntraVmCopyMonitor monitor = (IntraVmCopyMonitor)threadMonitor.get();
      if (monitor == null) {
         monitor = new IntraVmCopyMonitor();
         threadMonitor.set(monitor);
      }

      return monitor;
   }

   public static void pre(State state) {
      getMonitor().state = state;
   }

   public static void post() {
      pre(IntraVmCopyMonitor.State.NONE);
   }

   public static State state() {
      return getMonitor().state;
   }

   public static void prePassivationOperation() {
      pre(IntraVmCopyMonitor.State.PASSIVATION);
   }

   public static void postPassivationOperation() {
      post();
   }

   public static void preCrossClassLoaderOperation() {
      pre(IntraVmCopyMonitor.State.CLASSLOADER_COPY);
   }

   public static void postCrossClassLoaderOperation() {
      post();
   }

   public static void preCopyOperation() {
      pre(IntraVmCopyMonitor.State.COPY);
   }

   public static void postCopyOperation() {
      post();
   }

   public static boolean isIntraVmCopyOperation() {
      return state() == IntraVmCopyMonitor.State.COPY;
   }

   public static boolean isStatefulPassivationOperation() {
      return state() == IntraVmCopyMonitor.State.PASSIVATION;
   }

   public static boolean isCrossClassLoaderOperation() {
      return state() == IntraVmCopyMonitor.State.CLASSLOADER_COPY;
   }

   public static enum State {
      NONE,
      COPY(true),
      CLASSLOADER_COPY(true),
      PASSIVATION;

      private final boolean copy;

      private State() {
         this.copy = false;
      }

      private State(boolean copy) {
         this.copy = copy;
      }

      public boolean isCopy() {
         return this.copy;
      }
   }
}
