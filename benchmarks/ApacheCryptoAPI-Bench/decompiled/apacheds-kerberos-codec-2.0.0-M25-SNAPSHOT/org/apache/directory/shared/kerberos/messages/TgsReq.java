package org.apache.directory.shared.kerberos.messages;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.components.KdcReq;

public class TgsReq extends KdcReq {
   private int kdcReqLength;
   private int tgsReqLength;

   public TgsReq() {
      super(KerberosMessageType.TGS_REQ);
   }

   public int computeLength() {
      this.kdcReqLength = 0;
      this.tgsReqLength = 0;
      this.kdcReqLength = super.computeLength();
      this.tgsReqLength = 1 + TLV.getNbBytes(this.kdcReqLength) + this.kdcReqLength;
      return this.tgsReqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         buffer = ByteBuffer.allocate(this.computeLength());
      }

      buffer.put((byte)108);
      buffer.put(TLV.getBytes(this.kdcReqLength));
      super.encode(buffer);
      return buffer;
   }
}
