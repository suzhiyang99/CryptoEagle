package org.apache.directory.shared.kerberos.messages;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.components.KdcRep;
import org.apache.directory.shared.kerberos.components.PrincipalName;
import org.apache.directory.shared.kerberos.flags.TicketFlags;

public class AsRep extends KdcRep {
   private int kdcRepLength;
   private int asRepLength;

   public AsRep() {
      super(KerberosMessageType.AS_REP);
   }

   public KerberosTime getEndTime() {
      return this.encKdcRepPart.getEndTime();
   }

   public TicketFlags getFlags() {
      return this.encKdcRepPart.getFlags();
   }

   public int getNonce() {
      return this.encKdcRepPart.getNonce();
   }

   public KerberosTime getRenewTill() {
      return this.encKdcRepPart.getRenewTill();
   }

   public KerberosTime getStartTime() {
      return this.encKdcRepPart.getStartTime();
   }

   public PrincipalName getSName() {
      return this.encKdcRepPart.getSName();
   }

   public int computeLength() {
      this.kdcRepLength = super.computeLength();
      this.asRepLength = 1 + TLV.getNbBytes(this.kdcRepLength) + this.kdcRepLength;
      return this.asRepLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         buffer = ByteBuffer.allocate(this.computeLength());
      }

      buffer.put((byte)107);
      buffer.put(TLV.getBytes(this.kdcRepLength));
      super.encode(buffer);
      return buffer;
   }
}
