package org.apache.manifoldcf.core.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public abstract class BinaryInput extends PersistentDatabaseObject {
   public static final String _rcsid = "@(#)$Id: BinaryInput.java 988245 2010-08-23 18:39:35Z kwright $";
   protected InputStream stream = null;
   protected long length = -1L;

   public InputStream getStream() throws ManifoldCFException {
      if (this.stream == null) {
         this.openStream();
      }

      return this.stream;
   }

   public long getLength() throws ManifoldCFException {
      if (this.length == -1L) {
         this.calculateLength();
      }

      return this.length;
   }

   public void doneWithStream() throws ManifoldCFException {
      if (this.stream != null) {
         this.closeStream();
      }

   }

   public abstract BinaryInput transfer();

   public void discard() throws ManifoldCFException {
      this.doneWithStream();
   }

   protected abstract void openStream() throws ManifoldCFException;

   protected abstract void calculateLength() throws ManifoldCFException;

   protected void closeStream() throws ManifoldCFException {
      try {
         this.stream.close();
         this.stream = null;
      } catch (IOException e) {
         handleIOException(e, "closing stream");
      }

   }

   protected static void handleIOException(IOException e, String context) throws ManifoldCFException {
      if (e instanceof InterruptedIOException) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      } else {
         throw new ManifoldCFException("IO exception while " + context + ": " + e.getMessage(), e);
      }
   }
}
