package org.apache.manifoldcf.core;

import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.system.Logging;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class DBCreate extends DBInitializationCommand {
   public static final String _rcsid = "@(#)$Id: DBCreate.java 988245 2010-08-23 18:39:35Z kwright $";

   public DBCreate(String userName, String password) {
      super(userName, password);
   }

   public void doExecute(IThreadContext tc) throws ManifoldCFException {
      ManifoldCF.createSystemDatabase(tc, this.getUserName(), this.getPassword());
      Logging.root.info("ManifoldCF database created");
   }

   public static void main(String[] args) {
      if (args.length != 1 && args.length != 2) {
         System.err.println("Usage: DBCreate <dbuser> [<dbpassword>]");
         System.exit(1);
      }

      String userName = args[0];
      String password = "";
      if (args.length == 2) {
         password = args[1];
      }

      DBCreate dbCreate = new DBCreate(userName, password);

      try {
         dbCreate.execute();
         System.err.println("ManifoldCF database created");
      } catch (ManifoldCFException e) {
         e.printStackTrace();
         System.exit(1);
      }

   }
}
