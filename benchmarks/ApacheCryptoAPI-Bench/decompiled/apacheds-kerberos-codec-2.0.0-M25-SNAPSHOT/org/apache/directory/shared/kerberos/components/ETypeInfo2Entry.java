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
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ETypeInfo2Entry implements Asn1Object {
   private static final Logger LOG = LoggerFactory.getLogger(ETypeInfo2Entry.class);
   private static final boolean IS_DEBUG;
   private EncryptionType etype;
   private String salt;
   private byte[] saltBytes;
   private byte[] s2kparams;
   private int etypeTagLength;
   private int saltTagLength;
   private int s2kparamsTagLength;
   private int etypeInfo2EntrySeqLength;

   public ETypeInfo2Entry() {
   }

   public ETypeInfo2Entry(EncryptionType etype) {
      this.etype = etype;
   }

   public String getSalt() {
      return this.salt;
   }

   public void setSalt(String salt) {
      this.salt = salt;
   }

   public byte[] getS2kparams() {
      return this.s2kparams;
   }

   public void setS2kparams(byte[] s2kparams) {
      this.s2kparams = s2kparams;
   }

   public EncryptionType getEType() {
      return this.etype;
   }

   public void setEType(EncryptionType etype) {
      this.etype = etype;
   }

   public int computeLength() {
      int etypeLength = BerValue.getNbBytes(this.etype.getValue());
      this.etypeTagLength = 1 + TLV.getNbBytes(etypeLength) + etypeLength;
      this.etypeInfo2EntrySeqLength = 1 + TLV.getNbBytes(this.etypeTagLength) + this.etypeTagLength;
      if (this.salt != null) {
         this.saltBytes = Strings.getBytesUtf8(this.salt);
         this.saltTagLength = 1 + TLV.getNbBytes(this.saltBytes.length) + this.saltBytes.length;
         this.etypeInfo2EntrySeqLength += 1 + TLV.getNbBytes(this.saltTagLength) + this.saltTagLength;
      }

      if (this.s2kparams != null) {
         this.s2kparamsTagLength = 1 + TLV.getNbBytes(this.s2kparams.length) + this.s2kparams.length;
         this.etypeInfo2EntrySeqLength += 1 + TLV.getNbBytes(this.s2kparamsTagLength) + this.s2kparamsTagLength;
      }

      return 1 + TLV.getNbBytes(this.etypeInfo2EntrySeqLength) + this.etypeInfo2EntrySeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.etypeInfo2EntrySeqLength));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.etypeTagLength));
            BerValue.encode(buffer, this.etype.getValue());
            if (this.salt != null) {
               buffer.put((byte)-95);
               buffer.put(TLV.getBytes(this.saltTagLength));
               buffer.put(UniversalTag.GENERAL_STRING.getValue());
               buffer.put(TLV.getBytes(this.saltBytes.length));
               buffer.put(this.saltBytes);
            }

            if (this.s2kparams != null) {
               buffer.put((byte)-94);
               buffer.put(TLV.getBytes(this.saltTagLength));
               BerValue.encode(buffer, this.s2kparams);
            }
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_145, new Object[]{1 + TLV.getNbBytes(this.etypeInfo2EntrySeqLength) + this.etypeInfo2EntrySeqLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("ETYPE-INFO2-ENTRY encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("ETYPE-INFO2-ENTRY initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("ETYPE-INFO2-ENTRY : {\n");
      sb.append("    etype: ").append(this.etype).append('\n');
      if (this.salt != null) {
         sb.append("    salt: ").append(this.salt).append('\n');
      }

      if (this.salt != null) {
         sb.append("    s2kparams: ").append(Strings.dumpBytes(this.s2kparams)).append('\n');
      }

      sb.append("}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
