package org.apache.openejb.util;

import java.io.PrintWriter;
import org.apache.log4j.Priority;

public class Log4jPrintWriter extends PrintWriter {
   private final StringBuffer text = new StringBuffer("");
   private final org.apache.log4j.Logger logger;
   private final Priority priority;

   public Log4jPrintWriter(String category, Priority priority) {
      super(System.err);
      this.logger = org.apache.log4j.Logger.getLogger(category);
      this.priority = priority;
   }

   public void close() {
      this.flush();
   }

   private void flushLine() {
      this.logger.log(this.priority, this.text.toString());
      this.text.setLength(0);
   }

   public void flush() {
      if (!this.text.toString().equals("")) {
         this.flushLine();
      }

   }

   public void print(boolean b) {
      this.text.append(b);
   }

   public void print(char c) {
      this.text.append(c);
   }

   public void print(char[] s) {
      this.text.append(s);
   }

   public void print(double d) {
      this.text.append(d);
   }

   public void print(float f) {
      this.text.append(f);
   }

   public void print(int i) {
      this.text.append(i);
   }

   public void print(long l) {
      this.text.append(l);
   }

   public void print(Object obj) {
      this.text.append(obj);
   }

   public void print(String s) {
      this.text.append(s);
   }

   public void println() {
      if (!this.text.toString().equals("")) {
         this.flushLine();
      }

   }

   public void println(boolean x) {
      this.text.append(x);
      this.flushLine();
   }

   public void println(char x) {
      this.text.append(x);
      this.flushLine();
   }

   public void println(char[] x) {
      this.text.append(x);
      this.flushLine();
   }

   public void println(double x) {
      this.text.append(x);
      this.flushLine();
   }

   public void println(float x) {
      this.text.append(x);
      this.flushLine();
   }

   public void println(int x) {
      this.text.append(x);
      this.flushLine();
   }

   public void println(long x) {
      this.text.append(x);
      this.flushLine();
   }

   public void println(Object x) {
      this.text.append(x);
      this.flushLine();
   }

   public void println(String x) {
      this.text.append(x);
      this.flushLine();
   }
}
