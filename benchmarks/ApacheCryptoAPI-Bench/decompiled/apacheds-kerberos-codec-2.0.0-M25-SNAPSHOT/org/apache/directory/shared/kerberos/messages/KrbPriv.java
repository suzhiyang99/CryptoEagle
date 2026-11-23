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
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrbPriv extends KerberosMessage {
   private static final Logger log = LoggerFactory.getLogger(KrbError.class);
   private static final boolean IS_DEBUG;
   private EncryptedData encPart;
   private int pvnoLen;
   private int msgTypeLength;
   private int encPartLen;
   private int krbPrivSeqLen;
   private int krbPrivLen;

   public KrbPriv() {
      super(5, KerberosMessageType.KRB_PRIV);
   }

   public EncryptedData getEncPart() {
      return this.encPart;
   }

   public void setEncPart(EncryptedData encPart) {
      this.encPart = encPart;
   }

   public int computeLength() {
      this.pvnoLen = 3;
      this.krbPrivSeqLen = 1 + TLV.getNbBytes(this.pvnoLen) + this.pvnoLen;
      this.msgTypeLength = 2 + BerValue.getNbBytes(this.getMessageType().getValue());
      this.krbPrivSeqLen += 1 + TLV.getNbBytes(this.msgTypeLength) + this.msgTypeLength;
      this.encPartLen = this.encPart.computeLength();
      this.krbPrivSeqLen += 1 + TLV.getNbBytes(this.encPartLen) + this.encPartLen;
      this.krbPrivLen += 1 + TLV.getNbBytes(this.krbPrivSeqLen) + this.krbPrivSeqLen;
      return 1 + TLV.getNbBytes(this.krbPrivLen) + this.krbPrivLen;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put((byte)117);
            buffer.put(TLV.getBytes(this.krbPrivLen));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.krbPrivSeqLen));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.pvnoLen));
            BerValue.encode(buffer, this.getProtocolVersionNumber());
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.msgTypeLength));
            BerValue.encode(buffer, this.getMessageType().getValue());
            buffer.put((byte)-93);
            buffer.put(TLV.getBytes(this.encPartLen));
            this.encPart.encode(buffer);
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_738_CANNOT_ENCODE_KRB_PRIV, new Object[]{1 + TLV.getNbBytes(this.krbPrivLen) + this.krbPrivLen, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("KrbPriv encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("KrbPriv initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("KRB-PRIV : {\n");
      sb.append("    pvno: ").append(this.getProtocolVersionNumber()).append('\n');
      sb.append("    msgType: ").append(this.getMessageType()).append('\n');
      sb.append("    msgType: ").append(this.getEncPart()).append('\n');
      sb.append("}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
