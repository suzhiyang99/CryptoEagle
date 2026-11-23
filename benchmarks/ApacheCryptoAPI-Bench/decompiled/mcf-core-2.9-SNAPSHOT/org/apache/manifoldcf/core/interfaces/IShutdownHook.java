package org.apache.manifoldcf.core.interfaces;

public interface IShutdownHook {
   void doCleanup(IThreadContext var1) throws ManifoldCFException;
}
