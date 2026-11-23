package org.apache.directory.shared.kerberos.messages;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
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

public class KrbCred extends KerberosMessage {
   private static final Logger log = LoggerFactory.getLogger(KrbCred.class);
   private static final boolean IS_DEBUG;
   private List<Ticket> tickets;
   private EncryptedData encPart;
   private int pvnoLen;
   private int msgTypeLen;
   private int ticketsSeqLen;
   private int ticketsLen;
   private int encPartLen;
   private int krbCredSeqLen;
   private int krbCredLen;

   public KrbCred() {
      super(5, KerberosMessageType.KRB_CRED);
   }

   public int computeLength() {
      this.pvnoLen = 3;
      this.krbCredSeqLen = 1 + TLV.getNbBytes(this.pvnoLen) + this.pvnoLen;
      this.msgTypeLen = 2 + BerValue.getNbBytes(this.getMessageType().getValue());
      this.krbCredSeqLen += 1 + TLV.getNbBytes(this.msgTypeLen) + this.msgTypeLen;

      for(Ticket t : this.tickets) {
         this.ticketsSeqLen += t.computeLength();
      }

      this.ticketsLen = 1 + TLV.getNbBytes(this.ticketsSeqLen) + this.ticketsSeqLen;
      this.krbCredSeqLen += 1 + TLV.getNbBytes(this.ticketsLen) + this.ticketsLen;
      this.encPartLen = this.encPart.computeLength();
      this.krbCredSeqLen += 1 + TLV.getNbBytes(this.encPartLen) + this.encPartLen;
      this.krbCredLen = 1 + TLV.getNbBytes(this.krbCredSeqLen) + this.krbCredSeqLen;
      return 1 + TLV.getNbBytes(this.krbCredLen) + this.krbCredLen;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put((byte)118);
            buffer.put(TLV.getBytes(this.krbCredLen));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.krbCredSeqLen));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.pvnoLen));
            BerValue.encode(buffer, this.getProtocolVersionNumber());
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.msgTypeLen));
            BerValue.encode(buffer, this.getMessageType().getValue());
            buffer.put((byte)-94);
            buffer.put(TLV.getBytes(this.ticketsLen));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.ticketsSeqLen));

            for(Ticket t : this.tickets) {
               t.encode(buffer);
            }

            buffer.put((byte)-93);
            buffer.put(TLV.getBytes(this.encPartLen));
            this.encPart.encode(buffer);
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_741_CANNOT_ENCODE_KRB_CRED, new Object[]{1 + TLV.getNbBytes(this.krbCredLen) + this.krbCredLen, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("KrbCred encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("KrbCred initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public List<Ticket> getTickets() {
      return this.tickets;
   }

   public void setTickets(List<Ticket> tickets) {
      this.tickets = tickets;
   }

   public EncryptedData getEncPart() {
      return this.encPart;
   }

   public void setEncPart(EncryptedData encPart) {
      this.encPart = encPart;
   }

   public void addTicket(Ticket ticket) {
      if (ticket == null) {
         throw new IllegalArgumentException("null ticket cannot be added");
      } else {
         if (this.tickets == null) {
            this.tickets = new ArrayList();
         }

         this.tickets.add(ticket);
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("KRB-CRED : {\n");
      sb.append("    pvno: ").append(this.getProtocolVersionNumber()).append('\n');
      sb.append("    msg-type: ").append(this.getMessageType()).append('\n');
      sb.append("    tickets: ").append(this.tickets).append('\n');
      sb.append("    en-part: ").append(this.encPart).append('\n');
      sb.append("}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
