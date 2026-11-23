package org.apache.openejb.cli;

public class SystemExitException extends Exception {
   private final int exitCode;

   public SystemExitException(int exitCode) {
      this.exitCode = exitCode;
   }

   public int getExitCode() {
      return this.exitCode;
   }
}
