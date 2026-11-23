package org.apache.directory.server.kerberos.changepwd.messages;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.kerberos.changepwd.exceptions.ChangePasswdErrorType;
import org.apache.directory.server.kerberos.changepwd.exceptions.ChangePasswordException;
import org.apache.directory.shared.kerberos.codec.krbError.KrbErrorContainer;
import org.apache.directory.shared.kerberos.messages.KrbError;

public class ChangePasswordError extends AbstractPasswordMessage {
   private KrbError krbError;
   private short krbErrorLen;
   private short messageLength;

   public ChangePasswordError(KrbError krbError) {
      this((short)-128, krbError);
   }

   public ChangePasswordError(short versionNumber, KrbError krbError) {
      super(versionNumber);
      this.krbError = krbError;
   }

   public KrbError getKrbError() {
      return this.krbError;
   }

   public short computeLength() {
      this.krbErrorLen = (short)this.krbError.computeLength();
      this.messageLength = (short)(6 + this.krbErrorLen);
      return this.messageLength;
   }

   public ByteBuffer encode(ByteBuffer buf) throws EncoderException {
      buf.putShort(this.messageLength);
      buf.putShort(this.getVersionNumber());
      buf.putShort((short)0);
      this.krbError.encode(buf);
      return buf;
   }

   public static ChangePasswordError decode(ByteBuffer buf) throws ChangePasswordException {
      short messageLength = buf.getShort();
      short pvno = buf.getShort();
      buf.getShort();
      int errorLength = messageLength - 6;
      byte[] errorBytes = new byte[errorLength];
      buf.get(errorBytes);
      ByteBuffer errorBuffer = ByteBuffer.wrap(errorBytes);
      KrbErrorContainer container = new KrbErrorContainer(errorBuffer);
      Asn1Decoder decoder = new Asn1Decoder();

      try {
         decoder.decode(errorBuffer, container);
      } catch (DecoderException e) {
         throw new ChangePasswordException(ChangePasswdErrorType.KRB5_KPASSWD_MALFORMED, e);
      }

      KrbError errorMessage = container.getKrbError();
      return new ChangePasswordError(pvno, errorMessage);
   }

   public ChangePasswdErrorType getResultCode() {
      ByteBuffer buf = ByteBuffer.wrap(this.krbError.getEData());
      return ChangePasswdErrorType.getTypeByValue(buf.getShort());
   }

   public String getResultString() {
      byte[] edata = this.krbError.getEData();
      return Strings.utf8ToString(edata, 2, edata.length - 2);
   }
}
