package org.apache.manifoldcf.core.interfaces;

import org.apache.manifoldcf.core.threadcontext.ThreadContext;

public class ThreadContextFactory {
   public static final String _rcsid = "@(#)$Id: ThreadContextFactory.java 988245 2010-08-23 18:39:35Z kwright $";

   private ThreadContextFactory() {
   }

   public static IThreadContext make() {
      return new ThreadContext();
   }
}
