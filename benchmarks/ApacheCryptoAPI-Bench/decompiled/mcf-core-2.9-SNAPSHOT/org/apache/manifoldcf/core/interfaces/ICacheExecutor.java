package org.apache.manifoldcf.core.interfaces;

public interface ICacheExecutor {
   String _rcsid = "@(#)$Id: ICacheExecutor.java 988245 2010-08-23 18:39:35Z kwright $";

   Object[] create(ICacheDescription[] var1) throws ManifoldCFException;

   void exists(ICacheDescription var1, Object var2) throws ManifoldCFException;

   void execute() throws ManifoldCFException;
}
