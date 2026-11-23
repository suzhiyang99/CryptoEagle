package org.apache.directory.shared.kerberos.codec;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;

public class KerberosEncoder {
   public static ByteBuffer encode(Asn1Object asn1Obj, boolean isTcp) throws IOException {
      ByteBuffer kerberosMessage = null;
      int responseLength = asn1Obj.computeLength();
      int bufferLen = responseLength;
      if (isTcp) {
         bufferLen = responseLength + 4;
      }

      kerberosMessage = ByteBuffer.allocate(bufferLen);
      if (isTcp) {
         kerberosMessage.putInt(responseLength);
      }

      try {
         asn1Obj.encode(kerberosMessage);
         kerberosMessage.flip();
         return kerberosMessage;
      } catch (EncoderException e) {
         throw new IOException(e.getMessage());
      }
   }
}
