package org.apache.manifoldcf.core;

import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.system.Logging;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class DBDrop extends DBInitializationCommand {
   public static final String _rcsid = "@(#)$Id: DBDrop.java 988245 2010-08-23 18:39:35Z kwright $";

   public DBDrop(String userName, String password) {
      super(userName, password);
   }

   protected void doExecute(IThreadContext tc) throws ManifoldCFException {
      ManifoldCF.dropSystemDatabase(tc, this.getUserName(), this.getPassword());
      Logging.root.info("ManifoldCF database dropped");
   }

   public static void main(String[] args) {
      if (args.length != 1 && args.length != 2) {
         System.err.println("Usage: DBDrop <dbuser> [<dbpassword>]");
         System.exit(1);
      }

      String userName = args[0];
      String password = "";
      if (args.length == 2) {
         password = args[1];
      }

      DBDrop dbDrop = new DBDrop(userName, password);

      try {
         dbDrop.execute();
         System.err.println("ManifoldCF database dropped");
      } catch (ManifoldCFException e) {
         e.printStackTrace();
         System.exit(1);
      }

   }
}
