package org.apache.directory.server.kerberos.changepwd.messages;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.server.kerberos.changepwd.exceptions.ChangePasswdErrorType;
import org.apache.directory.server.kerberos.changepwd.exceptions.ChangePasswordException;
import org.apache.directory.shared.kerberos.codec.KerberosDecoder;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;
import org.apache.directory.shared.kerberos.messages.ApReq;
import org.apache.directory.shared.kerberos.messages.KrbPriv;

public class ChangePasswordRequest extends AbstractPasswordMessage {
   private ApReq authHeader;
   private KrbPriv privateMessage;
   private short authHeaderLen;
   private short privateMessageLen;
   private short messageLength;

   public ChangePasswordRequest(ApReq authHeader, KrbPriv privateMessage) {
      this((short)-128, authHeader, privateMessage);
   }

   public ChangePasswordRequest(short versionNumber, ApReq authHeader, KrbPriv privateMessage) {
      super(versionNumber);
      this.authHeader = authHeader;
      this.privateMessage = privateMessage;
   }

   public ApReq getAuthHeader() {
      return this.authHeader;
   }

   public KrbPriv getPrivateMessage() {
      return this.privateMessage;
   }

   public short computeLength() {
      this.authHeaderLen = (short)this.authHeader.computeLength();
      this.privateMessageLen = (short)this.privateMessage.computeLength();
      this.messageLength = (short)(6 + this.authHeaderLen + this.privateMessageLen);
      return this.messageLength;
   }

   public ByteBuffer encode(ByteBuffer buf) throws EncoderException {
      buf.putShort(this.messageLength);
      buf.putShort(this.getVersionNumber());
      buf.putShort(this.authHeaderLen);
      this.authHeader.encode(buf);
      this.privateMessage.encode(buf);
      return buf;
   }

   public static ChangePasswordRequest decode(ByteBuffer buf) throws ChangePasswordException {
      try {
         buf.getShort();
         short pvno = buf.getShort();
         short authHeaderLength = buf.getShort();
         byte[] undecodedAuthHeader = new byte[authHeaderLength];
         buf.get(undecodedAuthHeader, 0, authHeaderLength);
         ApReq authHeader = KerberosDecoder.decodeApReq(undecodedAuthHeader);
         byte[] encodedPrivate = new byte[buf.remaining()];
         buf.get(encodedPrivate, 0, buf.remaining());
         KrbPriv privMessage = KerberosDecoder.decodeKrbPriv(encodedPrivate);
         return new ChangePasswordRequest(pvno, authHeader, privMessage);
      } catch (KerberosException e) {
         throw new ChangePasswordException(ChangePasswdErrorType.KRB5_KPASSWD_MALFORMED, e);
      }
   }
}
