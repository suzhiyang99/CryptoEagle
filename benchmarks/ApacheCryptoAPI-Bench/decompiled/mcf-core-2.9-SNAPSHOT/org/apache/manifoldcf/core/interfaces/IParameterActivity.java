package org.apache.manifoldcf.core.interfaces;

public interface IParameterActivity {
   String _rcsid = "@(#)$Id$";

   String[] getParameterValues(String var1);

   String getParameter(String var1);

   BinaryInput getBinaryStream(String var1) throws ManifoldCFException;

   byte[] getBinaryBytes(String var1);

   void setParameter(String var1, String var2);

   void setParameterValues(String var1, String[] var2);
}
