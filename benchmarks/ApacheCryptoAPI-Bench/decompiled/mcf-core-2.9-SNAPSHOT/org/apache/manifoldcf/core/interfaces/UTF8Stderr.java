package org.apache.manifoldcf.core.interfaces;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class UTF8Stderr {
   public static final String _rcsid = "@(#)$Id: UTF8Stderr.java 988245 2010-08-23 18:39:35Z kwright $";
   private static PrintWriter err;

   private UTF8Stderr() {
   }

   public static void close() {
      err.close();
   }

   public static void println() {
      err.println();
   }

   public static void println(Object x) {
      err.println(x);
   }

   public static void println(boolean x) {
      err.println(x);
   }

   public static void println(char x) {
      err.println(x);
   }

   public static void println(double x) {
      err.println(x);
   }

   public static void println(float x) {
      err.println(x);
   }

   public static void println(int x) {
      err.println(x);
   }

   public static void println(long x) {
      err.println(x);
   }

   public static void print() {
      err.flush();
   }

   public static void print(Object x) {
      err.print(x);
      err.flush();
   }

   public static void print(boolean x) {
      err.print(x);
      err.flush();
   }

   public static void print(char x) {
      err.print(x);
      err.flush();
   }

   public static void print(double x) {
      err.print(x);
      err.flush();
   }

   public static void print(float x) {
      err.print(x);
      err.flush();
   }

   public static void print(int x) {
      err.print(x);
      err.flush();
   }

   public static void print(long x) {
      err.print(x);
      err.flush();
   }

   public static void printStackTrace(Throwable e) {
      e.printStackTrace(err);
   }

   static {
      err = new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8), true);
   }
}
