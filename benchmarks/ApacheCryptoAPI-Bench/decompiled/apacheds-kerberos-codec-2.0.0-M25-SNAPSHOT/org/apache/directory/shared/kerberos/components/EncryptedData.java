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

public class EncryptedData implements Asn1Object {
   private static final Logger log = LoggerFactory.getLogger(EncryptedData.class);
   private static final boolean IS_DEBUG;
   private EncryptionType eType;
   private int kvno;
   private boolean hasKvno;
   private byte[] cipher;
   public static final boolean HAS_KVNO = true;
   private int eTypeTagLength;
   private int kvnoTagLength;
   private int cipherTagLength;
   private int encryptedDataSeqLength;

   public EncryptedData() {
      this.hasKvno = false;
   }

   public EncryptedData(EncryptionType eType, int kvno, byte[] cipher) {
      this.eType = eType;
      this.hasKvno = kvno > 0;
      this.kvno = kvno;
      this.cipher = cipher;
   }

   public EncryptedData(EncryptionType eType, byte[] cipher) {
      this.eType = eType;
      this.hasKvno = false;
      this.kvno = -1;
      this.cipher = cipher;
   }

   public EncryptionType getEType() {
      return this.eType;
   }

   public void setEType(EncryptionType eType) {
      this.eType = eType;
   }

   public int getKvno() {
      return this.hasKvno ? this.kvno : -1;
   }

   public void setKvno(int kvno) {
      this.kvno = kvno;
      this.hasKvno = true;
   }

   public boolean hasKvno() {
      return this.hasKvno;
   }

   public byte[] getCipher() {
      return this.cipher;
   }

   public void setCipher(byte[] cipher) {
      this.cipher = cipher;
   }

   public int computeLength() {
      this.encryptedDataSeqLength = 0;
      int eTypeLength = BerValue.getNbBytes(this.eType.getValue());
      this.eTypeTagLength = 1 + TLV.getNbBytes(eTypeLength) + eTypeLength;
      this.encryptedDataSeqLength = 1 + TLV.getNbBytes(this.eTypeTagLength) + this.eTypeTagLength;
      if (this.hasKvno) {
         int kvnoLength = BerValue.getNbBytes(this.kvno);
         this.kvnoTagLength = 1 + TLV.getNbBytes(kvnoLength) + kvnoLength;
         this.encryptedDataSeqLength += 1 + TLV.getNbBytes(this.kvnoTagLength) + this.kvnoTagLength;
      } else {
         this.kvnoTagLength = 0;
      }

      if (this.cipher != null && this.cipher.length != 0) {
         this.cipherTagLength = 1 + TLV.getNbBytes(this.cipher.length) + this.cipher.length;
      } else {
         this.cipherTagLength = 2;
      }

      this.encryptedDataSeqLength += 1 + TLV.getNbBytes(this.cipherTagLength) + this.cipherTagLength;
      return 1 + TLV.getNbBytes(this.encryptedDataSeqLength) + this.encryptedDataSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.encryptedDataSeqLength));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.eTypeTagLength));
            BerValue.encode(buffer, this.eType.getValue());
            if (this.hasKvno) {
               buffer.put((byte)-95);
               buffer.put(TLV.getBytes(this.kvnoTagLength));
               BerValue.encode(buffer, this.kvno);
            }

            buffer.put((byte)-94);
            buffer.put(TLV.getBytes(this.cipherTagLength));
            BerValue.encode(buffer, this.cipher);
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_141, new Object[]{1 + TLV.getNbBytes(this.encryptedDataSeqLength) + this.encryptedDataSeqLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("EncryptedData encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("EncryptedData initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + Arrays.hashCode(this.cipher);
      result = 31 * result + (this.eType == null ? 0 : this.eType.hashCode());
      result = 31 * result + this.kvno;
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else {
         EncryptedData other = (EncryptedData)obj;
         if (!Arrays.equals(this.cipher, other.cipher)) {
            return false;
         } else if (this.eType != other.eType) {
            return false;
         } else {
            return this.kvno == other.kvno;
         }
      }
   }

   public String toString() {
      return this.toString("");
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("EncryptedData : {\n");
      sb.append(tabs).append("    etype: ").append(this.eType).append('\n');
      if (this.hasKvno) {
         sb.append(tabs).append("    kvno: ").append(this.kvno).append('\n');
      }

      sb.append(tabs).append("    cipher: ").append(Strings.dumpBytes(this.cipher)).append("\n}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
