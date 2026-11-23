package org.apache.directory.shared.kerberos.components;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import org.apache.directory.shared.kerberos.codec.types.HostAddrType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostAddress implements Asn1Object {
   private static final Logger LOG = LoggerFactory.getLogger(HostAddress.class);
   private static final boolean IS_DEBUG;
   private HostAddrType addrType;
   private byte[] address;
   private int addrTypeLength;
   private int addressLength;
   private int hostAddressLength;
   private int hostAddressSeqLength;

   public HostAddress() {
   }

   public HostAddress(HostAddrType addrType, byte[] address) {
      this.addrType = addrType;
      this.address = address;
   }

   public HostAddress(InetAddress internetAddress) {
      this.addrType = internetAddress instanceof Inet6Address ? HostAddrType.ADDRTYPE_INET6 : HostAddrType.ADDRTYPE_INET;
      byte[] newAddress = internetAddress.getAddress();
      this.address = new byte[newAddress.length];
      System.arraycopy(newAddress, 0, this.address, 0, newAddress.length);
   }

   public int hashCode() {
      int hash = 37;
      hash = hash * 17 + this.addrType.hashCode();
      if (this.address != null) {
         hash = hash * 17 + Arrays.hashCode(this.address);
      }

      return hash;
   }

   public boolean equals(Object that) {
      if (this == that) {
         return true;
      } else if (!(that instanceof HostAddress)) {
         return false;
      } else {
         HostAddress hostAddress = (HostAddress)that;
         if (this.addrType == hostAddress.addrType && (this.address == null || hostAddress.address != null) && (this.address != null || hostAddress.address == null)) {
            if (this.address != null && hostAddress.address != null) {
               if (this.address.length != hostAddress.address.length) {
                  return false;
               }

               for(int ii = 0; ii < this.address.length; ++ii) {
                  if (this.address[ii] != hostAddress.address[ii]) {
                     return false;
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public byte[] getAddress() {
      return this.address;
   }

   public void setAddress(byte[] addresse) {
      this.address = addresse;
   }

   public int computeLength() {
      this.addrTypeLength = 2 + BerValue.getNbBytes(this.addrType.getValue());
      this.hostAddressLength = 1 + TLV.getNbBytes(this.addrTypeLength) + this.addrTypeLength;
      if (this.address == null) {
         this.addressLength = 2;
      } else {
         this.addressLength = 1 + TLV.getNbBytes(this.address.length) + this.address.length;
      }

      this.hostAddressLength += 1 + TLV.getNbBytes(this.addressLength) + this.addressLength;
      this.hostAddressSeqLength = 1 + TLV.getNbBytes(this.hostAddressLength) + this.hostAddressLength;
      return this.hostAddressSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.hostAddressLength));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.addrTypeLength));
            BerValue.encode(buffer, this.addrType.getValue());
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.addressLength));
            BerValue.encode(buffer, this.address);
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_143, new Object[]{1 + TLV.getNbBytes(this.hostAddressLength) + this.hostAddressLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("Checksum encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("Checksum initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public HostAddrType getAddrType() {
      return this.addrType;
   }

   public void setAddrType(HostAddrType addrType) {
      this.addrType = addrType;
   }

   public void setAddrType(int addrType) {
      this.addrType = HostAddrType.getTypeByOrdinal(addrType);
   }

   public String toString() {
      try {
         return InetAddress.getByAddress(this.address).getHostAddress();
      } catch (UnknownHostException var2) {
         return "Unknow host : " + Strings.utf8ToString(this.address);
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
