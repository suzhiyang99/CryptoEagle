package org.apache.manifoldcf.core;

import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.ThreadContextFactory;
import org.apache.manifoldcf.core.system.ManifoldCF;

public abstract class DBInitializationCommand implements InitializationCommand {
   private final String userName;
   private final String password;

   public DBInitializationCommand(String userName, String password) {
      this.userName = userName;
      this.password = password;
   }

   public void execute() throws ManifoldCFException {
      IThreadContext tc = ThreadContextFactory.make();
      ManifoldCF.initializeEnvironment(tc);
      this.doExecute(tc);
   }

   protected abstract void doExecute(IThreadContext var1) throws ManifoldCFException;

   protected String getPassword() {
      return this.password;
   }

   protected String getUserName() {
      return this.userName;
   }
}
