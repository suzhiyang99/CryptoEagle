package org.apache.openejb.util;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public final class JndiTreeBrowser {
   private static final String ROOT = "";
   private final Context context;
   private final String path;

   private JndiTreeBrowser(Context ctx, String name) {
      this.path = name;
      this.context = ctx;
   }

   private JndiTreeBrowser(Context ctx) {
      this(ctx, "");
   }

   private JndiTreeBrowser(String name) throws NamingException {
      this(new InitialContext(), name);
   }

   private JndiTreeBrowser() throws NamingException {
      this(new InitialContext(), "");
   }

   private void runOnTree(JndiNodeWorker worker) throws NamingException {
      NamingEnumeration<Binding> ne = this.context.listBindings("");

      while(ne.hasMoreElements()) {
         Binding current = (Binding)ne.next();
         Object obj = current.getObject();
         worker.doWork(this.path, current.getName(), obj);
         if (obj instanceof Context) {
            this.runOnJndiTree((Context)obj, worker, this.path + '/' + current.getName());
         }
      }

   }

   private void runOnJndiTree(Context ctx, JndiNodeWorker worker, String prefix) throws NamingException {
      (new JndiTreeBrowser(ctx, prefix)).runOnTree(worker);
   }

   public static void runOnJndiTree(Context ctx, JndiNodeWorker worker) throws NamingException {
      (new JndiTreeBrowser(ctx)).runOnTree(worker);
   }

   public static void log(Context ctx) throws NamingException {
      (new JndiTreeBrowser(ctx)).runOnTree(new LogJndiWorker((String)null));
   }

   public static void log(Context ctx, String foo) throws NamingException {
      (new JndiTreeBrowser(ctx)).runOnTree(new LogJndiWorker(foo));
   }

   private static class LogJndiWorker implements JndiNodeWorker {
      private final String filter;

      public LogJndiWorker(String filter) {
         this.filter = filter;
      }

      public void doWork(String path, String name, Object obj) {
         String complete = path + "/" + name;
         if (this.filter == null || complete.contains(this.filter)) {
            System.out.println(complete);
         }

      }
   }

   private interface JndiNodeWorker {
      void doWork(String var1, String var2, Object var3);
   }
}
