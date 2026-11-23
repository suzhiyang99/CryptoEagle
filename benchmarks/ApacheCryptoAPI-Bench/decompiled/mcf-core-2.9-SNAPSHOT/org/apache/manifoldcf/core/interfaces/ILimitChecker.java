package org.apache.manifoldcf.core.interfaces;

public interface ILimitChecker {
   String _rcsid = "@(#)$Id: ILimitChecker.java 988245 2010-08-23 18:39:35Z kwright $";

   boolean doesCompareWork();

   ILimitChecker duplicate();

   int hashCode();

   boolean equals(Object var1);

   boolean checkInclude(IResultRow var1) throws ManifoldCFException;

   boolean checkContinue() throws ManifoldCFException;
}
