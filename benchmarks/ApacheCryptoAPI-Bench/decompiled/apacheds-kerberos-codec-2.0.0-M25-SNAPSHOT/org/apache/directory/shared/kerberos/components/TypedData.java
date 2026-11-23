package org.apache.directory.shared.kerberos.components;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypedData implements Asn1Object {
   private List<TD> typedDataList = new ArrayList();
   private TD currentTD;
   private static final Logger LOG = LoggerFactory.getLogger(TypedData.class);
   private static final boolean IS_DEBUG;
   private int[] dataTypeTagLength;
   private int[] dataValueTagLength;
   private int[] typedDataSeqLength;
   private int typedDataSeqSeqLength;

   public int getCurrentDataType() {
      return this.currentTD.dataType;
   }

   public void setCurrentDataType(int tdType) {
      this.currentTD.dataType = tdType;
   }

   public byte[] getCurrentDataValue() {
      return this.currentTD.dataValue;
   }

   public void setCurrentDataValue(byte[] tdData) {
      this.currentTD.dataValue = tdData;
   }

   public TD getCurrentTD() {
      return this.currentTD;
   }

   public void createNewTD() {
      this.currentTD = new TD();
      this.typedDataList.add(this.currentTD);
   }

   public List<TD> getTypedData() {
      return this.typedDataList;
   }

   public int computeLength() {
      int i = 0;
      this.typedDataSeqLength = new int[this.typedDataList.size()];
      this.dataTypeTagLength = new int[this.typedDataList.size()];
      this.dataValueTagLength = new int[this.typedDataList.size()];
      this.typedDataSeqSeqLength = 0;

      for(TD td : this.typedDataList) {
         int adTypeLen = BerValue.getNbBytes(td.dataType);
         this.dataTypeTagLength[i] = 1 + TLV.getNbBytes(adTypeLen) + adTypeLen;
         this.typedDataSeqLength[i] = 1 + TLV.getNbBytes(this.dataTypeTagLength[i]) + this.dataTypeTagLength[i];
         if (td.dataValue != null) {
            this.dataValueTagLength[i] = 1 + TLV.getNbBytes(td.dataValue.length) + td.dataValue.length;
            int[] var10000 = this.typedDataSeqLength;
            var10000[i] += 1 + TLV.getNbBytes(this.dataValueTagLength[i]) + this.dataValueTagLength[i];
         }

         this.typedDataSeqSeqLength += 1 + TLV.getNbBytes(this.typedDataSeqLength[i]) + this.typedDataSeqLength[i];
         ++i;
      }

      return 1 + TLV.getNbBytes(this.typedDataSeqSeqLength) + this.typedDataSeqSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.typedDataSeqSeqLength));
            int i = 0;

            for(TD td : this.typedDataList) {
               buffer.put(UniversalTag.SEQUENCE.getValue());
               buffer.put(TLV.getBytes(this.typedDataSeqLength[i]));
               buffer.put((byte)-96);
               buffer.put(TLV.getBytes(this.dataTypeTagLength[i]));
               BerValue.encode(buffer, td.dataType);
               if (td.dataValue != null) {
                  buffer.put((byte)-95);
                  buffer.put(TLV.getBytes(this.dataValueTagLength[i]));
                  BerValue.encode(buffer, td.dataValue);
               }

               ++i;
            }
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_743_CANNOT_ENCODE_TYPED_DATA, new Object[]{1 + TLV.getNbBytes(this.typedDataSeqSeqLength) + this.typedDataSeqSeqLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("TypedData encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("TypedData initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      return this.toString("");
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("TypedData : \n");

      for(TD td : this.typedDataList) {
         sb.append(tabs).append("    {\n");
         sb.append(tabs).append("        tdType: ").append(td.dataType).append('\n');
         if (td.dataValue != null) {
            sb.append(tabs).append("        tdData: ").append(Strings.dumpBytes(td.dataValue)).append('\n');
         }

         sb.append(tabs).append("    }\n");
      }

      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }

   public class TD {
      private int dataType;
      private byte[] dataValue;

      public int getDataType() {
         return this.dataType;
      }

      public byte[] getDataValue() {
         return this.dataValue;
      }
   }
}
