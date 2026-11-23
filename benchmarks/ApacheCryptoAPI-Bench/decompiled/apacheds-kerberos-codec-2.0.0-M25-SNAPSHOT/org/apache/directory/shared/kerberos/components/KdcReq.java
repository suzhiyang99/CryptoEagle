package org.apache.directory.shared.kerberos.components;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.messages.KerberosMessage;

public abstract class KdcReq extends KerberosMessage {
   private List<PaData> paData = new ArrayList();
   private KdcReqBody kdcReqBody;
   private int pvnoLength;
   private int msgTypeLength;
   private int paDataLength;
   private int paDataSeqLength;
   private int[] paDataLengths;
   private int kdcReqBodyLength;
   private int kdcReqSeqLength;
   private int kdcReqLength;

   public KdcReq(KerberosMessageType msgType) {
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

   public KdcReqBody getKdcReqBody() {
      return this.kdcReqBody;
   }

   public void setKdcReqBody(KdcReqBody kdcReqBody) {
      this.kdcReqBody = kdcReqBody;
   }

   public int computeLength() {
      this.pvnoLength = 3;
      this.kdcReqSeqLength = 1 + TLV.getNbBytes(this.pvnoLength) + this.pvnoLength;
      this.msgTypeLength = 3;
      this.kdcReqSeqLength += 1 + TLV.getNbBytes(this.msgTypeLength) + this.msgTypeLength;
      if (this.paData.size() > 0) {
         this.paDataLengths = new int[this.paData.size()];
         int pos = 0;
         this.paDataSeqLength = 0;

         for(PaData paDataElem : this.paData) {
            this.paDataLengths[pos] = paDataElem.computeLength();
            this.paDataSeqLength += this.paDataLengths[pos];
            ++pos;
         }

         this.paDataLength = 1 + TLV.getNbBytes(this.paDataSeqLength) + this.paDataSeqLength;
         this.kdcReqSeqLength += 1 + TLV.getNbBytes(this.paDataLength) + this.paDataLength;
      }

      this.kdcReqBodyLength = this.kdcReqBody.computeLength();
      this.kdcReqSeqLength += 1 + TLV.getNbBytes(this.kdcReqBodyLength) + this.kdcReqBodyLength;
      this.kdcReqLength = 1 + TLV.getNbBytes(this.kdcReqSeqLength) + this.kdcReqSeqLength;
      return this.kdcReqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         buffer.put(UniversalTag.SEQUENCE.getValue());
         buffer.put(TLV.getBytes(this.kdcReqSeqLength));
         buffer.put((byte)-95);
         buffer.put(TLV.getBytes(this.pvnoLength));
         BerValue.encode(buffer, this.getProtocolVersionNumber());
         buffer.put((byte)-94);
         buffer.put(TLV.getBytes(this.msgTypeLength));
         BerValue.encode(buffer, this.getMessageType().getValue());
         if (this.paData.size() > 0) {
            buffer.put((byte)-93);
            buffer.put(TLV.getBytes(this.paDataLength));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.paDataSeqLength));

            for(PaData paDataElem : this.paData) {
               paDataElem.encode(buffer);
            }
         }

         buffer.put((byte)-92);
         buffer.put(TLV.getBytes(this.kdcReqBodyLength));
         this.kdcReqBody.encode(buffer);
         return buffer;
      }
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("\n>-------------------------------------------------------------------------------\n");
      if (this.getMessageType() == KerberosMessageType.AS_REQ) {
         sb.append(tabs).append("AS-REQ").append('\n');
      } else if (this.getMessageType() == KerberosMessageType.TGS_REQ) {
         sb.append(tabs).append("TGS-REQ").append('\n');
      } else {
         sb.append(tabs).append("Unknown").append('\n');
      }

      sb.append(tabs).append("pvno : ").append(this.getProtocolVersionNumber()).append('\n');
      sb.append(tabs).append("msg-type : ").append(this.getMessageType()).append("\n");

      for(PaData paDataElem : this.paData) {
         sb.append(tabs).append("padata :\n");
         sb.append(paDataElem.toString(tabs + "    ")).append('\n');
      }

      sb.append(tabs).append("kdc-req-body : \n");
      sb.append(this.kdcReqBody.toString(tabs + "    ")).append('\n');
      sb.append(tabs).append("\n-------------------------------------------------------------------------------<\n");
      return sb.toString();
   }

   public String toString() {
      return this.toString("");
   }
}
