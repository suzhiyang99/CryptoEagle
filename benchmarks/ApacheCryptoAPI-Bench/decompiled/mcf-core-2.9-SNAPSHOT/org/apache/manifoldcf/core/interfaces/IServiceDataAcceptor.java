package org.apache.manifoldcf.core.interfaces;

public interface IServiceDataAcceptor {
   String _rcsid = "@(#)$Id$";

   boolean acceptServiceData(String var1, byte[] var2) throws ManifoldCFException;
}
