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
import org.apache.directory.shared.kerberos.codec.types.TransitedEncodingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransitedEncoding implements Asn1Object {
   private static final Logger log = LoggerFactory.getLogger(TransitedEncoding.class);
   private static final boolean IS_DEBUG;
   private TransitedEncodingType trType;
   private byte[] contents;
   private int trTypeLength;
   private int contentsLength;
   private int transitedEncodingLength;

   public TransitedEncoding() {
      this.trType = TransitedEncodingType.NULL;
      this.contents = Strings.EMPTY_BYTES;
   }

   public byte[] getContents() {
      return this.contents;
   }

   public void setContents(byte[] contents) {
      this.contents = contents;
   }

   public TransitedEncodingType getTrType() {
      return this.trType;
   }

   public void setTrType(TransitedEncodingType trType) {
      this.trType = trType;
   }

   public int computeLength() {
      this.trTypeLength = 2 + BerValue.getNbBytes(this.trType.getValue());
      this.transitedEncodingLength = 1 + TLV.getNbBytes(this.trTypeLength) + this.trTypeLength;
      if (this.contents == null) {
         this.contentsLength = 2;
      } else {
         this.contentsLength = 1 + TLV.getNbBytes(this.contents.length) + this.contents.length;
      }

      this.transitedEncodingLength += 1 + TLV.getNbBytes(this.contentsLength) + this.contentsLength;
      int transitedEncodingSeqLength = 1 + TLV.getNbBytes(this.transitedEncodingLength) + this.transitedEncodingLength;
      return transitedEncodingSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.transitedEncodingLength));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.trTypeLength));
            BerValue.encode(buffer, this.trType.getValue());
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.contentsLength));
            BerValue.encode(buffer, this.contents);
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_147, new Object[]{1 + TLV.getNbBytes(this.transitedEncodingLength) + this.transitedEncodingLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("TransitedEncoding encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("TransitedEncoding initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + Arrays.hashCode(this.contents);
      result = 31 * result + (this.trType == null ? 0 : this.trType.hashCode());
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else {
         TransitedEncoding other = (TransitedEncoding)obj;
         if (!Arrays.equals(this.contents, other.contents)) {
            return false;
         } else {
            return this.trType == other.trType;
         }
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("TransitedEncoding : {\n");
      sb.append("    tr-type: ").append(this.trType).append('\n');
      sb.append("    contents: ").append(Strings.dumpBytes(this.contents)).append("\n}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
