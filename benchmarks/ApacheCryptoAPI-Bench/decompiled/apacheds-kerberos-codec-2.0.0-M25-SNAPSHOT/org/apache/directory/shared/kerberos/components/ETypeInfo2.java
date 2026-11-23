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

public class ETypeInfo2 implements Asn1Object {
   private static final Logger LOG = LoggerFactory.getLogger(ETypeInfo2.class);
   private static final boolean IS_DEBUG;
   private List<ETypeInfo2Entry> etypeInfo2Entries;
   private int etypeInfo2Length;

   public ETypeInfo2() {
      this.etypeInfo2Entries = new ArrayList();
   }

   public ETypeInfo2(ETypeInfo2Entry[] etypeInfo2Entries) {
      if (etypeInfo2Entries == null) {
         this.etypeInfo2Entries = new ArrayList();
      } else {
         this.etypeInfo2Entries = Arrays.asList(etypeInfo2Entries);
      }

   }

   public void addETypeInfo2Entry(ETypeInfo2Entry etypeInfo2Entry) {
      this.etypeInfo2Entries.add(etypeInfo2Entry);
   }

   public boolean contains(ETypeInfo2Entry etypeInfo2Entry) {
      return this.etypeInfo2Entries != null ? this.etypeInfo2Entries.contains(etypeInfo2Entry) : false;
   }

   public int hashCode() {
      int hash = 37;
      if (this.etypeInfo2Entries != null) {
         hash = hash * 17 + this.etypeInfo2Entries.size();

         for(ETypeInfo2Entry etypeInfo2Entry : this.etypeInfo2Entries) {
            hash = hash * 17 + etypeInfo2Entry.hashCode();
         }
      }

      return hash;
   }

   public boolean equals(ETypeInfo2 that) {
      if (that == null) {
         return false;
      } else if (this.etypeInfo2Entries.size() != that.etypeInfo2Entries.size()) {
         return false;
      } else {
         for(int i = 0; i < this.etypeInfo2Entries.size(); ++i) {
            if (!((ETypeInfo2Entry)this.etypeInfo2Entries.get(i)).equals(that.etypeInfo2Entries.get(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public ETypeInfo2Entry[] getETypeInfo2Entries() {
      return (ETypeInfo2Entry[])this.etypeInfo2Entries.toArray(new ETypeInfo2Entry[0]);
   }

   public int computeLength() {
      this.etypeInfo2Length = 0;
      if (this.etypeInfo2Entries != null && this.etypeInfo2Entries.size() != 0) {
         for(ETypeInfo2Entry info2Entry : this.etypeInfo2Entries) {
            int length = info2Entry.computeLength();
            this.etypeInfo2Length += length;
         }
      }

      return 1 + TLV.getNbBytes(this.etypeInfo2Length) + this.etypeInfo2Length;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.etypeInfo2Length));
            if (this.etypeInfo2Entries != null && this.etypeInfo2Entries.size() != 0) {
               for(ETypeInfo2Entry info2Entry : this.etypeInfo2Entries) {
                  info2Entry.encode(buffer);
               }
            }
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_144, new Object[]{1 + TLV.getNbBytes(this.etypeInfo2Length) + this.etypeInfo2Length, buffer.capacity()}));
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

      for(ETypeInfo2Entry info2Entry : this.etypeInfo2Entries) {
         if (isFirst) {
            isFirst = false;
         } else {
            sb.append(", ");
         }

         sb.append(info2Entry.toString());
      }

      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
