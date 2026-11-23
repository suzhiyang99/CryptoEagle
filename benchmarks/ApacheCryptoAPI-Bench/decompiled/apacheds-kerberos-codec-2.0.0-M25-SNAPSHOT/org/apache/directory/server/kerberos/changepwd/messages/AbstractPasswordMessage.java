package org.apache.directory.server.kerberos.changepwd.messages;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.EncoderException;

public abstract class AbstractPasswordMessage {
   public static final short PVNO = -128;
   public static final short OLD_PVNO = 1;
   static final int HEADER_LENGTH = 6;
   private short versionNumber;

   protected AbstractPasswordMessage(short versionNumber) {
      this.versionNumber = versionNumber;
   }

   public short getVersionNumber() {
      return this.versionNumber;
   }

   public abstract short computeLength();

   public abstract ByteBuffer encode(ByteBuffer var1) throws EncoderException;
}
