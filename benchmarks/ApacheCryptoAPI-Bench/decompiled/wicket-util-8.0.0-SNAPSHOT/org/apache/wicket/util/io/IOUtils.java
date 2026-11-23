package org.apache.wicket.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IOUtils {
   private static final Logger log = LoggerFactory.getLogger(IOUtils.class);
   private static final int DEFAULT_BUFFER_SIZE = 4096;

   public static void close(Closeable closeable) throws IOException {
      if (closeable != null) {
         closeable.close();
      }

   }

   public static void closeQuietly(Closeable closeable) {
      try {
         close(closeable);
      } catch (IOException e) {
         log.debug("closing resource failed: " + e.getMessage(), e);
      }

   }

   public static byte[] toByteArray(InputStream input) throws IOException {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      copy((InputStream)input, (OutputStream)output);
      return output.toByteArray();
   }

   public static byte[] toByteArray(Reader input) throws IOException {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      copy((Reader)input, (OutputStream)output);
      return output.toByteArray();
   }

   public static byte[] toByteArray(Reader input, String encoding) throws IOException {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      copy((Reader)input, (OutputStream)output, encoding);
      return output.toByteArray();
   }

   public static char[] toCharArray(InputStream is) throws IOException {
      CharArrayWriter output = new CharArrayWriter();
      copy((InputStream)is, (Writer)output);
      return output.toCharArray();
   }

   public static char[] toCharArray(InputStream is, String encoding) throws IOException {
      CharArrayWriter output = new CharArrayWriter();
      copy((InputStream)is, (Writer)output, encoding);
      return output.toCharArray();
   }

   public static char[] toCharArray(Reader input) throws IOException {
      CharArrayWriter sw = new CharArrayWriter();
      copy((Reader)input, (Writer)sw);
      return sw.toCharArray();
   }

   public static String toString(InputStream input) throws IOException {
      StringWriter sw = new StringWriter();
      copy((InputStream)input, (Writer)sw);
      return sw.toString();
   }

   public static String toString(InputStream input, String encoding) throws IOException {
      StringWriter sw = new StringWriter();
      copy((InputStream)input, (Writer)sw, encoding);
      return sw.toString();
   }

   public static String toString(Reader input) throws IOException {
      StringWriter sw = new StringWriter();
      copy((Reader)input, (Writer)sw);
      return sw.toString();
   }

   public static void write(byte[] data, OutputStream output) throws IOException {
      if (data != null) {
         output.write(data);
      }

   }

   public static void write(byte[] data, Writer output) throws IOException {
      if (data != null) {
         output.write(new String(data));
      }

   }

   public static void write(byte[] data, Writer output, String encoding) throws IOException {
      if (data != null) {
         if (encoding == null) {
            write(data, output);
         } else {
            output.write(new String(data, encoding));
         }
      }

   }

   public static void write(char[] data, Writer output) throws IOException {
      if (data != null) {
         output.write(data);
      }

   }

   public static void write(char[] data, OutputStream output) throws IOException {
      if (data != null) {
         output.write((new String(data)).getBytes());
      }

   }

   public static void write(char[] data, OutputStream output, String encoding) throws IOException {
      if (data != null) {
         if (encoding == null) {
            write(data, output);
         } else {
            output.write((new String(data)).getBytes(encoding));
         }
      }

   }

   public static void write(String data, Writer output) throws IOException {
      if (data != null) {
         output.write(data);
      }

   }

   public static void write(String data, OutputStream output) throws IOException {
      if (data != null) {
         output.write(data.getBytes());
      }

   }

   public static void write(String data, OutputStream output, String encoding) throws IOException {
      if (data != null) {
         if (encoding == null) {
            write(data, output);
         } else {
            output.write(data.getBytes(encoding));
         }
      }

   }

   public static void write(StringBuilder data, Writer output) throws IOException {
      if (data != null) {
         output.write(data.toString());
      }

   }

   public static void write(StringBuilder data, OutputStream output) throws IOException {
      if (data != null) {
         output.write(data.toString().getBytes());
      }

   }

   public static void write(StringBuilder data, OutputStream output, String encoding) throws IOException {
      if (data != null) {
         if (encoding == null) {
            write(data, output);
         } else {
            output.write(data.toString().getBytes(encoding));
         }
      }

   }

   public static int copy(InputStream input, OutputStream output) throws IOException {
      byte[] buffer = new byte[4096];
      int count = 0;

      int n;
      for(n = 0; -1 != (n = input.read(buffer)); count += n) {
         output.write(buffer, 0, n);
      }

      return count;
   }

   public static void copy(InputStream input, Writer output) throws IOException {
      InputStreamReader in = new InputStreamReader(input);
      copy((Reader)in, (Writer)output);
   }

   public static void copy(InputStream input, Writer output, String encoding) throws IOException {
      if (encoding == null) {
         copy(input, output);
      } else {
         InputStreamReader in = new InputStreamReader(input, encoding);
         copy((Reader)in, (Writer)output);
      }

   }

   public static int copy(Reader input, Writer output) throws IOException {
      char[] buffer = new char[4096];
      int count = 0;

      int n;
      for(n = 0; -1 != (n = input.read(buffer)); count += n) {
         output.write(buffer, 0, n);
      }

      return count;
   }

   public static void copy(Reader input, OutputStream output) throws IOException {
      OutputStreamWriter out = new OutputStreamWriter(output);
      copy((Reader)input, (Writer)out);
      out.flush();
   }

   public static void copy(Reader input, OutputStream output, String encoding) throws IOException {
      if (encoding == null) {
         copy(input, output);
      } else {
         OutputStreamWriter out = new OutputStreamWriter(output, encoding);
         copy((Reader)input, (Writer)out);
         out.flush();
      }

   }

   public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
      if (!(input1 instanceof BufferedInputStream)) {
         input1 = new BufferedInputStream(input1);
      }

      if (!(input2 instanceof BufferedInputStream)) {
         input2 = new BufferedInputStream(input2);
      }

      for(int ch = input1.read(); -1 != ch; ch = input1.read()) {
         int ch2 = input2.read();
         if (ch != ch2) {
            return false;
         }
      }

      int ch2 = input2.read();
      return ch2 == -1;
   }

   public static boolean contentEquals(Reader input1, Reader input2) throws IOException {
      if (!(input1 instanceof BufferedReader)) {
         input1 = new BufferedReader(input1);
      }

      if (!(input2 instanceof BufferedReader)) {
         input2 = new BufferedReader(input2);
      }

      for(int ch = input1.read(); -1 != ch; ch = input1.read()) {
         int ch2 = input2.read();
         if (ch != ch2) {
            return false;
         }
      }

      int ch2 = input2.read();
      return ch2 == -1;
   }
}
