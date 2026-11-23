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
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionKey implements Asn1Object {
   private static final Logger log = LoggerFactory.getLogger(EncryptionKey.class);
   private static final boolean IS_DEBUG;
   private EncryptionType keyType;
   private byte[] keyValue;
   private int keyVersion;
   private int keyTypeLength;
   private int keyValueLength;
   private int encryptionKeyLength;

   public EncryptionKey() {
   }

   public EncryptionKey(EncryptionType keyType, byte[] keyValue) {
      this.keyType = keyType;
      this.keyValue = keyValue;
   }

   public EncryptionKey(EncryptionType keyType, byte[] keyValue, int keyVersion) {
      this.keyType = keyType;
      this.keyValue = keyValue;
      this.keyVersion = keyVersion;
   }

   public synchronized void destroy() {
      if (this.keyValue != null) {
         Arrays.fill(this.keyValue, (byte)0);
      }

   }

   public EncryptionType getKeyType() {
      return this.keyType;
   }

   public void setKeyType(EncryptionType keyType) {
      this.keyType = keyType;
   }

   public byte[] getKeyValue() {
      return this.keyValue;
   }

   public int getKeyVersion() {
      return this.keyVersion;
   }

   public void setKeyVersion(int keyVersion) {
      this.keyVersion = keyVersion;
   }

   public void setKeyValue(byte[] keyValue) {
      this.keyValue = keyValue;
   }

   public int hashCode() {
      int hash = 37;
      hash = hash * 17 + this.keyType.hashCode();
      hash = hash * 17 + Arrays.hashCode(this.keyValue);
      return hash;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && o instanceof EncryptionKey) {
         EncryptionKey that = (EncryptionKey)o;
         return this.keyType == that.keyType && Arrays.equals(this.keyValue, that.keyValue);
      } else {
         return false;
      }
   }

   public int computeLength() {
      this.keyTypeLength = 2 + BerValue.getNbBytes(this.keyType.getValue());
      this.encryptionKeyLength = 1 + TLV.getNbBytes(this.keyTypeLength) + this.keyTypeLength;
      if (this.keyValue == null) {
         this.keyValueLength = 2;
      } else {
         this.keyValueLength = 1 + TLV.getNbBytes(this.keyValue.length) + this.keyValue.length;
      }

      this.encryptionKeyLength += 1 + TLV.getNbBytes(this.keyValueLength) + this.keyValueLength;
      int encryptionKeySeqLength = 1 + BerValue.getNbBytes(this.encryptionKeyLength) + this.encryptionKeyLength;
      return encryptionKeySeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.encryptionKeyLength));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.keyTypeLength));
            BerValue.encode(buffer, this.keyType.getValue());
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.keyValueLength));
            BerValue.encode(buffer, this.keyValue);
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_142, new Object[]{1 + TLV.getNbBytes(this.encryptionKeyLength) + this.encryptionKeyLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("EncryptionKey encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("EncryptionKey initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      return this.keyType.toString() + " (" + this.keyType.getValue() + ")";
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
