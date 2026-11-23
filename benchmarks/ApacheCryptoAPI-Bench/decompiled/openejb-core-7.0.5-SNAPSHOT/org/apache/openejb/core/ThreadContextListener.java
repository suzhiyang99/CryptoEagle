package org.apache.openejb.core;

public interface ThreadContextListener {
   void contextEntered(ThreadContext var1, ThreadContext var2);

   void contextExited(ThreadContext var1, ThreadContext var2);
}
