package org.apache.openejb.assembler.classic.cmd;

import java.io.File;
import org.apache.openejb.assembler.classic.OpenEjbConfiguration;

public interface ConfigurationInfo {
   OpenEjbConfiguration getOpenEjbConfiguration(File var1) throws UnauthorizedException;

   public static class UnauthorizedException extends Exception {
      public UnauthorizedException(String message) {
         super(message);
      }
   }
}
