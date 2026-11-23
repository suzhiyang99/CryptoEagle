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
import org.apache.directory.shared.kerberos.codec.types.AuthorizationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationData implements Asn1Object {
   private List<AuthorizationDataEntry> authorizationData = new ArrayList();
   private AuthorizationDataEntry currentAD;
   private static final Logger LOG = LoggerFactory.getLogger(EncryptedData.class);
   private static final boolean IS_DEBUG;
   private int[] adTypeTagLen;
   private int[] adDataTagLen;
   private int[] authorizationDataSeqLen;
   private int authorizationDataSeqSeqLen;

   public int computeLength() {
      int i = 0;
      this.authorizationDataSeqLen = new int[this.authorizationData.size()];
      this.adTypeTagLen = new int[this.authorizationData.size()];
      this.adDataTagLen = new int[this.authorizationData.size()];
      this.authorizationDataSeqSeqLen = 0;

      for(AuthorizationDataEntry ad : this.authorizationData) {
         int adTypeLen = BerValue.getNbBytes(ad.getAdType().getValue());
         this.adTypeTagLen[i] = 1 + TLV.getNbBytes(adTypeLen) + adTypeLen;
         this.adDataTagLen[i] = 1 + TLV.getNbBytes(ad.getAdDataRef().length) + ad.getAdDataRef().length;
         this.authorizationDataSeqLen[i] = 1 + TLV.getNbBytes(this.adTypeTagLen[i]) + this.adTypeTagLen[i] + 1 + TLV.getNbBytes(this.adDataTagLen[i]) + this.adDataTagLen[i];
         this.authorizationDataSeqSeqLen += 1 + TLV.getNbBytes(this.authorizationDataSeqLen[i]) + this.authorizationDataSeqLen[i];
         ++i;
      }

      return 1 + TLV.getNbBytes(this.authorizationDataSeqSeqLen) + this.authorizationDataSeqSeqLen;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.authorizationDataSeqSeqLen));
            int i = 0;

            for(AuthorizationDataEntry ad : this.authorizationData) {
               buffer.put(UniversalTag.SEQUENCE.getValue());
               buffer.put(TLV.getBytes(this.authorizationDataSeqLen[i]));
               buffer.put((byte)-96);
               buffer.put(TLV.getBytes(this.adTypeTagLen[i]));
               BerValue.encode(buffer, ad.getAdType().getValue());
               buffer.put((byte)-95);
               buffer.put(TLV.getBytes(this.adDataTagLen[i]));
               BerValue.encode(buffer, ad.getAdDataRef());
               ++i;
            }
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_139, new Object[]{1 + TLV.getNbBytes(this.authorizationDataSeqSeqLen) + this.authorizationDataSeqSeqLen, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("AuthorizationData encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("AuthorizationData initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public AuthorizationType getCurrentAdType() {
      return this.currentAD.getAdType();
   }

   public void setCurrentAdType(AuthorizationType adType) {
      this.currentAD.setAdType(adType);
   }

   public byte[] getCurrentAdData() {
      return this.currentAD.getAdData();
   }

   public void setCurrentAdData(byte[] adData) {
      this.currentAD.setAdData(adData);
   }

   public AuthorizationDataEntry getCurrentAD() {
      return this.currentAD;
   }

   public void createNewAD() {
      this.currentAD = new AuthorizationDataEntry();
      this.authorizationData.add(this.currentAD);
   }

   public void addEntry(AuthorizationDataEntry entry) {
      this.authorizationData.add(entry);
   }

   public List<AuthorizationDataEntry> getAuthorizationData() {
      return this.authorizationData;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.authorizationData == null ? 0 : this.authorizationData.hashCode());
      result = 31 * result + (this.currentAD == null ? 0 : this.currentAD.hashCode());
      return result;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else {
         AuthorizationData other = (AuthorizationData)obj;
         if (this.authorizationData == null) {
            if (other.authorizationData != null) {
               return false;
            }
         } else if (!this.authorizationData.equals(other.authorizationData)) {
            return false;
         }

         if (this.currentAD == null) {
            if (other.currentAD != null) {
               return false;
            }
         } else if (!this.currentAD.equals(other.currentAD)) {
            return false;
         }

         return true;
      }
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("AuthorizationData : \n");

      for(AuthorizationDataEntry ad : this.authorizationData) {
         sb.append(ad.toString(tabs + "    "));
      }

      return sb.toString();
   }

   public String toString() {
      return this.toString("");
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
