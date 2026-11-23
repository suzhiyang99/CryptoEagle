package org.apache.manifoldcf.core.interfaces;

public interface IServiceCleanup {
   String _rcsid = "@(#)$Id$";

   void cleanUpService(String var1) throws ManifoldCFException;

   void cleanUpAllServices() throws ManifoldCFException;

   void clusterInit() throws ManifoldCFException;
}
