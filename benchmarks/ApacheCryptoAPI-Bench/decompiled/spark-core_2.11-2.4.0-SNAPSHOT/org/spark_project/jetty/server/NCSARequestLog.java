package org.spark_project.jetty.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.TimeZone;
import org.spark_project.jetty.util.RolloverFileOutputStream;
import org.spark_project.jetty.util.StringUtil;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;

@ManagedObject("NCSA standard format request log")
public class NCSARequestLog extends AbstractNCSARequestLog {
   private String _filename;
   private boolean _append;
   private int _retainDays;
   private boolean _closeOut;
   private String _filenameDateFormat = null;
   private transient OutputStream _out;
   private transient OutputStream _fileOut;
   private transient Writer _writer;

   public NCSARequestLog() {
      this.setExtended(true);
      this._append = true;
      this._retainDays = 31;
   }

   public NCSARequestLog(String filename) {
      this.setExtended(true);
      this._append = true;
      this._retainDays = 31;
      this.setFilename(filename);
   }

   public void setFilename(String filename) {
      if (filename != null) {
         filename = filename.trim();
         if (filename.length() == 0) {
            filename = null;
         }
      }

      this._filename = filename;
   }

   @ManagedAttribute("file of log")
   public String getFilename() {
      return this._filename;
   }

   public String getDatedFilename() {
      return this._fileOut instanceof RolloverFileOutputStream ? ((RolloverFileOutputStream)this._fileOut).getDatedFilename() : null;
   }

   protected boolean isEnabled() {
      return this._fileOut != null;
   }

   public void setRetainDays(int retainDays) {
      this._retainDays = retainDays;
   }

   @ManagedAttribute("number of days that log files are kept")
   public int getRetainDays() {
      return this._retainDays;
   }

   public void setAppend(boolean append) {
      this._append = append;
   }

   @ManagedAttribute("existing log files are appends to the new one")
   public boolean isAppend() {
      return this._append;
   }

   public void setFilenameDateFormat(String logFileDateFormat) {
      this._filenameDateFormat = logFileDateFormat;
   }

   public String getFilenameDateFormat() {
      return this._filenameDateFormat;
   }

   public void write(String requestEntry) throws IOException {
      synchronized(this) {
         if (this._writer != null) {
            this._writer.write(requestEntry);
            this._writer.write(StringUtil.__LINE_SEPARATOR);
            this._writer.flush();
         }
      }
   }

   protected synchronized void doStart() throws Exception {
      if (this._filename != null) {
         this._fileOut = new RolloverFileOutputStream(this._filename, this._append, this._retainDays, TimeZone.getTimeZone(this.getLogTimeZone()), this._filenameDateFormat, (String)null);
         this._closeOut = true;
         LOG.info("Opened " + this.getDatedFilename());
      } else {
         this._fileOut = System.err;
      }

      this._out = this._fileOut;
      synchronized(this) {
         this._writer = new OutputStreamWriter(this._out);
      }

      super.doStart();
   }

   protected void doStop() throws Exception {
      synchronized(this) {
         super.doStop();

         try {
            if (this._writer != null) {
               this._writer.flush();
            }
         } catch (IOException e) {
            LOG.ignore(e);
         }

         if (this._out != null && this._closeOut) {
            try {
               this._out.close();
            } catch (IOException e) {
               LOG.ignore(e);
            }
         }

         this._out = null;
         this._fileOut = null;
         this._closeOut = false;
         this._writer = null;
      }
   }
}
