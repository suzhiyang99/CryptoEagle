package org.apache.directory.shared.kerberos.components;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ETypeInfo implements Asn1Object {
   private static final Logger LOG = LoggerFactory.getLogger(ETypeInfo.class);
   private static final boolean IS_DEBUG;
   private List<ETypeInfoEntry> etypeInfoEntries;
   private int etypeInfoLength;

   public ETypeInfo() {
      this.etypeInfoEntries = new ArrayList();
   }

   public ETypeInfo(ETypeInfoEntry[] etypeInfoEntries) {
      if (etypeInfoEntries == null) {
         this.etypeInfoEntries = new ArrayList();
      } else {
         this.etypeInfoEntries = Arrays.asList(etypeInfoEntries);
      }

   }

   public void addETypeInfoEntry(ETypeInfoEntry etypeInfoEntry) {
      this.etypeInfoEntries.add(etypeInfoEntry);
   }

   public boolean contains(ETypeInfoEntry etypeInfoEntry) {
      return this.etypeInfoEntries != null ? this.etypeInfoEntries.contains(etypeInfoEntry) : false;
   }

   public int hashCode() {
      int hash = 37;
      if (this.etypeInfoEntries != null) {
         hash = hash * 17 + this.etypeInfoEntries.size();

         for(ETypeInfoEntry etypeInfoEntry : this.etypeInfoEntries) {
            hash = hash * 17 + etypeInfoEntry.hashCode();
         }
      }

      return hash;
   }

   public boolean equals(ETypeInfo that) {
      if (that == null) {
         return false;
      } else if (this.etypeInfoEntries.size() != that.etypeInfoEntries.size()) {
         return false;
      } else {
         for(int i = 0; i < this.etypeInfoEntries.size(); ++i) {
            if (!((ETypeInfoEntry)this.etypeInfoEntries.get(i)).equals(that.etypeInfoEntries.get(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public ETypeInfoEntry[] getETypeInfoEntries() {
      return (ETypeInfoEntry[])this.etypeInfoEntries.toArray(new ETypeInfoEntry[0]);
   }

   public int computeLength() {
      this.etypeInfoLength = 0;
      if (this.etypeInfoEntries != null && this.etypeInfoEntries.size() != 0) {
         for(ETypeInfoEntry infoEntry : this.etypeInfoEntries) {
            int length = infoEntry.computeLength();
            this.etypeInfoLength += length;
         }
      }

      return 1 + TLV.getNbBytes(this.etypeInfoLength) + this.etypeInfoLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.etypeInfoLength));
            if (this.etypeInfoEntries != null && this.etypeInfoEntries.size() != 0) {
               for(ETypeInfoEntry infoEntry : this.etypeInfoEntries) {
                  infoEntry.encode(buffer);
               }
            }
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_144, new Object[]{1 + TLV.getNbBytes(this.etypeInfoLength) + this.etypeInfoLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("ETYPE-INFO encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("ETYPE-INFO initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      boolean isFirst = true;

      for(ETypeInfoEntry infoEntry : this.etypeInfoEntries) {
         if (isFirst) {
            isFirst = false;
         } else {
            sb.append(", ");
         }

         sb.append(infoEntry.toString());
      }

      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
