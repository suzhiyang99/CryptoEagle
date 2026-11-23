package org.apache.manifoldcf.core.interfaces;

public interface IAuth {
   int CAPABILITY_VIEW_CONNECTIONS = 1;
   int CAPABILITY_VIEW_JOBS = 2;
   int CAPABILITY_VIEW_REPORTS = 3;
   int CAPABILITY_EDIT_CONNECTIONS = 4;
   int CAPABILITY_EDIT_JOBS = 5;
   int CAPABILITY_RUN_JOBS = 6;

   boolean verifyUILogin(String var1, String var2) throws ManifoldCFException;

   boolean verifyAPILogin(String var1, String var2) throws ManifoldCFException;

   boolean checkCapability(String var1, int var2) throws ManifoldCFException;
}
