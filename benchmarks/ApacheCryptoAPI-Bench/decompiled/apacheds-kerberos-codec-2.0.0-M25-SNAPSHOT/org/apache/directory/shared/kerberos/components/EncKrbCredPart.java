package org.apache.directory.shared.kerberos.components;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncKrbCredPart implements Asn1Object {
   private static final Logger log = LoggerFactory.getLogger(EncKrbCredPart.class);
   private static final boolean IS_DEBUG;
   private List<KrbCredInfo> ticketInfo;
   private Integer nonce;
   private KerberosTime timestamp;
   private Integer usec;
   private HostAddress senderAddress;
   private HostAddress recipientAddress;
   private int ticketInfoSeqLen;
   private int ticketInfoLen;
   private int nonceLen;
   private int timestampLen;
   private byte[] timestampBytes;
   private int usecLen;
   private int senderAddressLen;
   private int recipientAddressLen;
   private int encKrbCredPartSeqLen;
   private int encKrbCredPartLen;

   public int computeLength() {
      for(KrbCredInfo kci : this.ticketInfo) {
         this.ticketInfoSeqLen += kci.computeLength();
      }

      this.ticketInfoLen = 1 + TLV.getNbBytes(this.ticketInfoSeqLen) + this.ticketInfoSeqLen;
      this.encKrbCredPartSeqLen = 1 + TLV.getNbBytes(this.ticketInfoLen) + this.ticketInfoLen;
      if (this.nonce != null) {
         this.nonceLen = BerValue.getNbBytes(this.nonce);
         this.nonceLen += 1 + TLV.getNbBytes(this.nonceLen);
         this.encKrbCredPartSeqLen += 1 + TLV.getNbBytes(this.nonceLen) + this.nonceLen;
      }

      if (this.timestamp != null) {
         this.timestampBytes = this.timestamp.getBytes();
         this.timestampLen = 1 + TLV.getNbBytes(this.timestampBytes.length) + this.timestampBytes.length;
         this.encKrbCredPartSeqLen += 1 + TLV.getNbBytes(this.timestampLen) + this.timestampLen;
      }

      if (this.usec != null) {
         this.usecLen = BerValue.getNbBytes(this.usec);
         this.usecLen += 1 + TLV.getNbBytes(this.usecLen);
         this.encKrbCredPartSeqLen += 1 + TLV.getNbBytes(this.usecLen) + this.usecLen;
      }

      if (this.senderAddress != null) {
         this.senderAddressLen = this.senderAddress.computeLength();
         this.encKrbCredPartSeqLen += 1 + TLV.getNbBytes(this.senderAddressLen) + this.senderAddressLen;
      }

      if (this.recipientAddress != null) {
         this.recipientAddressLen = this.recipientAddress.computeLength();
         this.encKrbCredPartSeqLen += 1 + TLV.getNbBytes(this.recipientAddressLen) + this.recipientAddressLen;
      }

      this.encKrbCredPartLen = 1 + TLV.getNbBytes(this.encKrbCredPartSeqLen) + this.encKrbCredPartSeqLen;
      return 1 + TLV.getNbBytes(this.encKrbCredPartLen) + this.encKrbCredPartLen;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put((byte)125);
            buffer.put(TLV.getBytes(this.encKrbCredPartLen));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.encKrbCredPartSeqLen));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.ticketInfoLen));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.ticketInfoSeqLen));

            for(KrbCredInfo ki : this.ticketInfo) {
               ki.encode(buffer);
            }

            if (this.nonce != null) {
               buffer.put((byte)-95);
               buffer.put(TLV.getBytes(this.nonceLen));
               BerValue.encode(buffer, this.nonce);
            }

            if (this.timestamp != null) {
               buffer.put((byte)-94);
               buffer.put(TLV.getBytes(this.timestampLen));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.timestampBytes);
            }

            if (this.usec != null) {
               buffer.put((byte)-93);
               buffer.put(TLV.getBytes(this.usecLen));
               BerValue.encode(buffer, this.usec);
            }

            if (this.senderAddress != null) {
               buffer.put((byte)-92);
               buffer.put(TLV.getBytes(this.senderAddressLen));
               this.senderAddress.encode(buffer);
            }

            if (this.recipientAddress != null) {
               buffer.put((byte)-91);
               buffer.put(TLV.getBytes(this.recipientAddressLen));
               this.recipientAddress.encode(buffer);
            }
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_740_CANNOT_ENCODE_ENC_KRB_CRED_PART, new Object[]{1 + TLV.getNbBytes(this.encKrbCredPartLen) + this.encKrbCredPartLen, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("EncKrbCredPart encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("EncKrbCredPart initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public List<KrbCredInfo> getTicketInfo() {
      return this.ticketInfo;
   }

   public void setTicketInfo(List<KrbCredInfo> ticketInfo) {
      this.ticketInfo = ticketInfo;
   }

   public Integer getNonce() {
      return this.nonce;
   }

   public void setNonce(Integer nonce) {
      this.nonce = nonce;
   }

   public KerberosTime getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(KerberosTime timestamp) {
      this.timestamp = timestamp;
   }

   public Integer getUsec() {
      return this.usec;
   }

   public void setUsec(Integer usec) {
      this.usec = usec;
   }

   public HostAddress getSenderAddress() {
      return this.senderAddress;
   }

   public void setSenderAddress(HostAddress senderAddress) {
      this.senderAddress = senderAddress;
   }

   public HostAddress getRecipientAddress() {
      return this.recipientAddress;
   }

   public void setRecipientAddress(HostAddress recipientAddress) {
      this.recipientAddress = recipientAddress;
   }

   public void addTicketInfo(KrbCredInfo info) {
      if (info == null) {
         throw new IllegalArgumentException();
      } else {
         if (this.ticketInfo == null) {
            this.ticketInfo = new ArrayList();
         }

         this.ticketInfo.add(info);
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("EncKrbCredPart : {\n");
      sb.append("    ticketInfo: ").append(this.ticketInfo).append('\n');
      if (this.nonce != null) {
         sb.append("    nonce: ").append(this.nonce).append('\n');
      }

      if (this.timestamp != null) {
         sb.append("    timestamp: ").append(this.timestamp).append('\n');
      }

      if (this.usec != null) {
         sb.append("    usec: ").append(this.usec).append('\n');
      }

      if (this.senderAddress != null) {
         sb.append("    senderAddress: ").append(this.senderAddress).append('\n');
      }

      if (this.recipientAddress != null) {
         sb.append("    recipientAddress: ").append(this.recipientAddress).append('\n');
      }

      sb.append("}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
