package org.apache.openejb.core;

import java.io.PrintStream;
import java.io.PrintWriter;

public class NoSuchObjectException extends java.rmi.NoSuchObjectException {
   private Throwable cause = this;

   public NoSuchObjectException(String detailMessage) {
      super(detailMessage);
   }

   public NoSuchObjectException(String detailMessage, Throwable throwable) {
      super(detailMessage);
      this.cause = throwable;
   }

   public NoSuchObjectException(Throwable throwable) {
      super(throwable == null ? null : throwable.toString());
      this.cause = throwable;
   }

   public String getLocalizedMessage() {
      return this.getMessage();
   }

   public void printStackTrace() {
      this.printStackTrace(System.err);
   }

   private static int countDuplicates(StackTraceElement[] currentStack, StackTraceElement[] parentStack) {
      int duplicates = 0;
      int parentIndex = parentStack.length;
      int i = currentStack.length;

      while(true) {
         --i;
         if (i < 0) {
            break;
         }

         --parentIndex;
         if (parentIndex < 0) {
            break;
         }

         StackTraceElement parentFrame = parentStack[parentIndex];
         if (!parentFrame.equals(currentStack[i])) {
            break;
         }

         ++duplicates;
      }

      return duplicates;
   }

   public void printStackTrace(PrintStream err) {
      err.println(this.toString());
      StackTraceElement[] stack = this.getStackTrace();

      for(int i = 0; i < stack.length; ++i) {
         err.println("\tat " + stack[i]);
      }

      StackTraceElement[] parentStack = stack;

      for(Throwable throwable = this.getCause(); throwable != null; throwable = throwable.getCause()) {
         err.print("Caused by: ");
         err.println(throwable);
         StackTraceElement[] currentStack = throwable.getStackTrace();
         int duplicates = countDuplicates(currentStack, parentStack);

         for(int i = 0; i < currentStack.length - duplicates; ++i) {
            err.println("\tat " + currentStack[i]);
         }

         if (duplicates > 0) {
            err.println("\t... " + duplicates + " more");
         }

         parentStack = currentStack;
      }

   }

   public void printStackTrace(PrintWriter err) {
      err.println(this.toString());
      StackTraceElement[] stack = this.getStackTrace();

      for(int i = 0; i < stack.length; ++i) {
         err.println("\tat " + stack[i]);
      }

      StackTraceElement[] parentStack = stack;

      for(Throwable throwable = this.getCause(); throwable != null; throwable = throwable.getCause()) {
         err.print("Caused by: ");
         err.println(throwable);
         StackTraceElement[] currentStack = throwable.getStackTrace();
         int duplicates = countDuplicates(currentStack, parentStack);

         for(int i = 0; i < currentStack.length - duplicates; ++i) {
            err.println("\tat " + currentStack[i]);
         }

         if (duplicates > 0) {
            err.println("\t... " + duplicates + " more");
         }

         parentStack = currentStack;
      }

   }

   public String toString() {
      String msg = this.getLocalizedMessage();
      String name = this.getClass().getName();
      return msg == null ? name : (new StringBuilder(name.length() + 2 + msg.length())).append(name).append(": ").append(msg).toString();
   }

   public synchronized NoSuchObjectException initCause(Throwable throwable) {
      this.cause = throwable;
      return this;
   }

   public Throwable getCause() {
      return this.cause == this ? null : this.cause;
   }
}
