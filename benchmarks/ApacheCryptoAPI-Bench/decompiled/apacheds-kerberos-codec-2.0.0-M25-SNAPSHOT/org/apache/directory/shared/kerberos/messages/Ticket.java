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
import org.apache.directory.shared.kerberos.components.EncTicketPart;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.apache.directory.shared.kerberos.components.PrincipalName;
import org.apache.directory.shared.kerberos.exceptions.InvalidTicketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ticket extends KerberosMessage {
   private static final Logger LOG = LoggerFactory.getLogger(Ticket.class);
   private static final boolean IS_DEBUG;
   public static final int TICKET_VNO = 5;
   private byte[] realmBytes;
   private PrincipalName sName;
   private String realm;
   private EncryptedData encPart;
   private EncTicketPart encTicketPart;
   private int tktvnoLength;
   private int realmLength;
   private int sNameLength;
   private int encPartLength;
   private int ticketSeqLength;
   private int ticketLength;

   public Ticket(PrincipalName sName, EncryptedData encPart) throws InvalidTicketException {
      this(5, sName, encPart);
      this.setSName(sName);
   }

   public Ticket() {
      super(KerberosMessageType.TICKET);
   }

   public Ticket(int tktvno, PrincipalName sName, EncryptedData encPart) throws InvalidTicketException {
      super(tktvno, KerberosMessageType.TICKET);
      this.encPart = encPart;
      this.setSName(sName);
   }

   public EncryptedData getEncPart() {
      return this.encPart;
   }

   public void setEncPart(EncryptedData encPart) {
      this.encPart = encPart;
   }

   public String getRealm() {
      return this.realm;
   }

   public void setRealm(String realm) {
      this.realm = realm;
   }

   public PrincipalName getSName() {
      return this.sName;
   }

   public void setSName(PrincipalName sName) {
      this.sName = sName;
   }

   public int getTktVno() {
      return this.getProtocolVersionNumber();
   }

   public void setTktVno(int tktVno) {
      this.setProtocolVersionNumber(tktVno);
   }

   public EncTicketPart getEncTicketPart() {
      return this.encTicketPart;
   }

   public void setEncTicketPart(EncTicketPart encTicketPart) {
      this.encTicketPart = encTicketPart;
   }

   public int computeLength() {
      this.tktvnoLength = 2 + BerValue.getNbBytes(this.getProtocolVersionNumber());
      this.realmBytes = Strings.getBytesUtf8(this.realm);
      this.realmLength = 1 + TLV.getNbBytes(this.realmBytes.length) + this.realmBytes.length;
      this.sNameLength = this.sName.computeLength();
      this.encPartLength = this.encPart.computeLength();
      this.ticketSeqLength = 1 + TLV.getNbBytes(this.tktvnoLength) + this.tktvnoLength + 1 + TLV.getNbBytes(this.realmLength) + this.realmLength + 1 + TLV.getNbBytes(this.sNameLength) + this.sNameLength + 1 + TLV.getNbBytes(this.encPartLength) + this.encPartLength;
      this.ticketLength = 1 + TLV.getNbBytes(this.ticketSeqLength) + this.ticketSeqLength;
      return 1 + TLV.getNbBytes(this.ticketLength) + this.ticketLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         buffer = ByteBuffer.allocate(this.computeLength());
      }

      try {
         buffer.put((byte)97);
         buffer.put(TLV.getBytes(this.ticketLength));
         buffer.put(UniversalTag.SEQUENCE.getValue());
         buffer.put(TLV.getBytes(this.ticketSeqLength));
         buffer.put((byte)-96);
         buffer.put(TLV.getBytes(this.tktvnoLength));
         BerValue.encode(buffer, this.getProtocolVersionNumber());
         buffer.put((byte)-95);
         buffer.put(TLV.getBytes(this.realmLength));
         buffer.put(UniversalTag.GENERAL_STRING.getValue());
         buffer.put(TLV.getBytes(this.realmBytes.length));
         buffer.put(this.realmBytes);
         buffer.put((byte)-94);
         buffer.put(TLV.getBytes(this.sNameLength));
         this.sName.encode(buffer);
         buffer.put((byte)-93);
         buffer.put(TLV.getBytes(this.encPartLength));
         this.encPart.encode(buffer);
      } catch (BufferOverflowException boe) {
         LOG.error(I18n.err(I18n.ERR_137, new Object[]{1 + TLV.getNbBytes(this.ticketLength) + this.ticketLength, buffer.capacity()}));
         throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
      }

      if (IS_DEBUG) {
         LOG.debug("Ticket encoding : {}", Strings.dumpBytes(buffer.array()));
         LOG.debug("Ticket initial value : {}", this.toString());
      }

      return buffer;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.encPart == null ? 0 : this.encPart.hashCode());
      result = 31 * result + (this.realm == null ? 0 : this.realm.hashCode());
      result = 31 * result + (this.sName == null ? 0 : this.sName.hashCode());
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else {
         Ticket other = (Ticket)obj;
         if (this.encPart == null) {
            if (other.encPart != null) {
               return false;
            }
         } else if (!this.encPart.equals(other.encPart)) {
            return false;
         }

         if (this.realm == null) {
            if (other.realm != null) {
               return false;
            }
         } else if (!this.realm.equals(other.realm)) {
            return false;
         }

         if (this.sName == null) {
            if (other.sName != null) {
               return false;
            }
         } else if (!this.sName.equals(other.sName)) {
            return false;
         }

         return true;
      }
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("Ticket :\n");
      sb.append(tabs).append("  tkt-vno : ").append(this.getProtocolVersionNumber()).append("\n");
      sb.append(tabs).append("  realm : ").append(this.realm).append("\n");
      sb.append(tabs).append("  sname : ").append(this.sName).append("\n");
      sb.append(tabs).append("  enc-part : ").append(this.encPart).append("\n");
      return sb.toString();
   }

   public String toString() {
      return this.toString("");
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
