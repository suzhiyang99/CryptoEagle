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
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncApRepPart extends KerberosMessage {
   private static final Logger LOG = LoggerFactory.getLogger(EncApRepPart.class);
   private static final boolean IS_DEBUG;
   private KerberosTime ctime;
   private int cusec;
   private EncryptionKey subkey;
   private Integer seqNumber;
   private int ctimeLength;
   private int cusecLength;
   private int subKeyLength;
   private int seqNumberLength;
   private int encApRepPartSeqLength;
   private int encApRepPartLength;

   public EncApRepPart() {
      super(KerberosMessageType.ENC_AP_REP_PART);
   }

   public KerberosTime getCTime() {
      return this.ctime;
   }

   public void setCTime(KerberosTime ctime) {
      this.ctime = ctime;
   }

   public int getCusec() {
      return this.cusec;
   }

   public void setCusec(int cusec) {
      this.cusec = cusec;
   }

   public EncryptionKey getSubkey() {
      return this.subkey;
   }

   public void setSubkey(EncryptionKey subkey) {
      this.subkey = subkey;
   }

   public Integer getSeqNumber() {
      return this.seqNumber;
   }

   public void setSeqNumber(Integer seqNumber) {
      this.seqNumber = seqNumber;
   }

   public int computeLength() {
      this.ctimeLength = 17;
      this.encApRepPartSeqLength = 1 + TLV.getNbBytes(this.ctimeLength) + this.ctimeLength;
      this.cusecLength = 2 + BerValue.getNbBytes(this.cusec);
      this.encApRepPartSeqLength += 1 + TLV.getNbBytes(this.cusecLength) + this.cusecLength;
      if (this.subkey != null) {
         this.subKeyLength = this.subkey.computeLength();
         this.encApRepPartSeqLength += 1 + TLV.getNbBytes(this.subKeyLength) + this.subKeyLength;
      }

      if (this.seqNumber != null) {
         this.seqNumberLength = 2 + BerValue.getNbBytes(this.seqNumber);
         this.encApRepPartSeqLength += 1 + TLV.getNbBytes(this.seqNumberLength) + this.seqNumberLength;
      }

      this.encApRepPartLength = 1 + TLV.getNbBytes(this.encApRepPartSeqLength) + this.encApRepPartSeqLength;
      return 1 + TLV.getNbBytes(this.encApRepPartLength) + this.encApRepPartLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         buffer = ByteBuffer.allocate(this.computeLength());
      }

      try {
         buffer.put((byte)123);
         buffer.put(TLV.getBytes(this.encApRepPartLength));
         buffer.put(UniversalTag.SEQUENCE.getValue());
         buffer.put(TLV.getBytes(this.encApRepPartSeqLength));
         buffer.put((byte)-96);
         buffer.put((byte)17);
         buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
         buffer.put((byte)15);
         buffer.put(this.ctime.getBytes());
         buffer.put((byte)-95);
         buffer.put(TLV.getBytes(this.cusecLength));
         BerValue.encode(buffer, this.cusec);
         if (this.subkey != null) {
            buffer.put((byte)-94);
            buffer.put(TLV.getBytes(this.subKeyLength));
            this.subkey.encode(buffer);
         }

         if (this.seqNumber != null) {
            buffer.put((byte)-93);
            buffer.put(TLV.getBytes(this.seqNumberLength));
            BerValue.encode(buffer, this.seqNumber);
         }
      } catch (BufferOverflowException boe) {
         LOG.error(I18n.err(I18n.ERR_139, new Object[]{1 + TLV.getNbBytes(this.encApRepPartLength) + this.encApRepPartLength, buffer.capacity()}));
         throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
      }

      if (IS_DEBUG) {
         LOG.debug("EncApRepPart encoding : {}", Strings.dumpBytes(buffer.array()));
         LOG.debug("EncApRepPart initial value : {}", this.toString());
      }

      return buffer;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("EncApRepPart : \n");
      sb.append("    ctime : ").append(this.ctime).append('\n');
      sb.append("    cusec : ").append(this.cusec).append('\n');
      if (this.subkey != null) {
         sb.append("    subkey : ").append(this.subkey).append('\n');
      }

      if (this.seqNumber != null) {
         sb.append("    seq-number : ").append(this.seqNumber).append('\n');
      }

      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
