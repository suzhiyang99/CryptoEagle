package org.apache.directory.shared.kerberos.components;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
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

public class EncKrbPrivPart implements Asn1Object {
   private static final Logger log = LoggerFactory.getLogger(EncKrbPrivPart.class);
   private static final boolean IS_DEBUG;
   private byte[] userData;
   private KerberosTime timestamp;
   private Integer usec;
   private Integer seqNumber;
   private HostAddress senderAddress;
   private HostAddress recipientAddress;
   private int userDataLen;
   private int timestampLen;
   private int usecLen;
   private int seqNumberLen;
   private int senderAddressLen;
   private int recipientAddressLen;
   private int encKrbPrivPartSeqLen;
   private int encKrbPrivPartLen;

   public byte[] getUserData() {
      return this.userData;
   }

   public void setUserData(byte[] userData) {
      this.userData = userData;
   }

   public KerberosTime getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(KerberosTime timestamp) {
      this.timestamp = timestamp;
   }

   public int getUsec() {
      return this.usec == null ? 0 : this.usec;
   }

   public void setUsec(int usec) {
      this.usec = usec;
   }

   public int getSeqNumber() {
      return this.seqNumber == null ? 0 : this.seqNumber;
   }

   public void setSeqNumber(int seqNumber) {
      this.seqNumber = seqNumber;
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

   public int computeLength() {
      this.userDataLen = 1 + TLV.getNbBytes(this.userData.length) + this.userData.length;
      this.encKrbPrivPartSeqLen = 1 + TLV.getNbBytes(this.userDataLen) + this.userDataLen;
      this.senderAddressLen = this.senderAddress.computeLength();
      this.encKrbPrivPartSeqLen += 1 + TLV.getNbBytes(this.senderAddressLen) + this.senderAddressLen;
      if (this.timestamp != null) {
         this.timestampLen = this.timestamp.getBytes().length;
         this.timestampLen += 1 + TLV.getNbBytes(this.timestampLen);
         this.encKrbPrivPartSeqLen += 1 + TLV.getNbBytes(this.timestampLen) + this.timestampLen;
      }

      if (this.usec != null) {
         this.usecLen = BerValue.getNbBytes(this.usec);
         this.usecLen += 1 + TLV.getNbBytes(this.usecLen);
         this.encKrbPrivPartSeqLen += 1 + TLV.getNbBytes(this.usecLen) + this.usecLen;
      }

      if (this.seqNumber != null) {
         this.seqNumberLen = BerValue.getNbBytes(this.seqNumber);
         this.seqNumberLen += 1 + TLV.getNbBytes(this.seqNumberLen);
         this.encKrbPrivPartSeqLen += 1 + TLV.getNbBytes(this.seqNumberLen) + this.seqNumberLen;
      }

      if (this.recipientAddress != null) {
         this.recipientAddressLen = this.recipientAddress.computeLength();
         this.encKrbPrivPartSeqLen += 1 + TLV.getNbBytes(this.recipientAddressLen) + this.recipientAddressLen;
      }

      this.encKrbPrivPartLen = 1 + TLV.getNbBytes(this.encKrbPrivPartSeqLen) + this.encKrbPrivPartSeqLen;
      return 1 + TLV.getNbBytes(this.encKrbPrivPartLen) + this.encKrbPrivPartLen;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put((byte)124);
            buffer.put(TLV.getBytes(this.encKrbPrivPartLen));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.encKrbPrivPartSeqLen));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.userDataLen));
            BerValue.encode(buffer, this.userData);
            if (this.timestamp != null) {
               buffer.put((byte)-95);
               buffer.put(TLV.getBytes(this.timestampLen));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.timestamp.getBytes());
            }

            if (this.usec != null) {
               buffer.put((byte)-94);
               buffer.put(TLV.getBytes(this.usecLen));
               BerValue.encode(buffer, this.usec);
            }

            if (this.seqNumber != null) {
               buffer.put((byte)-93);
               buffer.put(TLV.getBytes(this.seqNumberLen));
               BerValue.encode(buffer, this.seqNumber);
            }

            buffer.put((byte)-92);
            buffer.put(TLV.getBytes(this.senderAddressLen));
            this.senderAddress.encode(buffer);
            if (this.recipientAddress != null) {
               buffer.put((byte)-91);
               buffer.put(TLV.getBytes(this.recipientAddressLen));
               this.recipientAddress.encode(buffer);
            }
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_735_CANNOT_ENCODE_KRBSAFEBODY, new Object[]{1 + TLV.getNbBytes(this.encKrbPrivPartLen) + this.encKrbPrivPartLen, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("EncKrbPrivPart encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("EncKrbPrivPart initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("EncKrbPrivPart : {\n");
      sb.append("    user-data: ").append(Strings.dumpBytes(this.userData)).append('\n');
      if (this.timestamp != null) {
         sb.append("    timestamp: ").append(this.timestamp.getDate()).append('\n');
      }

      if (this.usec != null) {
         sb.append("    usec: ").append(this.usec).append('\n');
      }

      if (this.seqNumber != null) {
         sb.append("    seq-number: ").append(this.seqNumber).append('\n');
      }

      sb.append("    s-address: ").append(this.senderAddress).append('\n');
      if (this.recipientAddress != null) {
         sb.append("    r-address: ").append(this.recipientAddress).append('\n');
      }

      sb.append("}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
