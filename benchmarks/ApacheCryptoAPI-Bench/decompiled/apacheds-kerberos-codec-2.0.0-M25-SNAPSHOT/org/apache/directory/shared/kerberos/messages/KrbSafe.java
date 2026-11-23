package org.apache.directory.shared.kerberos.messages;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.components.Checksum;
import org.apache.directory.shared.kerberos.components.KrbSafeBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrbSafe extends KerberosMessage {
   private static final Logger log = LoggerFactory.getLogger(KrbError.class);
   private static final boolean IS_DEBUG;
   private KrbSafeBody krbSafeBody;
   private Checksum checksum;
   private int pvnoLen;
   private int msgTypeLength;
   private int krbSafeBodyLen;
   private int checksumLen;
   private int krbSafeSeqLen;
   private int krbSafeLen;

   public KrbSafe() {
      super(KerberosMessageType.KRB_SAFE);
   }

   public KrbSafeBody getSafeBody() {
      return this.krbSafeBody;
   }

   public void setSafeBody(KrbSafeBody safeBody) {
      this.krbSafeBody = safeBody;
   }

   public Checksum getChecksum() {
      return this.checksum;
   }

   public void setChecksum(Checksum checksum) {
      this.checksum = checksum;
   }

   public int computeLength() {
      this.pvnoLen = 3;
      this.krbSafeSeqLen = 1 + TLV.getNbBytes(this.pvnoLen) + this.pvnoLen;
      this.msgTypeLength = 2 + BerValue.getNbBytes(this.getMessageType().getValue());
      this.krbSafeSeqLen += 1 + TLV.getNbBytes(this.msgTypeLength) + this.msgTypeLength;
      this.krbSafeBodyLen = this.krbSafeBody.computeLength();
      this.krbSafeSeqLen += 1 + TLV.getNbBytes(this.krbSafeBodyLen) + this.krbSafeBodyLen;
      this.checksumLen = this.checksum.computeLength();
      this.krbSafeSeqLen += 1 + TLV.getNbBytes(this.checksumLen) + this.checksumLen;
      this.krbSafeLen = 1 + TLV.getNbBytes(this.krbSafeSeqLen) + this.krbSafeSeqLen;
      return 1 + TLV.getNbBytes(this.krbSafeLen) + this.krbSafeLen;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put((byte)116);
            buffer.put(TLV.getBytes(this.krbSafeLen));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.krbSafeSeqLen));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.pvnoLen));
            BerValue.encode(buffer, this.getProtocolVersionNumber());
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.msgTypeLength));
            BerValue.encode(buffer, this.getMessageType().getValue());
            buffer.put((byte)-94);
            buffer.put(TLV.getBytes(this.krbSafeBodyLen));
            this.krbSafeBody.encode(buffer);
            buffer.put((byte)-93);
            buffer.put(TLV.getBytes(this.checksumLen));
            this.checksum.encode(buffer);
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_736_CANNOT_ENCODE_KRBSAFE, new Object[]{1 + TLV.getNbBytes(this.krbSafeLen) + this.krbSafeLen, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("KrbSafe encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("KrbSafe initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("KRB-SAFE : {\n");
      sb.append("    pvno: ").append(this.getProtocolVersionNumber()).append('\n');
      sb.append("    msgType: ").append(this.getMessageType()).append('\n');
      if (this.krbSafeBody != null) {
         sb.append("    safe-body: ").append(this.krbSafeBody).append('\n');
      }

      if (this.checksum != null) {
         sb.append("    cusec: ").append(this.checksum).append('\n');
      }

      sb.append("}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
