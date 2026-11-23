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
import org.apache.directory.shared.kerberos.flags.TicketFlag;
import org.apache.directory.shared.kerberos.flags.TicketFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncTicketPart implements Asn1Object {
   private static final Logger log = LoggerFactory.getLogger(EncTicketPart.class);
   private static final boolean IS_DEBUG;
   private TicketFlags flags = new TicketFlags();
   private EncryptionKey key;
   private String cRealm;
   private PrincipalName cName;
   private TransitedEncoding transited;
   private KerberosTime authTime;
   private KerberosTime startTime;
   private KerberosTime endTime;
   private KerberosTime renewtill;
   private HostAddresses clientAddresses;
   private AuthorizationData authorizationData;
   private int flagsLen;
   private int keyLen;
   private int cRealmLen;
   private byte[] cRealmBytes;
   private int cNameLen;
   private int transitedLen;
   private int authTimeLen;
   private byte[] authTimeBytes;
   private int startTimeLen;
   private byte[] startTimeBytes;
   private int endTimeLen;
   private byte[] endTimeBytes;
   private int renewtillLen;
   private byte[] renewtillBytes;
   private int clientAddressesLen;
   private int authzDataLen;
   private int encTikcetPartSeqLen;
   private int encTikcetPartLen;

   public int computeLength() {
      this.flagsLen = this.flags.getData().length;
      this.flagsLen += 1 + TLV.getNbBytes(this.flagsLen);
      this.encTikcetPartSeqLen = 1 + TLV.getNbBytes(this.flagsLen) + this.flagsLen;
      this.keyLen = this.key.computeLength();
      this.encTikcetPartSeqLen += 1 + TLV.getNbBytes(this.keyLen) + this.keyLen;
      this.cRealmBytes = Strings.getBytesUtf8(this.cRealm);
      this.cRealmLen = 1 + TLV.getNbBytes(this.cRealmBytes.length) + this.cRealmBytes.length;
      this.encTikcetPartSeqLen += 1 + TLV.getNbBytes(this.cRealmLen) + this.cRealmLen;
      this.cNameLen = this.cName.computeLength();
      this.encTikcetPartSeqLen += 1 + TLV.getNbBytes(this.cNameLen) + this.cNameLen;
      this.transitedLen = this.transited.computeLength();
      this.encTikcetPartSeqLen += 1 + TLV.getNbBytes(this.transitedLen) + this.transitedLen;
      this.authTimeBytes = this.authTime.getBytes();
      this.authTimeLen = 1 + TLV.getNbBytes(this.authTimeBytes.length) + this.authTimeBytes.length;
      this.encTikcetPartSeqLen += 1 + TLV.getNbBytes(this.authTimeLen) + this.authTimeLen;
      if (this.startTime != null) {
         this.startTimeBytes = this.startTime.getBytes();
         this.startTimeLen = 1 + TLV.getNbBytes(this.startTimeBytes.length) + this.startTimeBytes.length;
         this.encTikcetPartSeqLen += 1 + TLV.getNbBytes(this.startTimeLen) + this.startTimeLen;
      }

      this.endTimeBytes = this.endTime.getBytes();
      this.endTimeLen = 1 + TLV.getNbBytes(this.endTimeBytes.length) + this.endTimeBytes.length;
      this.encTikcetPartSeqLen += 1 + TLV.getNbBytes(this.endTimeLen) + this.endTimeLen;
      if (this.renewtill != null) {
         this.renewtillBytes = this.renewtill.getBytes();
         this.renewtillLen = 1 + TLV.getNbBytes(this.renewtillBytes.length) + this.renewtillBytes.length;
         this.encTikcetPartSeqLen += 1 + TLV.getNbBytes(this.renewtillLen) + this.renewtillLen;
      }

      if (this.clientAddresses != null) {
         this.clientAddressesLen = this.clientAddresses.computeLength();
         this.encTikcetPartSeqLen += 1 + TLV.getNbBytes(this.clientAddressesLen) + this.clientAddressesLen;
      }

      if (this.authorizationData != null) {
         this.authzDataLen = this.authorizationData.computeLength();
         this.encTikcetPartSeqLen += 1 + TLV.getNbBytes(this.authzDataLen) + this.authzDataLen;
      }

      this.encTikcetPartLen = 1 + TLV.getNbBytes(this.encTikcetPartSeqLen) + this.encTikcetPartSeqLen;
      return 1 + TLV.getNbBytes(this.encTikcetPartLen) + this.encTikcetPartLen;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put((byte)99);
            buffer.put(TLV.getBytes(this.encTikcetPartLen));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.encTikcetPartSeqLen));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.flagsLen));
            BerValue.encode(buffer, this.flags);
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.keyLen));
            this.key.encode(buffer);
            buffer.put((byte)-94);
            buffer.put(TLV.getBytes(this.cRealmLen));
            buffer.put(UniversalTag.GENERAL_STRING.getValue());
            buffer.put(TLV.getBytes(this.cRealmBytes.length));
            buffer.put(this.cRealmBytes);
            buffer.put((byte)-93);
            buffer.put(TLV.getBytes(this.cNameLen));
            this.cName.encode(buffer);
            buffer.put((byte)-92);
            buffer.put(TLV.getBytes(this.transitedLen));
            this.transited.encode(buffer);
            buffer.put((byte)-91);
            buffer.put(TLV.getBytes(this.authTimeLen));
            buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
            buffer.put((byte)15);
            buffer.put(this.authTimeBytes);
            if (this.startTime != null) {
               buffer.put((byte)-90);
               buffer.put(TLV.getBytes(this.startTimeLen));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.startTimeBytes);
            }

            buffer.put((byte)-89);
            buffer.put(TLV.getBytes(this.endTimeLen));
            buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
            buffer.put((byte)15);
            buffer.put(this.endTimeBytes);
            if (this.renewtill != null) {
               buffer.put((byte)-88);
               buffer.put(TLV.getBytes(this.renewtillLen));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.renewtillBytes);
            }

            if (this.clientAddresses != null) {
               buffer.put((byte)-87);
               buffer.put(TLV.getBytes(this.clientAddressesLen));
               this.clientAddresses.encode(buffer);
            }

            if (this.authorizationData != null) {
               buffer.put((byte)-86);
               buffer.put(TLV.getBytes(this.authzDataLen));
               this.authorizationData.encode(buffer);
            }
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_742_CANNOT_ENCODE_ENC_TICKET_PART, new Object[]{1 + TLV.getNbBytes(this.encTikcetPartLen) + this.encTikcetPartLen, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("EncTicketPart encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("EncTicketPart initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public TicketFlags getFlags() {
      return this.flags;
   }

   public void setFlags(TicketFlags flags) {
      this.flags = flags;
   }

   public EncryptionKey getKey() {
      return this.key;
   }

   public void setKey(EncryptionKey key) {
      this.key = key;
   }

   public String getCRealm() {
      return this.cRealm;
   }

   public void setCRealm(String cRealm) {
      this.cRealm = cRealm;
   }

   public PrincipalName getCName() {
      return this.cName;
   }

   public void setCName(PrincipalName cName) {
      this.cName = cName;
   }

   public TransitedEncoding getTransited() {
      return this.transited;
   }

   public void setTransited(TransitedEncoding transited) {
      this.transited = transited;
   }

   public KerberosTime getAuthTime() {
      return this.authTime;
   }

   public void setAuthTime(KerberosTime authTime) {
      this.authTime = authTime;
   }

   public KerberosTime getStartTime() {
      return this.startTime;
   }

   public void setStartTime(KerberosTime startTime) {
      this.startTime = startTime;
   }

   public KerberosTime getEndTime() {
      return this.endTime;
   }

   public void setEndTime(KerberosTime endTime) {
      this.endTime = endTime;
   }

   public KerberosTime getRenewTill() {
      return this.renewtill;
   }

   public void setRenewTill(KerberosTime renewtill) {
      this.renewtill = renewtill;
   }

   public HostAddresses getClientAddresses() {
      return this.clientAddresses;
   }

   public void setClientAddresses(HostAddresses clientAddresses) {
      this.clientAddresses = clientAddresses;
   }

   public AuthorizationData getAuthorizationData() {
      return this.authorizationData;
   }

   public void setAuthorizationData(AuthorizationData authzData) {
      this.authorizationData = authzData;
   }

   public void setFlag(TicketFlag flag) {
      if (this.flags == null) {
         this.flags = new TicketFlags();
      }

      this.flags.setFlag(flag.getValue());
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("EncTicketPart : {\n");
      sb.append("    flags: ").append(this.flags).append('\n');
      sb.append("    key: ").append(this.key).append('\n');
      sb.append("    cRealm: ").append(this.cRealm).append('\n');
      sb.append("    cName: ").append(this.cName).append('\n');
      sb.append("    transited: ").append(this.transited).append('\n');
      sb.append("    authTime: ").append(this.authTime).append('\n');
      if (this.startTime != null) {
         sb.append("    startTime: ").append(this.startTime).append('\n');
      }

      sb.append("    endTime: ").append(this.endTime).append('\n');
      if (this.renewtill != null) {
         sb.append("    renewtill: ").append(this.renewtill).append('\n');
      }

      if (this.clientAddresses != null) {
         sb.append("    clientAddresses: ").append(this.clientAddresses).append('\n');
      }

      if (this.authorizationData != null) {
         sb.append("    authzData: ").append(this.authorizationData).append('\n');
      }

      sb.append("}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
