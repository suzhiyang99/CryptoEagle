package org.apache.manifoldcf.core.interfaces;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class UTF8Stdout {
   public static final String _rcsid = "@(#)$Id: UTF8Stdout.java 988245 2010-08-23 18:39:35Z kwright $";
   private static PrintWriter out;

   private UTF8Stdout() {
   }

   public static void close() {
      out.close();
   }

   public static void println() {
      out.println();
   }

   public static void println(Object x) {
      out.println(x);
   }

   public static void println(boolean x) {
      out.println(x);
   }

   public static void println(char x) {
      out.println(x);
   }

   public static void println(double x) {
      out.println(x);
   }

   public static void println(float x) {
      out.println(x);
   }

   public static void println(int x) {
      out.println(x);
   }

   public static void println(long x) {
      out.println(x);
   }

   public static void print() {
      out.flush();
   }

   public static void print(Object x) {
      out.print(x);
      out.flush();
   }

   public static void print(boolean x) {
      out.print(x);
      out.flush();
   }

   public static void print(char x) {
      out.print(x);
      out.flush();
   }

   public static void print(double x) {
      out.print(x);
      out.flush();
   }

   public static void print(float x) {
      out.print(x);
      out.flush();
   }

   public static void print(int x) {
      out.print(x);
      out.flush();
   }

   public static void print(long x) {
      out.print(x);
      out.flush();
   }

   public static void printStackTrace(Throwable e) {
      e.printStackTrace(out);
   }

   static {
      out = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);
   }
}
