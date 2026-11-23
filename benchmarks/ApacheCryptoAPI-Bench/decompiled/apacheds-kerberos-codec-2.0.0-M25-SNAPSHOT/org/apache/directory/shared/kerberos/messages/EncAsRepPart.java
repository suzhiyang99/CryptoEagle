package org.apache.directory.shared.kerberos.messages;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.components.EncKdcRepPart;

public class EncAsRepPart extends KerberosMessage {
   private EncKdcRepPart encKdcRepPart;
   private int encKdcRepPartLength;

   public EncAsRepPart() {
      super(KerberosMessageType.ENC_AS_REP_PART);
   }

   public EncKdcRepPart getEncKdcRepPart() {
      return this.encKdcRepPart;
   }

   public void setEncKdcRepPart(EncKdcRepPart encKdcRepPart) {
      this.encKdcRepPart = encKdcRepPart;
   }

   public int computeLength() {
      this.encKdcRepPartLength = this.encKdcRepPart.computeLength();
      return 1 + TLV.getNbBytes(this.encKdcRepPartLength) + this.encKdcRepPartLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         buffer = ByteBuffer.allocate(this.computeLength());
      }

      buffer.put((byte)121);
      buffer.put(TLV.getBytes(this.encKdcRepPartLength));
      this.encKdcRepPart.encode(buffer);
      return buffer;
   }
}
