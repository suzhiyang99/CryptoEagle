package org.apache.manifoldcf.core.database;

import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class DBInterfaceMariaDB extends DBInterfaceMySQL {
   private static final String _driver = "org.mariadb.jdbc.Driver";

   public DBInterfaceMariaDB(IThreadContext tc, String databaseName, String userName, String password) throws ManifoldCFException {
      super(tc, "org.mariadb.jdbc.Driver", databaseName, userName, password);
   }

   protected String getJdbcDriverClass() {
      return "org.mariadb.jdbc.Driver";
   }
}
