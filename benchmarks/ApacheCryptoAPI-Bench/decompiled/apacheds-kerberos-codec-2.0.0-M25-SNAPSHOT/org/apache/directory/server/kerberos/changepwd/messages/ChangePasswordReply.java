package org.apache.directory.server.kerberos.changepwd.messages;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.server.kerberos.changepwd.exceptions.ChangePasswdErrorType;
import org.apache.directory.server.kerberos.changepwd.exceptions.ChangePasswordException;
import org.apache.directory.shared.kerberos.codec.KerberosDecoder;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;
import org.apache.directory.shared.kerberos.messages.ApRep;
import org.apache.directory.shared.kerberos.messages.KrbPriv;

public class ChangePasswordReply extends AbstractPasswordMessage {
   private ApRep applicationReply;
   private KrbPriv privateMessage;
   private short applicationReplyLen;
   private short privateMessageLen;
   private short messageLength;

   public ChangePasswordReply(ApRep applicationReply, KrbPriv privateMessage) {
      this((short)-128, applicationReply, privateMessage);
   }

   public ChangePasswordReply(short versionNumber, ApRep applicationReply, KrbPriv privateMessage) {
      super(versionNumber);
      this.applicationReply = applicationReply;
      this.privateMessage = privateMessage;
   }

   public ApRep getApplicationReply() {
      return this.applicationReply;
   }

   public KrbPriv getPrivateMessage() {
      return this.privateMessage;
   }

   public short computeLength() {
      this.applicationReplyLen = (short)this.applicationReply.computeLength();
      this.privateMessageLen = (short)this.privateMessage.computeLength();
      this.messageLength = (short)(6 + this.applicationReplyLen + this.privateMessageLen);
      return this.messageLength;
   }

   public ByteBuffer encode(ByteBuffer buf) throws EncoderException {
      buf.putShort(this.messageLength);
      buf.putShort(this.getVersionNumber());
      buf.putShort(this.applicationReplyLen);
      this.applicationReply.encode(buf);
      this.privateMessage.encode(buf);
      return buf;
   }

   public static ChangePasswordReply decode(ByteBuffer buf) throws ChangePasswordException {
      try {
         short messageLength = buf.getShort();
         short protocolVersion = buf.getShort();
         short encodedAppReplyLength = buf.getShort();
         byte[] encodedAppReply = new byte[encodedAppReplyLength];
         buf.get(encodedAppReply);
         ApRep applicationReply = KerberosDecoder.decodeApRep(encodedAppReply);
         int privateBytesLength = messageLength - 6 - encodedAppReplyLength;
         byte[] encodedPrivateMessage = new byte[privateBytesLength];
         buf.get(encodedPrivateMessage);
         KrbPriv privateMessage = KerberosDecoder.decodeKrbPriv(encodedPrivateMessage);
         return new ChangePasswordReply(protocolVersion, applicationReply, privateMessage);
      } catch (KerberosException e) {
         throw new ChangePasswordException(ChangePasswdErrorType.KRB5_KPASSWD_MALFORMED, e);
      }
   }
}
