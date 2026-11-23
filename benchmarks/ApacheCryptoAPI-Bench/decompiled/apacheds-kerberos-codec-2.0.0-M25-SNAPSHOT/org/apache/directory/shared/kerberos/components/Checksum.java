package org.apache.directory.shared.kerberos.components;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.crypto.checksum.ChecksumType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Checksum implements Asn1Object {
   private static final Logger log = LoggerFactory.getLogger(Checksum.class);
   private static final boolean IS_DEBUG;
   private ChecksumType cksumtype;
   private byte[] checksum;
   private int checksumTypeLength;
   private int checksumBytesLength;
   private int checksumLength;

   public Checksum() {
   }

   public Checksum(ChecksumType cksumtype, byte[] checksum) {
      this.cksumtype = cksumtype;
      this.checksum = checksum;
   }

   public int hashCode() {
      int hash = 37;
      hash = hash * 17 + this.cksumtype.hashCode();
      hash = hash * 17 + Arrays.hashCode(this.checksum);
      return hash;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Checksum)) {
         return false;
      } else {
         Checksum that = (Checksum)o;
         return this.cksumtype == that.cksumtype && Arrays.equals(this.checksum, that.checksum);
      }
   }

   public byte[] getChecksumValue() {
      return this.checksum;
   }

   public void setChecksumValue(byte[] checksum) {
      this.checksum = checksum;
   }

   public ChecksumType getChecksumType() {
      return this.cksumtype;
   }

   public void setChecksumType(ChecksumType cksumType) {
      this.cksumtype = cksumType;
   }

   public int computeLength() {
      this.checksumTypeLength = 2 + BerValue.getNbBytes(this.cksumtype.getValue());
      this.checksumLength = 1 + TLV.getNbBytes(this.checksumTypeLength) + this.checksumTypeLength;
      if (this.checksum == null) {
         this.checksumBytesLength = 2;
      } else {
         this.checksumBytesLength = 1 + TLV.getNbBytes(this.checksum.length) + this.checksum.length;
      }

      this.checksumLength += 1 + TLV.getNbBytes(this.checksumBytesLength) + this.checksumBytesLength;
      int checksumSeqLength = 1 + TLV.getNbBytes(this.checksumLength) + this.checksumLength;
      return checksumSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.checksumLength));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.checksumTypeLength));
            BerValue.encode(buffer, this.cksumtype.getValue());
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.checksumBytesLength));
            BerValue.encode(buffer, this.checksum);
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_140, new Object[]{1 + TLV.getNbBytes(this.checksumLength) + this.checksumLength, buffer.capacity()}));
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
      sb.append(tabs).append("Checksum : {\n");
      sb.append(tabs).append("    cksumtype: ").append(this.cksumtype).append('\n');
      if (this.checksum != null) {
         sb.append(tabs + "    checksum:").append(Strings.dumpBytes(this.checksum)).append('\n');
      }

      sb.append(tabs + "}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
