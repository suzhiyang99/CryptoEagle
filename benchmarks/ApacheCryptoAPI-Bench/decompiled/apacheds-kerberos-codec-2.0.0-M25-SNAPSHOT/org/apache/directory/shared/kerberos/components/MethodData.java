package org.apache.directory.shared.kerberos.components;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodData implements Asn1Object {
   private static final Logger LOG = LoggerFactory.getLogger(MethodData.class);
   private static final boolean IS_DEBUG;
   private List<PaData> paDatas = new ArrayList();
   private int methodDataLength;

   public void addPaData(PaData paData) {
      this.paDatas.add(paData);
   }

   public boolean contains(PaData paData) {
      return this.paDatas != null ? this.paDatas.contains(paData) : false;
   }

   public int hashCode() {
      int hash = 37;
      if (this.paDatas != null) {
         hash = hash * 17 + this.paDatas.size();

         for(PaData paData : this.paDatas) {
            hash = hash * 17 + paData.hashCode();
         }
      }

      return hash;
   }

   public boolean equals(MethodData that) {
      if (that == null) {
         return false;
      } else if (this.paDatas.size() != that.paDatas.size()) {
         return false;
      } else {
         for(int i = 0; i < this.paDatas.size(); ++i) {
            if (!((PaData)this.paDatas.get(i)).equals(that.paDatas.get(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public PaData[] getPaDatas() {
      return (PaData[])this.paDatas.toArray(new PaData[0]);
   }

   public int computeLength() {
      this.methodDataLength = 0;
      if (this.paDatas != null && this.paDatas.size() != 0) {
         for(PaData paData : this.paDatas) {
            int length = paData.computeLength();
            this.methodDataLength += length;
         }
      }

      return 1 + TLV.getNbBytes(this.methodDataLength) + this.methodDataLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.methodDataLength));
            if (this.paDatas != null && this.paDatas.size() != 0) {
               for(PaData paData : this.paDatas) {
                  paData.encode(buffer);
               }
            }
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_144, new Object[]{1 + TLV.getNbBytes(this.methodDataLength) + this.methodDataLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("METHOD-DATA encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("METHOD-DATA initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      boolean isFirst = true;
      sb.append("METHOD-DATA : ");

      for(PaData paData : this.paDatas) {
         if (isFirst) {
            isFirst = false;
         } else {
            sb.append(", ");
         }

         sb.append(paData.toString());
      }

      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
