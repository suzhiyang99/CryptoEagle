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
import org.apache.directory.shared.kerberos.flags.TicketFlags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrbCredInfo implements Asn1Object {
   private static final Logger log = LoggerFactory.getLogger(KrbCredInfo.class);
   private static final boolean IS_DEBUG;
   private EncryptionKey key;
   private String pRealm;
   private PrincipalName pName;
   private TicketFlags ticketFlags;
   private KerberosTime authTime;
   private KerberosTime startTime;
   private KerberosTime endTime;
   private KerberosTime renewtill;
   private String sRealm;
   private PrincipalName sName;
   private HostAddresses clientAddresses;
   private int keyLen;
   private int pRealmLen;
   private byte[] pRealmBytes;
   private int pNameLen;
   private int ticketFlagsLen;
   private int authTimeLen;
   private byte[] authTimeBytes;
   private int startTimeLen;
   private byte[] startTimeBytes;
   private int endTimeLen;
   private byte[] endTimeBytes;
   private int renewtillLen;
   private byte[] renewtillBytes;
   private int sRealmLen;
   private byte[] sRealmBytes;
   private int sNameLen;
   private int clientAddressesLen;
   private int krbKredInfoSeqLen;

   public int computeLength() {
      this.keyLen = this.key.computeLength();
      this.krbKredInfoSeqLen = 1 + TLV.getNbBytes(this.keyLen) + this.keyLen;
      if (this.pRealm != null) {
         this.pRealmBytes = Strings.getBytesUtf8(this.pRealm);
         this.pRealmLen = 1 + TLV.getNbBytes(this.pRealmBytes.length) + this.pRealmBytes.length;
         this.krbKredInfoSeqLen += 1 + TLV.getNbBytes(this.pRealmLen) + this.pRealmLen;
      }

      if (this.pName != null) {
         this.pNameLen = this.pName.computeLength();
         this.krbKredInfoSeqLen += 1 + TLV.getNbBytes(this.pNameLen) + this.pNameLen;
      }

      if (this.ticketFlags != null) {
         this.ticketFlagsLen = this.ticketFlags.getData().length;
         this.ticketFlagsLen += 1 + TLV.getNbBytes(this.ticketFlagsLen);
         this.krbKredInfoSeqLen += 1 + TLV.getNbBytes(this.ticketFlagsLen) + this.ticketFlagsLen;
      }

      if (this.authTime != null) {
         this.authTimeBytes = this.authTime.getBytes();
         this.authTimeLen = 1 + TLV.getNbBytes(this.authTimeBytes.length) + this.authTimeBytes.length;
         this.krbKredInfoSeqLen += 1 + TLV.getNbBytes(this.authTimeLen) + this.authTimeLen;
      }

      if (this.startTime != null) {
         this.startTimeBytes = this.startTime.getBytes();
         this.startTimeLen = 1 + TLV.getNbBytes(this.startTimeBytes.length) + this.startTimeBytes.length;
         this.krbKredInfoSeqLen += 1 + TLV.getNbBytes(this.startTimeLen) + this.startTimeLen;
      }

      if (this.endTime != null) {
         this.endTimeBytes = this.endTime.getBytes();
         this.endTimeLen = 1 + TLV.getNbBytes(this.endTimeBytes.length) + this.endTimeBytes.length;
         this.krbKredInfoSeqLen += 1 + TLV.getNbBytes(this.endTimeLen) + this.endTimeLen;
      }

      if (this.renewtill != null) {
         this.renewtillBytes = this.renewtill.getBytes();
         this.renewtillLen = 1 + TLV.getNbBytes(this.renewtillBytes.length) + this.renewtillBytes.length;
         this.krbKredInfoSeqLen += 1 + TLV.getNbBytes(this.renewtillLen) + this.renewtillLen;
      }

      if (this.sRealm != null) {
         this.sRealmBytes = Strings.getBytesUtf8(this.sRealm);
         this.sRealmLen = 1 + TLV.getNbBytes(this.sRealmBytes.length) + this.sRealmBytes.length;
         this.krbKredInfoSeqLen += 1 + TLV.getNbBytes(this.sRealmLen) + this.sRealmLen;
      }

      if (this.sName != null) {
         this.sNameLen = this.sName.computeLength();
         this.krbKredInfoSeqLen += 1 + TLV.getNbBytes(this.sNameLen) + this.sNameLen;
      }

      if (this.clientAddresses != null) {
         this.clientAddressesLen = this.clientAddresses.computeLength();
         this.krbKredInfoSeqLen += 1 + TLV.getNbBytes(this.clientAddressesLen) + this.clientAddressesLen;
      }

      return 1 + TLV.getNbBytes(this.krbKredInfoSeqLen) + this.krbKredInfoSeqLen;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.krbKredInfoSeqLen));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.keyLen));
            this.key.encode(buffer);
            if (this.pRealm != null) {
               buffer.put((byte)-95);
               buffer.put(TLV.getBytes(this.pRealmLen));
               buffer.put(UniversalTag.GENERAL_STRING.getValue());
               buffer.put(TLV.getBytes(this.pRealmBytes.length));
               buffer.put(this.pRealmBytes);
            }

            if (this.pName != null) {
               buffer.put((byte)-94);
               buffer.put(TLV.getBytes(this.pNameLen));
               this.pName.encode(buffer);
            }

            if (this.ticketFlags != null) {
               buffer.put((byte)-93);
               buffer.put(TLV.getBytes(this.ticketFlagsLen));
               BerValue.encode(buffer, this.ticketFlags);
            }

            if (this.authTime != null) {
               buffer.put((byte)-92);
               buffer.put(TLV.getBytes(this.authTimeLen));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.authTimeBytes);
            }

            if (this.startTime != null) {
               buffer.put((byte)-91);
               buffer.put(TLV.getBytes(this.startTimeLen));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.startTimeBytes);
            }

            if (this.endTime != null) {
               buffer.put((byte)-90);
               buffer.put(TLV.getBytes(this.endTimeLen));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.endTimeBytes);
            }

            if (this.renewtill != null) {
               buffer.put((byte)-89);
               buffer.put(TLV.getBytes(this.renewtillLen));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.renewtillBytes);
            }

            if (this.sRealm != null) {
               buffer.put((byte)-88);
               buffer.put(TLV.getBytes(this.sRealmLen));
               buffer.put(UniversalTag.GENERAL_STRING.getValue());
               buffer.put(TLV.getBytes(this.sRealmBytes.length));
               buffer.put(this.sRealmBytes);
            }

            if (this.sName != null) {
               buffer.put((byte)-87);
               buffer.put(TLV.getBytes(this.sNameLen));
               this.sName.encode(buffer);
            }

            if (this.clientAddresses != null) {
               buffer.put((byte)-86);
               buffer.put(TLV.getBytes(this.clientAddressesLen));
               this.clientAddresses.encode(buffer);
            }
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_739_CANNOT_ENCODE_KRB_CRED_INFO, new Object[]{1 + TLV.getNbBytes(this.krbKredInfoSeqLen) + this.krbKredInfoSeqLen, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("KrbCredInfo encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("KrbCredInfo initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public EncryptionKey getKey() {
      return this.key;
   }

   public void setKey(EncryptionKey key) {
      this.key = key;
   }

   public String getpRealm() {
      return this.pRealm;
   }

   public void setpRealm(String pRealm) {
      this.pRealm = pRealm;
   }

   public PrincipalName getpName() {
      return this.pName;
   }

   public void setpName(PrincipalName pName) {
      this.pName = pName;
   }

   public TicketFlags getTicketFlags() {
      return this.ticketFlags;
   }

   public void setTicketFlags(TicketFlags ticketFlags) {
      this.ticketFlags = ticketFlags;
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

   public KerberosTime getRenewtill() {
      return this.renewtill;
   }

   public void setRenewtill(KerberosTime renewtill) {
      this.renewtill = renewtill;
   }

   public String getsRealm() {
      return this.sRealm;
   }

   public void setsRealm(String sRealm) {
      this.sRealm = sRealm;
   }

   public PrincipalName getsName() {
      return this.sName;
   }

   public void setsName(PrincipalName sName) {
      this.sName = sName;
   }

   public HostAddresses getClientAddresses() {
      return this.clientAddresses;
   }

   public void setClientAddresses(HostAddresses clientAddresses) {
      this.clientAddresses = clientAddresses;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("KrbCredInfo : {\n");
      sb.append("    key: ").append(this.key).append('\n');
      if (this.pRealm != null) {
         sb.append("    pRealm: ").append(this.pRealm).append('\n');
      }

      if (this.pName != null) {
         sb.append("    pName: ").append(this.pName).append('\n');
      }

      if (this.ticketFlags != null) {
         sb.append("    ticketFlags: ").append(this.ticketFlags).append('\n');
      }

      if (this.authTime != null) {
         sb.append("    authTime: ").append(this.authTime).append('\n');
      }

      if (this.startTime != null) {
         sb.append("    startTime: ").append(this.startTime).append('\n');
      }

      if (this.endTime != null) {
         sb.append("    endTime: ").append(this.endTime).append('\n');
      }

      if (this.renewtill != null) {
         sb.append("    renewtill: ").append(this.renewtill).append('\n');
      }

      if (this.sRealm != null) {
         sb.append("    sRealm: ").append(this.sRealm).append('\n');
      }

      if (this.sName != null) {
         sb.append("    sName: ").append(this.sName).append('\n');
      }

      if (this.clientAddresses != null) {
         sb.append("    clientAddresses: ").append(this.clientAddresses).append('\n');
      }

      sb.append("}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
