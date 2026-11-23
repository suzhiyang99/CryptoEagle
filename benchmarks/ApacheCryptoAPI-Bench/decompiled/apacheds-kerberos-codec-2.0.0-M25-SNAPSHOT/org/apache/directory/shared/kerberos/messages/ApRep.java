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

public class ApRep extends KerberosMessage {
   private static final Logger LOG = LoggerFactory.getLogger(ApRep.class);
   private static final boolean IS_DEBUG;
   private EncryptedData encPart;
   private int pvnoLength;
   private int msgTypeLength;
   private int encPartLength;
   private int apRepLength;
   private int apRepSeqLength;

   public ApRep() {
      super(KerberosMessageType.AP_REP);
   }

   public EncryptedData getEncPart() {
      return this.encPart;
   }

   public void setEncPart(EncryptedData encPart) {
      this.encPart = encPart;
   }

   public int computeLength() {
      this.pvnoLength = 2 + BerValue.getNbBytes(this.getProtocolVersionNumber());
      this.msgTypeLength = 2 + BerValue.getNbBytes(this.getMessageType().getValue());
      this.encPartLength = this.encPart.computeLength();
      this.apRepLength = 1 + TLV.getNbBytes(this.pvnoLength) + this.pvnoLength + 1 + TLV.getNbBytes(this.msgTypeLength) + this.msgTypeLength + 1 + TLV.getNbBytes(this.encPartLength) + this.encPartLength;
      this.apRepSeqLength = 1 + TLV.getNbBytes(this.apRepLength) + this.apRepLength;
      return 1 + TLV.getNbBytes(this.apRepSeqLength) + this.apRepSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         buffer = ByteBuffer.allocate(this.computeLength());
      }

      try {
         buffer.put((byte)111);
         buffer.put(TLV.getBytes(this.apRepSeqLength));
         buffer.put(UniversalTag.SEQUENCE.getValue());
         buffer.put(TLV.getBytes(this.apRepLength));
         buffer.put((byte)-96);
         buffer.put(TLV.getBytes(this.pvnoLength));
         BerValue.encode(buffer, this.getProtocolVersionNumber());
         buffer.put((byte)-95);
         buffer.put(TLV.getBytes(this.msgTypeLength));
         BerValue.encode(buffer, this.getMessageType().getValue());
         buffer.put((byte)-94);
         buffer.put(TLV.getBytes(this.encPartLength));
         this.encPart.encode(buffer);
      } catch (BufferOverflowException boe) {
         LOG.error(I18n.err(I18n.ERR_137, new Object[]{1 + TLV.getNbBytes(this.apRepLength) + this.apRepLength, buffer.capacity()}));
         throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
      }

      if (IS_DEBUG) {
         LOG.debug("AP-REP encoding : {}", Strings.dumpBytes(buffer.array()));
         LOG.debug("AP-REP initial value : {}", this.toString());
      }

      return buffer;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("AP-REP :\n");
      sb.append("  pvno : ").append(this.getProtocolVersionNumber()).append("\n");
      sb.append("  msg-type : ").append(this.getMessageType()).append("\n");
      sb.append("  enc-part : ").append(this.encPart).append("\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
