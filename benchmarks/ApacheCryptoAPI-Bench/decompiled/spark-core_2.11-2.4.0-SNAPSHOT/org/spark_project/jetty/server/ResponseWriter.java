package org.spark_project.jetty.server;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.Locale;
import org.spark_project.jetty.io.EofException;
import org.spark_project.jetty.io.RuntimeIOException;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class ResponseWriter extends PrintWriter {
   private static final Logger LOG = Log.getLogger(ResponseWriter.class);
   private static final String __lineSeparator = System.getProperty("line.separator");
   private static final String __trueln;
   private static final String __falseln;
   private final HttpWriter _httpWriter;
   private final Locale _locale;
   private final String _encoding;
   private IOException _ioException;
   private boolean _isClosed = false;
   private Formatter _formatter;

   public ResponseWriter(HttpWriter httpWriter, Locale locale, String encoding) {
      super(httpWriter, false);
      this._httpWriter = httpWriter;
      this._locale = locale;
      this._encoding = encoding;
   }

   public boolean isFor(Locale locale, String encoding) {
      if (this._locale == null && locale != null) {
         return false;
      } else if (this._encoding == null && encoding != null) {
         return false;
      } else {
         return this._encoding.equalsIgnoreCase(encoding) && this._locale.equals(locale);
      }
   }

   protected void reopen() {
      synchronized(this.lock) {
         this._isClosed = false;
         this.clearError();
         this.out = this._httpWriter;
      }
   }

   protected void clearError() {
      synchronized(this.lock) {
         this._ioException = null;
         super.clearError();
      }
   }

   public boolean checkError() {
      synchronized(this.lock) {
         return this._ioException != null || super.checkError();
      }
   }

   private void setError(Throwable th) {
      super.setError();
      if (th instanceof IOException) {
         this._ioException = (IOException)th;
      } else {
         this._ioException = new IOException(String.valueOf(th));
         this._ioException.initCause(th);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug(th);
      }

   }

   protected void setError() {
      this.setError(new IOException());
   }

   private void isOpen() throws IOException {
      if (this._ioException != null) {
         throw new RuntimeIOException(this._ioException);
      } else if (this._isClosed) {
         throw new EofException("Stream closed");
      }
   }

   public void flush() {
      try {
         synchronized(this.lock) {
            this.isOpen();
            this.out.flush();
         }
      } catch (IOException ex) {
         this.setError(ex);
      }

   }

   public void close() {
      try {
         synchronized(this.lock) {
            this.out.close();
            this._isClosed = true;
         }
      } catch (IOException ex) {
         this.setError(ex);
      }

   }

   public void write(int c) {
      try {
         synchronized(this.lock) {
            this.isOpen();
            this.out.write(c);
         }
      } catch (InterruptedIOException ex) {
         LOG.debug(ex);
         Thread.currentThread().interrupt();
      } catch (IOException ex) {
         this.setError(ex);
      }

   }

   public void write(char[] buf, int off, int len) {
      try {
         synchronized(this.lock) {
            this.isOpen();
            this.out.write(buf, off, len);
         }
      } catch (InterruptedIOException ex) {
         LOG.debug(ex);
         Thread.currentThread().interrupt();
      } catch (IOException ex) {
         this.setError(ex);
      }

   }

   public void write(char[] buf) {
      this.write((char[])buf, 0, buf.length);
   }

   public void write(String s, int off, int len) {
      try {
         synchronized(this.lock) {
            this.isOpen();
            this.out.write(s, off, len);
         }
      } catch (InterruptedIOException ex) {
         LOG.debug(ex);
         Thread.currentThread().interrupt();
      } catch (IOException ex) {
         this.setError(ex);
      }

   }

   public void write(String s) {
      this.write((String)s, 0, s.length());
   }

   public void print(boolean b) {
      this.write(b ? "true" : "false");
   }

   public void print(char c) {
      this.write(c);
   }

   public void print(int i) {
      this.write(String.valueOf(i));
   }

   public void print(long l) {
      this.write(String.valueOf(l));
   }

   public void print(float f) {
      this.write(String.valueOf(f));
   }

   public void print(double d) {
      this.write(String.valueOf(d));
   }

   public void print(char[] s) {
      this.write(s);
   }

   public void print(String s) {
      if (s == null) {
         s = "null";
      }

      this.write(s);
   }

   public void print(Object obj) {
      this.write(String.valueOf(obj));
   }

   public void println() {
      try {
         synchronized(this.lock) {
            this.isOpen();
            this.out.write(__lineSeparator);
         }
      } catch (InterruptedIOException ex) {
         LOG.debug(ex);
         Thread.currentThread().interrupt();
      } catch (IOException ex) {
         this.setError(ex);
      }

   }

   public void println(boolean b) {
      this.println(b ? __trueln : __falseln);
   }

   public void println(char c) {
      try {
         synchronized(this.lock) {
            this.isOpen();
            this.out.write(c);
         }
      } catch (InterruptedIOException ex) {
         LOG.debug(ex);
         Thread.currentThread().interrupt();
      } catch (IOException ex) {
         this.setError(ex);
      }

   }

   public void println(int x) {
      this.println(String.valueOf(x));
   }

   public void println(long x) {
      this.println(String.valueOf(x));
   }

   public void println(float x) {
      this.println(String.valueOf(x));
   }

   public void println(double x) {
      this.println(String.valueOf(x));
   }

   public void println(char[] s) {
      try {
         synchronized(this.lock) {
            this.isOpen();
            this.out.write(s, 0, s.length);
            this.out.write(__lineSeparator);
         }
      } catch (InterruptedIOException ex) {
         LOG.debug(ex);
         Thread.currentThread().interrupt();
      } catch (IOException ex) {
         this.setError(ex);
      }

   }

   public void println(String s) {
      if (s == null) {
         s = "null";
      }

      try {
         synchronized(this.lock) {
            this.isOpen();
            this.out.write(s, 0, s.length());
            this.out.write(__lineSeparator);
         }
      } catch (InterruptedIOException ex) {
         LOG.debug(ex);
         Thread.currentThread().interrupt();
      } catch (IOException ex) {
         this.setError(ex);
      }

   }

   public void println(Object x) {
      this.println(String.valueOf(x));
   }

   public PrintWriter printf(String format, Object... args) {
      return this.format(this._locale, format, args);
   }

   public PrintWriter printf(Locale l, String format, Object... args) {
      return this.format(l, format, args);
   }

   public PrintWriter format(String format, Object... args) {
      return this.format(this._locale, format, args);
   }

   public PrintWriter format(Locale l, String format, Object... args) {
      try {
         synchronized(this.lock) {
            this.isOpen();
            if (this._formatter == null || this._formatter.locale() != l) {
               this._formatter = new Formatter(this, l);
            }

            this._formatter.format(l, format, args);
         }
      } catch (InterruptedIOException ex) {
         LOG.debug(ex);
         Thread.currentThread().interrupt();
      } catch (IOException ex) {
         this.setError(ex);
      }

      return this;
   }

   static {
      __trueln = "true" + __lineSeparator;
      __falseln = "false" + __lineSeparator;
   }
}
