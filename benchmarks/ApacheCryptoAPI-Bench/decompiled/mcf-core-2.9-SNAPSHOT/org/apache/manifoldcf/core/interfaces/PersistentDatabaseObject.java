package org.apache.manifoldcf.core.interfaces;

public abstract class PersistentDatabaseObject {
   public static final String _rcsid = "@(#)$Id$";

   public abstract void doneWithStream() throws ManifoldCFException;

   public abstract void discard() throws ManifoldCFException;
}
