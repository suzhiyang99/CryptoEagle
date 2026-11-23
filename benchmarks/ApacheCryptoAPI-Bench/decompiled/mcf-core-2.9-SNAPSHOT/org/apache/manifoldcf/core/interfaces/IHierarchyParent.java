package org.apache.manifoldcf.core.interfaces;

public interface IHierarchyParent {
   String _rcsid = "@(#)$Id$";

   void clearChildren();

   int getChildCount();

   ConfigurationNode findChild(int var1);

   void removeChild(int var1);

   void addChild(int var1, ConfigurationNode var2);
}
