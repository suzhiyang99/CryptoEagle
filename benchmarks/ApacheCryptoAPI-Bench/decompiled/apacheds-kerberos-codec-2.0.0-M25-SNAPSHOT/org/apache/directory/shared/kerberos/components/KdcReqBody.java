package org.apache.directory.shared.kerberos.components;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.options.KdcOptions;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.messages.Ticket;

public class KdcReqBody implements Asn1Object {
   private KdcOptions kdcOptions;
   private PrincipalName cName;
   private String realm;
   private PrincipalName sName;
   private KerberosTime from;
   private KerberosTime till;
   private KerberosTime rtime;
   private int nonce;
   private Set<EncryptionType> eType = new LinkedHashSet();
   private HostAddresses addresses;
   private EncryptedData encAuthorizationData;
   private List<Ticket> additionalTickets = new ArrayList();
   private int kdcOptionsLength;
   private int cNameLength;
   private int realmLength;
   private byte[] realmBytes;
   private int sNameLength;
   private int fromLength;
   private int tillLength;
   private int rtimeLength;
   private int nonceLength;
   private int eTypeLength;
   private int eTypeSeqLength;
   private int[] eTypeLengths;
   private int addressesLength;
   private int encAuthzDataLength;
   private int additionalTicketLength;
   private int additionalTicketSeqLength;
   private int[] additionalTicketsLengths;
   private int kdcReqBodySeqLength;
   private int kdcReqBodyLength;

   public Ticket[] getAdditionalTickets() {
      return (Ticket[])this.additionalTickets.toArray(new Ticket[0]);
   }

   public void setAdditionalTickets(List<Ticket> additionalTickets) {
      this.additionalTickets = additionalTickets;
   }

   public void addAdditionalTicket(Ticket additionalTicket) {
      this.additionalTickets.add(additionalTicket);
   }

   public HostAddresses getAddresses() {
      return this.addresses;
   }

   public void setAddresses(HostAddresses addresses) {
      this.addresses = addresses;
   }

   public PrincipalName getCName() {
      return this.cName;
   }

   public void setCName(PrincipalName cName) {
      this.cName = cName;
   }

   public EncryptedData getEncAuthorizationData() {
      return this.encAuthorizationData;
   }

   public void setEncAuthorizationData(EncryptedData encAuthorizationData) {
      this.encAuthorizationData = encAuthorizationData;
   }

   public Set<EncryptionType> getEType() {
      return this.eType;
   }

   public void setEType(Set<EncryptionType> eType) {
      this.eType = eType;
   }

   public void addEType(EncryptionType eType) {
      this.eType.add(eType);
   }

   public KerberosTime getFrom() {
      return this.from;
   }

   public void setFrom(KerberosTime from) {
      this.from = from;
   }

   public KdcOptions getKdcOptions() {
      return this.kdcOptions;
   }

   public void setKdcOptions(KdcOptions kdcOptions) {
      this.kdcOptions = kdcOptions;
   }

   public int getNonce() {
      return this.nonce;
   }

   public void setNonce(int nonce) {
      this.nonce = nonce;
   }

   public String getRealm() {
      return this.realm;
   }

   public void setRealm(String realm) {
      this.realm = realm;
   }

   public KerberosTime getRTime() {
      return this.rtime;
   }

   public void setRtime(KerberosTime rtime) {
      this.rtime = rtime;
   }

   public PrincipalName getSName() {
      return this.sName;
   }

   public void setSName(PrincipalName sName) {
      this.sName = sName;
   }

   public KerberosTime getTill() {
      return this.till;
   }

   public void setTill(KerberosTime till) {
      this.till = till;
   }

   public int computeLength() {
      this.reset();
      this.kdcOptionsLength = 2 + this.kdcOptions.getBytes().length;
      this.kdcReqBodySeqLength = 1 + TLV.getNbBytes(this.kdcOptionsLength) + this.kdcOptionsLength;
      if (this.cName != null) {
         this.cNameLength = this.cName.computeLength();
         this.kdcReqBodySeqLength += 1 + TLV.getNbBytes(this.cNameLength) + this.cNameLength;
      }

      this.realmBytes = Strings.getBytesUtf8(this.realm);
      this.realmLength = 1 + TLV.getNbBytes(this.realmBytes.length) + this.realmBytes.length;
      this.kdcReqBodySeqLength += 1 + TLV.getNbBytes(this.realmLength) + this.realmLength;
      if (this.sName != null) {
         this.sNameLength = this.sName.computeLength();
         this.kdcReqBodySeqLength += 1 + TLV.getNbBytes(this.sNameLength) + this.sNameLength;
      }

      if (this.from != null) {
         this.fromLength = 17;
         this.kdcReqBodySeqLength += 2 + this.fromLength;
      }

      this.tillLength = 17;
      this.kdcReqBodySeqLength += 2 + this.tillLength;
      if (this.rtime != null) {
         this.rtimeLength = 17;
         this.kdcReqBodySeqLength += 2 + this.rtimeLength;
      }

      this.nonceLength = 2 + BerValue.getNbBytes(this.nonce);
      this.kdcReqBodySeqLength += 2 + this.nonceLength;
      this.eTypeLengths = new int[this.eType.size()];
      int pos = 0;
      this.eTypeSeqLength = 0;

      for(EncryptionType encryptionType : this.eType) {
         this.eTypeLengths[pos] = 2 + BerValue.getNbBytes(encryptionType.getValue());
         this.eTypeSeqLength += this.eTypeLengths[pos];
         ++pos;
      }

      this.eTypeLength = 1 + TLV.getNbBytes(this.eTypeSeqLength) + this.eTypeSeqLength;
      this.kdcReqBodySeqLength += 1 + TLV.getNbBytes(this.eTypeLength) + this.eTypeLength;
      if (this.addresses != null) {
         this.addressesLength = this.addresses.computeLength();
         this.kdcReqBodySeqLength += 1 + TLV.getNbBytes(this.addressesLength) + this.addressesLength;
      }

      if (this.encAuthorizationData != null) {
         this.encAuthzDataLength = this.encAuthorizationData.computeLength();
         this.kdcReqBodySeqLength += 1 + TLV.getNbBytes(this.encAuthzDataLength) + this.encAuthzDataLength;
      }

      if (this.additionalTickets.size() != 0) {
         this.additionalTicketsLengths = new int[this.additionalTickets.size()];
         this.additionalTicketSeqLength = 0;
         pos = 0;

         for(Ticket ticket : this.additionalTickets) {
            this.additionalTicketsLengths[pos] = ticket.computeLength();
            this.additionalTicketSeqLength += this.additionalTicketsLengths[pos];
            ++pos;
         }

         this.additionalTicketLength = 1 + TLV.getNbBytes(this.additionalTicketSeqLength) + this.additionalTicketSeqLength;
         this.kdcReqBodySeqLength += 1 + TLV.getNbBytes(this.additionalTicketLength) + this.additionalTicketLength;
      }

      this.kdcReqBodyLength = 1 + TLV.getNbBytes(this.kdcReqBodySeqLength) + this.kdcReqBodySeqLength;
      return this.kdcReqBodyLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         buffer.put(UniversalTag.SEQUENCE.getValue());
         buffer.put(TLV.getBytes(this.kdcReqBodySeqLength));
         buffer.put((byte)-96);
         buffer.put(TLV.getBytes(this.kdcOptionsLength));
         BerValue.encode(buffer, this.kdcOptions);
         if (this.cName != null) {
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.cNameLength));
            this.cName.encode(buffer);
         }

         buffer.put((byte)-94);
         buffer.put(TLV.getBytes(this.realmLength));
         buffer.put(UniversalTag.GENERAL_STRING.getValue());
         buffer.put(TLV.getBytes(this.realmBytes.length));
         buffer.put(this.realmBytes);
         if (this.sName != null) {
            buffer.put((byte)-93);
            buffer.put(TLV.getBytes(this.sNameLength));
            this.sName.encode(buffer);
         }

         if (this.from != null) {
            buffer.put((byte)-92);
            buffer.put(TLV.getBytes(this.fromLength));
            buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
            buffer.put((byte)15);
            buffer.put(this.from.getBytes());
         }

         buffer.put((byte)-91);
         buffer.put(TLV.getBytes(this.tillLength));
         buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
         buffer.put((byte)15);
         buffer.put(this.till.getBytes());
         if (this.rtime != null) {
            buffer.put((byte)-90);
            buffer.put(TLV.getBytes(this.rtimeLength));
            buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
            buffer.put((byte)15);
            buffer.put(this.rtime.getBytes());
         }

         buffer.put((byte)-89);
         buffer.put(TLV.getBytes(this.nonceLength));
         BerValue.encode(buffer, this.nonce);
         buffer.put((byte)-88);
         buffer.put(TLV.getBytes(this.eTypeLength));
         buffer.put(UniversalTag.SEQUENCE.getValue());
         buffer.put(TLV.getBytes(this.eTypeSeqLength));

         for(EncryptionType encryptionType : this.eType) {
            BerValue.encode(buffer, encryptionType.getValue());
         }

         if (this.addresses != null) {
            buffer.put((byte)-87);
            buffer.put(TLV.getBytes(this.addressesLength));
            this.addresses.encode(buffer);
         }

         if (this.encAuthorizationData != null) {
            buffer.put((byte)-86);
            buffer.put(TLV.getBytes(this.encAuthzDataLength));
            this.encAuthorizationData.encode(buffer);
         }

         if (this.additionalTickets.size() != 0) {
            buffer.put((byte)-85);
            buffer.put(TLV.getBytes(this.additionalTicketLength));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.additionalTicketSeqLength));

            for(Ticket ticket : this.additionalTickets) {
               ticket.encode(buffer);
            }
         }

         return buffer;
      }
   }

   private void reset() {
      this.kdcOptionsLength = 0;
      this.cNameLength = 0;
      this.realmLength = 0;
      this.realmBytes = null;
      this.sNameLength = 0;
      this.fromLength = 0;
      this.tillLength = 0;
      this.rtimeLength = 0;
      this.nonceLength = 0;
      this.eTypeLength = 0;
      this.eTypeSeqLength = 0;
      this.eTypeLengths = null;
      this.addressesLength = 0;
      this.encAuthzDataLength = 0;
      this.additionalTicketLength = 0;
      this.additionalTicketSeqLength = 0;
      this.additionalTicketsLengths = null;
      this.kdcReqBodySeqLength = 0;
      this.kdcReqBodyLength = 0;
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      if (this.kdcOptions != null && this.kdcOptions.size() > 0) {
         sb.append(tabs).append("KDCOptions : ").append(this.kdcOptions).append('\n');
      }

      if (this.cName != null) {
         sb.append(tabs).append("cname : ").append(this.cName).append('\n');
      }

      sb.append(tabs).append("realm : ").append(this.realm).append('\n');
      if (this.sName != null) {
         sb.append(tabs).append("sname : ").append(this.sName).append('\n');
      }

      if (this.from != null) {
         sb.append(tabs).append("from : ").append(this.from).append('\n');
      }

      sb.append(tabs).append("till : ").append(this.till).append('\n');
      if (this.rtime != null) {
         sb.append(tabs).append("rtime : ").append(this.rtime).append('\n');
      }

      sb.append(tabs).append("nonce : ").append(this.nonce).append('\n');
      sb.append(tabs).append("etype : ");
      boolean isFirst = true;

      for(EncryptionType encryptionType : this.eType) {
         if (isFirst) {
            isFirst = false;
         } else {
            sb.append(" ");
         }

         sb.append(encryptionType);
      }

      if (this.addresses != null) {
         sb.append('\n');
         sb.append(tabs).append("addresses : ");
         isFirst = true;

         for(HostAddress hostAddress : this.addresses.getAddresses()) {
            if (isFirst) {
               isFirst = false;
            } else {
               sb.append(" ");
            }

            sb.append(hostAddress);
         }
      }

      if (this.encAuthorizationData != null) {
         sb.append('\n');
         sb.append(tabs).append("enc-authorization-data").append(this.encAuthorizationData);
      }

      if (this.additionalTickets.size() != 0) {
         sb.append('\n');
         sb.append(tabs).append("Tickets : ");
         isFirst = true;

         for(Ticket ticket : this.additionalTickets) {
            if (isFirst) {
               isFirst = false;
               sb.append('\n');
            }

            sb.append(ticket.toString(tabs + "    "));
         }
      }

      return sb.toString();
   }

   public String toString() {
      return this.toString("");
   }
}
