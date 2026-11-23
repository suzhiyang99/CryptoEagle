package org.spark_project.jetty.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class RolloverFileOutputStream extends FilterOutputStream {
   private static Timer __rollover;
   static final String YYYY_MM_DD = "yyyy_mm_dd";
   static final String ROLLOVER_FILE_DATE_FORMAT = "yyyy_MM_dd";
   static final String ROLLOVER_FILE_BACKUP_FORMAT = "HHmmssSSS";
   static final int ROLLOVER_FILE_RETAIN_DAYS = 31;
   private RollTask _rollTask;
   private SimpleDateFormat _fileBackupFormat;
   private SimpleDateFormat _fileDateFormat;
   private String _filename;
   private File _file;
   private boolean _append;
   private int _retainDays;

   public RolloverFileOutputStream(String filename) throws IOException {
      this(filename, true, 31);
   }

   public RolloverFileOutputStream(String filename, boolean append) throws IOException {
      this(filename, append, 31);
   }

   public RolloverFileOutputStream(String filename, boolean append, int retainDays) throws IOException {
      this(filename, append, retainDays, TimeZone.getDefault());
   }

   public RolloverFileOutputStream(String filename, boolean append, int retainDays, TimeZone zone) throws IOException {
      this(filename, append, retainDays, zone, (String)null, (String)null, ZonedDateTime.now(zone.toZoneId()));
   }

   public RolloverFileOutputStream(String filename, boolean append, int retainDays, TimeZone zone, String dateFormat, String backupFormat) throws IOException {
      this(filename, append, retainDays, zone, dateFormat, backupFormat, ZonedDateTime.now(zone.toZoneId()));
   }

   RolloverFileOutputStream(String filename, boolean append, int retainDays, TimeZone zone, String dateFormat, String backupFormat, ZonedDateTime now) throws IOException {
      super((OutputStream)null);
      if (dateFormat == null) {
         dateFormat = "yyyy_MM_dd";
      }

      this._fileDateFormat = new SimpleDateFormat(dateFormat);
      if (backupFormat == null) {
         backupFormat = "HHmmssSSS";
      }

      this._fileBackupFormat = new SimpleDateFormat(backupFormat);
      this._fileBackupFormat.setTimeZone(zone);
      this._fileDateFormat.setTimeZone(zone);
      if (filename != null) {
         filename = filename.trim();
         if (filename.length() == 0) {
            filename = null;
         }
      }

      if (filename == null) {
         throw new IllegalArgumentException("Invalid filename");
      } else {
         this._filename = filename;
         this._append = append;
         this._retainDays = retainDays;
         synchronized(RolloverFileOutputStream.class) {
            if (__rollover == null) {
               __rollover = new Timer(RolloverFileOutputStream.class.getName(), true);
            }

            this.setFile(now);
            this.scheduleNextRollover(now);
         }
      }
   }

   public static ZonedDateTime toMidnight(ZonedDateTime now) {
      return now.toLocalDate().atStartOfDay(now.getZone()).plus(1L, ChronoUnit.DAYS);
   }

   private void scheduleNextRollover(ZonedDateTime now) {
      this._rollTask = new RollTask();
      ZonedDateTime midnight = toMidnight(now);
      long delay = midnight.toInstant().toEpochMilli() - now.toInstant().toEpochMilli();
      __rollover.schedule(this._rollTask, delay);
   }

   public String getFilename() {
      return this._filename;
   }

   public String getDatedFilename() {
      return this._file == null ? null : this._file.toString();
   }

   public int getRetainDays() {
      return this._retainDays;
   }

   synchronized void setFile(ZonedDateTime now) throws IOException {
      File file = new File(this._filename);
      this._filename = file.getCanonicalPath();
      file = new File(this._filename);
      File dir = new File(file.getParent());
      if (dir.isDirectory() && dir.canWrite()) {
         String filename = file.getName();
         int i = filename.toLowerCase(Locale.ENGLISH).indexOf("yyyy_mm_dd");
         if (i >= 0) {
            file = new File(dir, filename.substring(0, i) + this._fileDateFormat.format(new Date(now.toInstant().toEpochMilli())) + filename.substring(i + "yyyy_mm_dd".length()));
         }

         if (file.exists() && !file.canWrite()) {
            throw new IOException("Cannot write log file " + file);
         } else {
            if (this.out == null || !file.equals(this._file)) {
               this._file = file;
               if (!this._append && file.exists()) {
                  file.renameTo(new File(file.toString() + "." + this._fileBackupFormat.format(new Date(now.toInstant().toEpochMilli()))));
               }

               OutputStream oldOut = this.out;
               this.out = new FileOutputStream(file.toString(), this._append);
               if (oldOut != null) {
                  oldOut.close();
               }
            }

         }
      } else {
         throw new IOException("Cannot write log directory " + dir);
      }
   }

   void removeOldFiles(ZonedDateTime now) {
      if (this._retainDays > 0) {
         long expired = now.minus((long)this._retainDays, ChronoUnit.DAYS).toInstant().toEpochMilli();
         File file = new File(this._filename);
         File dir = new File(file.getParent());
         String fn = file.getName();
         int s = fn.toLowerCase(Locale.ENGLISH).indexOf("yyyy_mm_dd");
         if (s < 0) {
            return;
         }

         String prefix = fn.substring(0, s);
         String suffix = fn.substring(s + "yyyy_mm_dd".length());
         String[] logList = dir.list();

         for(int i = 0; i < logList.length; ++i) {
            fn = logList[i];
            if (fn.startsWith(prefix) && fn.indexOf(suffix, prefix.length()) >= 0) {
               File f = new File(dir, fn);
               if (f.lastModified() < expired) {
                  f.delete();
               }
            }
         }
      }

   }

   public void write(byte[] buf) throws IOException {
      this.out.write(buf);
   }

   public void write(byte[] buf, int off, int len) throws IOException {
      this.out.write(buf, off, len);
   }

   public void close() throws IOException {
      synchronized(RolloverFileOutputStream.class) {
         try {
            super.close();
         } finally {
            this.out = null;
            this._file = null;
         }

         if (this._rollTask != null) {
            this._rollTask.cancel();
         }

      }
   }

   private class RollTask extends TimerTask {
      private RollTask() {
      }

      public void run() {
         try {
            synchronized(RolloverFileOutputStream.class) {
               ZonedDateTime now = ZonedDateTime.now(RolloverFileOutputStream.this._fileDateFormat.getTimeZone().toZoneId());
               RolloverFileOutputStream.this.setFile(now);
               RolloverFileOutputStream.this.scheduleNextRollover(now);
               RolloverFileOutputStream.this.removeOldFiles(now);
            }
         } catch (Throwable t) {
            t.printStackTrace(System.err);
         }

      }
   }
}
