package org.apache.manifoldcf.core.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.Reader;

public abstract class CharacterInput extends PersistentDatabaseObject {
   public static final String _rcsid = "@(#)$Id: CharacterInput.java 988245 2010-08-23 18:39:35Z kwright $";
   protected Reader stream = null;
   protected long charLength = -1L;
   protected String hashValue = null;

   public CharacterInput() {
      this.stream = null;
      this.charLength = -1L;
   }

   public Reader getStream() throws ManifoldCFException {
      if (this.stream == null) {
         this.openStream();
      }

      return this.stream;
   }

   public void doneWithStream() throws ManifoldCFException {
      if (this.stream != null) {
         this.closeStream();
      }

   }

   public long getCharacterLength() throws ManifoldCFException {
      if (this.charLength == -1L) {
         this.calculateLength();
      }

      return this.charLength;
   }

   public String getHashValue() throws ManifoldCFException {
      if (this.hashValue == null) {
         this.calculateHashValue();
      }

      return this.hashValue;
   }

   public abstract InputStream getUtf8Stream() throws ManifoldCFException;

   public abstract long getUtf8StreamLength() throws ManifoldCFException;

   public abstract CharacterInput transfer();

   public void discard() throws ManifoldCFException {
      this.doneWithStream();
   }

   protected abstract void openStream() throws ManifoldCFException;

   protected void closeStream() throws ManifoldCFException {
      try {
         this.stream.close();
         this.stream = null;
      } catch (IOException e) {
         handleIOException(e, "closing stream");
      }

   }

   protected abstract void calculateLength() throws ManifoldCFException;

   protected abstract void calculateHashValue() throws ManifoldCFException;

   protected static void handleIOException(IOException e, String context) throws ManifoldCFException {
      if (e instanceof InterruptedIOException) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      } else {
         throw new ManifoldCFException("IO exception while " + context + ": " + e.getMessage(), e);
      }
   }
}
