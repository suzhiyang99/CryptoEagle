package org.apache.manifoldcf.core.interfaces;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class NullCharacterInput extends CharacterInput {
   public static final String _rcsid = "@(#)$Id$";

   public Reader getStream() throws ManifoldCFException {
      return new StringReader("");
   }

   public void doneWithStream() throws ManifoldCFException {
   }

   public long getCharacterLength() throws ManifoldCFException {
      return 0L;
   }

   public String getHashValue() throws ManifoldCFException {
      return ManifoldCF.getHashValue(ManifoldCF.startHash());
   }

   public InputStream getUtf8Stream() throws ManifoldCFException {
      return new ByteArrayInputStream(new byte[0]);
   }

   public long getUtf8StreamLength() throws ManifoldCFException {
      return 0L;
   }

   public CharacterInput transfer() {
      return new NullCharacterInput();
   }

   public void discard() throws ManifoldCFException {
   }

   protected void openStream() throws ManifoldCFException {
   }

   protected void closeStream() throws ManifoldCFException {
   }

   protected void calculateLength() throws ManifoldCFException {
   }

   protected void calculateHashValue() throws ManifoldCFException {
   }
}
