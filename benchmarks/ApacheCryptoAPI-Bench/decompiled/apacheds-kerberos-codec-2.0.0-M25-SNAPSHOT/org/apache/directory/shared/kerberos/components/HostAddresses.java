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

public class HostAddresses implements Asn1Object {
   private static final Logger LOG = LoggerFactory.getLogger(HostAddresses.class);
   private static final boolean IS_DEBUG;
   private List<HostAddress> addresses;
   private int addressesLength;

   public HostAddresses() {
      this.addresses = new ArrayList();
   }

   public HostAddresses(HostAddress[] addresses) {
      if (addresses == null) {
         this.addresses = new ArrayList();
      } else {
         this.addresses = Arrays.asList(addresses);
      }

   }

   public void addHostAddress(HostAddress hostAddress) {
      this.addresses.add(hostAddress);
   }

   public boolean contains(HostAddress address) {
      return this.addresses != null ? this.addresses.contains(address) : false;
   }

   public int hashCode() {
      int hash = 37;
      if (this.addresses != null) {
         hash = hash * 17 + this.addresses.size();
         hash = 17 + this.addresses.hashCode();
      }

      return hash;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else {
         HostAddresses that = (HostAddresses)obj;
         if (this.addresses.size() != that.addresses.size()) {
            return false;
         } else {
            for(int i = 0; i < this.addresses.size(); ++i) {
               if (!((HostAddress)this.addresses.get(i)).equals(that.addresses.get(i))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public HostAddress[] getAddresses() {
      return (HostAddress[])this.addresses.toArray(new HostAddress[0]);
   }

   public int computeLength() {
      this.addressesLength = 0;
      if (this.addresses != null && this.addresses.size() != 0) {
         for(HostAddress hostAddress : this.addresses) {
            int length = hostAddress.computeLength();
            this.addressesLength += length;
         }
      }

      return 1 + TLV.getNbBytes(this.addressesLength) + this.addressesLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.addressesLength));
            if (this.addresses != null && this.addresses.size() != 0) {
               for(HostAddress hostAddress : this.addresses) {
                  hostAddress.encode(buffer);
               }
            }
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_144, new Object[]{1 + TLV.getNbBytes(this.addressesLength) + this.addressesLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("HostAddresses encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("HostAddresses initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      boolean isFirst = true;

      for(HostAddress hostAddress : this.addresses) {
         if (isFirst) {
            isFirst = false;
         } else {
            sb.append(", ");
         }

         sb.append(hostAddress.toString());
      }

      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
