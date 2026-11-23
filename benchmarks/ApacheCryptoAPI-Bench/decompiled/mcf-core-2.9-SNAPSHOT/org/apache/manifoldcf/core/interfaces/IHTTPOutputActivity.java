package org.apache.manifoldcf.core.interfaces;

import java.io.IOException;

public interface IHTTPOutputActivity {
   String _rcsid = "@(#)$Id$";

   void flush() throws IOException;

   void newLine() throws IOException;

   void print(boolean var1) throws IOException;

   void print(char var1) throws IOException;

   void print(char[] var1) throws IOException;

   void print(double var1) throws IOException;

   void print(float var1) throws IOException;

   void print(int var1) throws IOException;

   void print(long var1) throws IOException;

   void print(Object var1) throws IOException;

   void print(String var1) throws IOException;

   void println(boolean var1) throws IOException;

   void println(char var1) throws IOException;

   void println(char[] var1) throws IOException;

   void println(double var1) throws IOException;

   void println(float var1) throws IOException;

   void println(int var1) throws IOException;

   void println(long var1) throws IOException;

   void println(Object var1) throws IOException;

   void println(String var1) throws IOException;
}
