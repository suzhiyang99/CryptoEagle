package org.apache.directory.shared.kerberos.messages;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.components.KdcReq;

public class AsReq extends KdcReq {
   private int kdcReqLength;

   public AsReq() {
      super(KerberosMessageType.AS_REQ);
   }

   public int computeLength() {
      this.kdcReqLength = super.computeLength();
      return 1 + TLV.getNbBytes(this.kdcReqLength) + this.kdcReqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         buffer = ByteBuffer.allocate(this.computeLength());
      }

      buffer.put((byte)106);
      buffer.put(TLV.getBytes(this.kdcReqLength));
      super.encode(buffer);
      return buffer;
   }
}
