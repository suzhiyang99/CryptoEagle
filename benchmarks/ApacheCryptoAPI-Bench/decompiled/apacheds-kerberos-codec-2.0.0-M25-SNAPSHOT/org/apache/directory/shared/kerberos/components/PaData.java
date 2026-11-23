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
import org.apache.directory.shared.kerberos.codec.types.PaDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaData implements Asn1Object {
   private static final Logger log = LoggerFactory.getLogger(PaData.class);
   private static final boolean IS_DEBUG;
   private PaDataType paDataType;
   private byte[] paDataValue;
   private int paDataTypeTagLength;
   private int paDataValueTagLength;
   private int preAuthenticationDataSeqLength;

   public PaData() {
   }

   public PaData(PaDataType paDataType, byte[] paDataValue) {
      this.paDataType = paDataType;
      this.paDataValue = paDataValue;
   }

   public PaDataType getPaDataType() {
      return this.paDataType;
   }

   public void setPaDataType(int paDataType) {
      this.paDataType = PaDataType.getTypeByValue(paDataType);
   }

   public void setPaDataType(PaDataType paDataType) {
      this.paDataType = paDataType;
   }

   public byte[] getPaDataValue() {
      return this.paDataValue;
   }

   public void setPaDataValue(byte[] paDataValue) {
      this.paDataValue = paDataValue;
   }

   public int computeLength() {
      int paDataTypeLength = BerValue.getNbBytes(this.paDataType.getValue());
      this.paDataTypeTagLength = 1 + TLV.getNbBytes(paDataTypeLength) + paDataTypeLength;
      this.preAuthenticationDataSeqLength = 1 + TLV.getNbBytes(this.paDataTypeTagLength) + this.paDataTypeTagLength;
      if (this.paDataValue == null) {
         this.paDataValueTagLength = 2;
      } else {
         this.paDataValueTagLength = 1 + TLV.getNbBytes(this.paDataValue.length) + this.paDataValue.length;
      }

      this.preAuthenticationDataSeqLength += 1 + TLV.getNbBytes(this.paDataValueTagLength) + this.paDataValueTagLength;
      return 1 + TLV.getNbBytes(this.preAuthenticationDataSeqLength) + this.preAuthenticationDataSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.preAuthenticationDataSeqLength));
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.paDataTypeTagLength));
            BerValue.encode(buffer, this.paDataType.getValue());
            buffer.put((byte)-94);
            buffer.put(TLV.getBytes(this.paDataValueTagLength));
            BerValue.encode(buffer, this.paDataValue);
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_145, new Object[]{1 + TLV.getNbBytes(this.preAuthenticationDataSeqLength) + this.preAuthenticationDataSeqLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("PreAuthenticationData encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("PreAuthenticationData initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      return this.toString("");
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("PreAuthenticationData : \n");
      sb.append(tabs).append("    padata-type: ").append(this.paDataType).append('\n');
      if (this.paDataValue != null) {
         sb.append(tabs + "    padata-value:").append(Strings.dumpBytes(this.paDataValue)).append('\n');
      }

      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
