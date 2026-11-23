package org.apache.manifoldcf.core;

import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public interface InitializationCommand {
   void execute() throws ManifoldCFException;
}
