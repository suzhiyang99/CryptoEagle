package org.apache.directory.shared.kerberos.components;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaEncTsEnc implements Asn1Object {
   private static final Logger log = LoggerFactory.getLogger(PaEncTsEnc.class);
   private static final boolean IS_DEBUG;
   private KerberosTime patimestamp;
   private Integer pausec;
   private int paTimestampLength;
   private int paUsecLength;
   private int paEncTsEncLength;

   public PaEncTsEnc() {
   }

   public PaEncTsEnc(KerberosTime paTimestamp, int pausec) {
      this.patimestamp = paTimestamp;
      this.pausec = pausec;
   }

   public KerberosTime getPaTimestamp() {
      return this.patimestamp;
   }

   public void setPaTimestamp(KerberosTime patimestamp) {
      this.patimestamp = patimestamp;
   }

   public int getPausec() {
      return this.pausec == null ? -1 : this.pausec;
   }

   public void setPausec(int pausec) {
      this.pausec = pausec;
   }

   public int computeLength() {
      this.paTimestampLength = 17;
      this.paEncTsEncLength = 1 + TLV.getNbBytes(this.paTimestampLength) + this.paTimestampLength;
      if (this.pausec != null) {
         int pausecLength = BerValue.getNbBytes(this.pausec);
         this.paUsecLength = 1 + TLV.getNbBytes(pausecLength) + pausecLength;
         this.paEncTsEncLength += 1 + TLV.getNbBytes(this.paUsecLength) + this.paUsecLength;
      }

      return 1 + TLV.getNbBytes(this.paEncTsEncLength) + this.paEncTsEncLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.paEncTsEncLength));
            buffer.put((byte)-96);
            buffer.put((byte)17);
            buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
            buffer.put((byte)15);
            buffer.put(this.patimestamp.getBytes());
            if (this.pausec != null) {
               buffer.put((byte)-95);
               buffer.put(TLV.getBytes(this.paUsecLength));
               BerValue.encode(buffer, this.pausec);
            }
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_140, new Object[]{1 + TLV.getNbBytes(this.paEncTsEncLength) + this.paEncTsEncLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("Checksum encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("Checksum initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      return this.toString("");
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("PA-ENC-TS-ENC : {\n");
      sb.append(tabs).append("    patimestamp : ").append(this.patimestamp).append('\n');
      if (this.pausec != null) {
         sb.append(tabs + "    pausec :").append(this.pausec).append('\n');
      }

      sb.append(tabs + "}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
