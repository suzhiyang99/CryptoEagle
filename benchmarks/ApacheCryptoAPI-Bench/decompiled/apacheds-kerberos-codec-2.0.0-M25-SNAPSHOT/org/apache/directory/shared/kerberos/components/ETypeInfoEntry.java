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

public class ETypeInfoEntry implements Asn1Object {
   private static final Logger LOG = LoggerFactory.getLogger(ETypeInfoEntry.class);
   private static final boolean IS_DEBUG;
   private EncryptionType etype;
   private byte[] salt;
   private int etypeTagLength;
   private int saltTagLength;
   private int etypeInfoEntrySeqLength;

   public ETypeInfoEntry(EncryptionType etype, byte[] salt) {
      this.etype = etype;
      this.salt = salt;
   }

   public ETypeInfoEntry() {
   }

   public byte[] getSalt() {
      return this.salt;
   }

   public void setSalt(byte[] salt) {
      this.salt = salt;
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
      this.etypeInfoEntrySeqLength = 1 + TLV.getNbBytes(this.etypeTagLength) + this.etypeTagLength;
      if (this.salt != null) {
         this.saltTagLength = 1 + TLV.getNbBytes(this.salt.length) + this.salt.length;
         this.etypeInfoEntrySeqLength += 1 + TLV.getNbBytes(this.saltTagLength) + this.saltTagLength;
      }

      return 1 + TLV.getNbBytes(this.etypeInfoEntrySeqLength) + this.etypeInfoEntrySeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.etypeInfoEntrySeqLength));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.etypeTagLength));
            BerValue.encode(buffer, this.etype.getValue());
            if (this.salt != null) {
               buffer.put((byte)-95);
               buffer.put(TLV.getBytes(this.saltTagLength));
               BerValue.encode(buffer, this.salt);
            }
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_145, new Object[]{1 + TLV.getNbBytes(this.etypeInfoEntrySeqLength) + this.etypeInfoEntrySeqLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("ETYPE-INFO-ENTRY encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("ETYPE-INFO-ENTRY initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("ETYPE-INFO-ENTRY : {\n");
      sb.append("    etype: ").append(this.etype).append('\n');
      if (this.salt != null) {
         sb.append("    salt: ").append(Strings.dumpBytes(this.salt)).append('\n');
      }

      sb.append("}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
