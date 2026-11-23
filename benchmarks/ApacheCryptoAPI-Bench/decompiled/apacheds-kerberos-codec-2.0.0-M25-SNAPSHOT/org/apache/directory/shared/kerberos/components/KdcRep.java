package org.apache.directory.shared.kerberos.components;

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
import org.apache.directory.shared.kerberos.messages.KerberosMessage;
import org.apache.directory.shared.kerberos.messages.Ticket;

public class KdcRep extends KerberosMessage {
   private List<PaData> paData = new ArrayList();
   private String crealm;
   private byte[] crealmBytes;
   private PrincipalName cname;
   private Ticket ticket;
   private EncryptedData encPart;
   protected EncKdcRepPart encKdcRepPart;
   private int pvnoLength;
   private int msgTypeLength;
   private int paDataLength;
   private int paDataSeqLength;
   private int[] paDataLengths;
   private int cnameLength;
   private int crealmLength;
   private int ticketLength;
   private int encPartLength;
   private int kdcRepSeqLength;
   private int kdcRepLength;

   public KdcRep(KerberosMessageType msgType) {
      super(msgType);
   }

   public int getPvno() {
      return this.getProtocolVersionNumber();
   }

   public void setPvno(int pvno) {
      this.setProtocolVersionNumber(pvno);
   }

   public List<PaData> getPaData() {
      return this.paData;
   }

   public void addPaData(PaData paData) {
      this.paData.add(paData);
   }

   public String getCRealm() {
      return this.crealm;
   }

   public void setCRealm(String crealm) {
      this.crealm = crealm;
   }

   public PrincipalName getCName() {
      return this.cname;
   }

   public void setCName(PrincipalName cname) {
      this.cname = cname;
   }

   public Ticket getTicket() {
      return this.ticket;
   }

   public void setTicket(Ticket ticket) {
      this.ticket = ticket;
   }

   public EncryptedData getEncPart() {
      return this.encPart;
   }

   public void setEncPart(EncryptedData encPart) {
      this.encPart = encPart;
   }

   public EncKdcRepPart getEncKdcRepPart() {
      return this.encKdcRepPart;
   }

   public void setEncKdcRepPart(EncKdcRepPart encKdcRepPart) {
      this.encKdcRepPart = encKdcRepPart;
   }

   public int computeLength() {
      this.pvnoLength = 3;
      this.kdcRepSeqLength = 1 + TLV.getNbBytes(this.pvnoLength) + this.pvnoLength;
      this.msgTypeLength = 3;
      this.kdcRepSeqLength += 1 + TLV.getNbBytes(this.msgTypeLength) + this.msgTypeLength;
      if (this.paData.size() != 0) {
         this.paDataLengths = new int[this.paData.size()];
         int pos = 0;
         this.paDataSeqLength = 0;

         for(PaData paDataElem : this.paData) {
            this.paDataLengths[pos] = paDataElem.computeLength();
            this.paDataSeqLength += this.paDataLengths[pos];
            ++pos;
         }

         this.paDataLength = 1 + TLV.getNbBytes(this.paDataSeqLength) + this.paDataSeqLength;
         this.kdcRepSeqLength += 1 + TLV.getNbBytes(this.paDataLength) + this.paDataLength;
      }

      this.crealmBytes = Strings.getBytesUtf8(this.crealm);
      this.crealmLength = 1 + TLV.getNbBytes(this.crealmBytes.length) + this.crealmBytes.length;
      this.kdcRepSeqLength += 1 + TLV.getNbBytes(this.crealmLength) + this.crealmLength;
      this.cnameLength = this.cname.computeLength();
      this.kdcRepSeqLength += 1 + TLV.getNbBytes(this.cnameLength) + this.cnameLength;
      this.ticketLength = this.ticket.computeLength();
      this.kdcRepSeqLength += 1 + TLV.getNbBytes(this.ticketLength) + this.ticketLength;
      this.encPartLength = this.encPart.computeLength();
      this.kdcRepSeqLength += 1 + TLV.getNbBytes(this.encPartLength) + this.encPartLength;
      this.kdcRepLength = 1 + TLV.getNbBytes(this.kdcRepSeqLength) + this.kdcRepSeqLength;
      return this.kdcRepLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         buffer.put(UniversalTag.SEQUENCE.getValue());
         buffer.put(TLV.getBytes(this.kdcRepSeqLength));
         buffer.put((byte)-96);
         buffer.put(TLV.getBytes(this.pvnoLength));
         BerValue.encode(buffer, this.getProtocolVersionNumber());
         buffer.put((byte)-95);
         buffer.put(TLV.getBytes(this.msgTypeLength));
         BerValue.encode(buffer, this.getMessageType().getValue());
         if (this.paData.size() != 0) {
            buffer.put((byte)-94);
            buffer.put(TLV.getBytes(this.paDataLength));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.paDataSeqLength));

            for(PaData paDataElem : this.paData) {
               paDataElem.encode(buffer);
            }
         }

         buffer.put((byte)-93);
         buffer.put(TLV.getBytes(this.crealmLength));
         buffer.put(UniversalTag.GENERAL_STRING.getValue());
         buffer.put(TLV.getBytes(this.crealmBytes.length));
         buffer.put(this.crealmBytes);
         buffer.put((byte)-92);
         buffer.put(TLV.getBytes(this.cnameLength));
         this.cname.encode(buffer);
         buffer.put((byte)-91);
         buffer.put(TLV.getBytes(this.ticketLength));
         this.ticket.encode(buffer);
         buffer.put((byte)-90);
         buffer.put(TLV.getBytes(this.encPartLength));
         this.encPart.encode(buffer);
         return buffer;
      }
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("\n>-------------------------------------------------------------------------------\n");
      sb.append(tabs).append("KdcRep : ");
      if (this.getMessageType() == KerberosMessageType.AS_REP) {
         sb.append("AS-REP").append('\n');
      } else if (this.getMessageType() == KerberosMessageType.TGS_REP) {
         sb.append("TGS-REP").append('\n');
      } else {
         sb.append("Unknown").append('\n');
      }

      sb.append(tabs).append("pvno : ").append(this.getProtocolVersionNumber()).append('\n');
      sb.append(tabs).append("msg-type : ").append(this.getMessageType()).append('\n');

      for(PaData paDataElem : this.paData) {
         sb.append(tabs).append("padata : ").append(paDataElem.toString(tabs + "    ")).append('\n');
      }

      sb.append(tabs).append("crealm : ").append(this.crealm).append('\n');
      sb.append(tabs).append("cname : ").append(this.cname).append('\n');
      sb.append(this.ticket.toString(tabs));
      sb.append(this.encPart.toString(tabs));
      sb.append(tabs).append("\n-------------------------------------------------------------------------------<\n");
      return sb.toString();
   }

   public String toString() {
      return this.toString("");
   }
}
