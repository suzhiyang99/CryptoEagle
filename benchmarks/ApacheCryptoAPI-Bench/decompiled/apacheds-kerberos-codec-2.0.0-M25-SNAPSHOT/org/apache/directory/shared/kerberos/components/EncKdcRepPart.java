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

public class EncKdcRepPart implements Asn1Object {
   private static final Logger log = LoggerFactory.getLogger(EncKdcRepPart.class);
   private static final boolean IS_DEBUG;
   private EncryptionKey key;
   private LastReq lastReq;
   private int nonce;
   private KerberosTime keyExpiration;
   private TicketFlags flags = new TicketFlags();
   private KerberosTime authTime;
   private KerberosTime startTime;
   private KerberosTime endTime;
   private KerberosTime renewTill;
   private String srealm;
   private PrincipalName sname;
   private HostAddresses caddr;
   private int keyLength;
   private int lastReqLength;
   private int nonceLength;
   private int flagsLength;
   private byte[] srealmBytes;
   private int srealmLength;
   private int snameLength;
   private int caddrLength;
   private int encKdcRepPartSeqLength;

   public KerberosTime getAuthTime() {
      return this.authTime;
   }

   public void setAuthTime(KerberosTime time) {
      this.authTime = time;
   }

   public HostAddresses getClientAddresses() {
      return this.caddr;
   }

   public void setClientAddresses(HostAddresses caddr) {
      this.caddr = caddr;
   }

   public KerberosTime getEndTime() {
      return this.endTime;
   }

   public void setEndTime(KerberosTime time) {
      this.endTime = time;
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

   public KerberosTime getKeyExpiration() {
      return this.keyExpiration;
   }

   public void setKeyExpiration(KerberosTime expiration) {
      this.keyExpiration = expiration;
   }

   public LastReq getLastReq() {
      return this.lastReq;
   }

   public void setLastReq(LastReq lastReq) {
      this.lastReq = lastReq;
   }

   public int getNonce() {
      return this.nonce;
   }

   public void setNonce(int nonce) {
      this.nonce = nonce;
   }

   public KerberosTime getRenewTill() {
      return this.renewTill;
   }

   public void setRenewTill(KerberosTime till) {
      this.renewTill = till;
   }

   public PrincipalName getSName() {
      return this.sname;
   }

   public void setSName(PrincipalName sname) {
      this.sname = sname;
   }

   public String getSRealm() {
      return this.srealm;
   }

   public void setSRealm(String srealm) {
      this.srealm = srealm;
   }

   public KerberosTime getStartTime() {
      return this.startTime;
   }

   public void setStartTime(KerberosTime time) {
      this.startTime = time;
   }

   public int computeLength() {
      this.keyLength = this.key.computeLength();
      this.encKdcRepPartSeqLength = 1 + TLV.getNbBytes(this.keyLength) + this.keyLength;
      this.lastReqLength = this.lastReq.computeLength();
      this.encKdcRepPartSeqLength += 1 + TLV.getNbBytes(this.lastReqLength) + this.lastReqLength;
      this.nonceLength = BerValue.getNbBytes(this.nonce);
      this.nonceLength += 1 + TLV.getNbBytes(this.nonceLength);
      this.encKdcRepPartSeqLength += 1 + TLV.getNbBytes(this.nonceLength) + this.nonceLength;
      if (this.keyExpiration != null) {
         this.encKdcRepPartSeqLength += 19;
      }

      this.flagsLength = 7;
      this.encKdcRepPartSeqLength += 1 + TLV.getNbBytes(this.flagsLength) + this.flagsLength;
      this.encKdcRepPartSeqLength += 19;
      if (this.startTime != null) {
         this.encKdcRepPartSeqLength += 19;
      }

      this.encKdcRepPartSeqLength += 19;
      if (this.renewTill != null) {
         this.encKdcRepPartSeqLength += 19;
      }

      this.srealmBytes = this.srealm.getBytes();
      this.srealmLength = 1 + TLV.getNbBytes(this.srealmBytes.length) + this.srealmBytes.length;
      this.encKdcRepPartSeqLength += 1 + TLV.getNbBytes(this.srealmLength) + this.srealmLength;
      this.snameLength = this.sname.computeLength();
      this.encKdcRepPartSeqLength += 1 + TLV.getNbBytes(this.snameLength) + this.snameLength;
      if (this.caddr != null) {
         this.caddrLength = this.caddr.computeLength();
         this.encKdcRepPartSeqLength += 1 + TLV.getNbBytes(this.caddrLength) + this.caddrLength;
      }

      return 1 + TLV.getNbBytes(this.encKdcRepPartSeqLength) + this.encKdcRepPartSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.encKdcRepPartSeqLength));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.keyLength));
            this.key.encode(buffer);
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.lastReqLength));
            this.lastReq.encode(buffer);
            buffer.put((byte)-94);
            buffer.put(TLV.getBytes(this.nonceLength));
            BerValue.encode(buffer, this.nonce);
            if (this.keyExpiration != null) {
               buffer.put((byte)-93);
               buffer.put(TLV.getBytes(17));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.keyExpiration.getBytes());
            }

            buffer.put((byte)-92);
            buffer.put(TLV.getBytes(7));
            BerValue.encode(buffer, this.flags);
            buffer.put((byte)-91);
            buffer.put(TLV.getBytes(17));
            buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
            buffer.put((byte)15);
            buffer.put(this.authTime.getBytes());
            if (this.startTime != null) {
               buffer.put((byte)-90);
               buffer.put(TLV.getBytes(17));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.startTime.getBytes());
            }

            buffer.put((byte)-89);
            buffer.put(TLV.getBytes(17));
            buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
            buffer.put((byte)15);
            buffer.put(this.endTime.getBytes());
            if (this.renewTill != null) {
               buffer.put((byte)-88);
               buffer.put(TLV.getBytes(17));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.renewTill.getBytes());
            }

            buffer.put((byte)-87);
            buffer.put(TLV.getBytes(this.srealmLength));
            buffer.put(UniversalTag.GENERAL_STRING.getValue());
            buffer.put(TLV.getBytes(this.srealmBytes.length));
            buffer.put(this.srealmBytes);
            buffer.put((byte)-86);
            buffer.put(TLV.getBytes(this.snameLength));
            this.sname.encode(buffer);
            if (this.caddr != null) {
               buffer.put((byte)-85);
               buffer.put(TLV.getBytes(this.caddrLength));
               this.caddr.encode(buffer);
            }
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_140, new Object[]{1 + TLV.getNbBytes(0) + 0, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("EncKdcRepPart encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("EncKdcRepPart initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("EncKdcRepPart : \n");
      sb.append("    key : ").append(this.key).append("\n");
      sb.append("    last-req : ").append(this.lastReq).append("\n");
      sb.append("    nonce : ").append(this.nonce).append("\n");
      if (this.keyExpiration != null) {
         sb.append("    key-expiration : ").append(this.keyExpiration).append("\n");
      }

      sb.append("    flags : ").append(this.flags).append("\n");
      sb.append("    authtime : ").append(this.authTime).append("\n");
      if (this.startTime != null) {
         sb.append("    starttime : ").append(this.startTime).append("\n");
      }

      sb.append("    endtime : ").append(this.endTime).append("\n");
      if (this.renewTill != null) {
         sb.append("    renew-till : ").append(this.renewTill).append("\n");
      }

      sb.append("    srealm : ").append(this.srealm).append("\n");
      sb.append("    sname : ").append(this.sname).append("\n");
      if (this.caddr != null) {
         sb.append("    caddr : ").append(this.caddr).append("\n");
      }

      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
