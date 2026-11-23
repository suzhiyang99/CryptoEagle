package org.apache.directory.shared.kerberos.components;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosUtils;
import org.apache.directory.shared.kerberos.codec.types.PrincipalNameType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrincipalName implements Asn1Object {
   private static final Logger LOG = LoggerFactory.getLogger(PrincipalName.class);
   private static final boolean IS_DEBUG;
   private PrincipalNameType nameType;
   private List<String> nameString = new ArrayList();
   private String realm;
   private List<byte[]> nameBytes;
   private int principalNameSeqLength;
   private int principalTypeTagLength;
   private int principalTypeLength;
   private int principalStringsTagLength;
   private int principalStringsSeqLength;

   public PrincipalName() {
   }

   public PrincipalName(KerberosPrincipal principal) {
      try {
         this.nameString = KerberosUtils.getNames(principal);
         this.realm = principal.getRealm();
      } catch (ParseException var3) {
         this.nameString = KerberosUtils.EMPTY_PRINCIPAL_NAME;
      }

      this.nameType = PrincipalNameType.getTypeByValue(principal.getNameType());
   }

   public PrincipalName(String nameString, PrincipalNameType nameType) throws ParseException {
      this.nameString = KerberosUtils.getNames(nameString);
      this.nameType = nameType;
   }

   public PrincipalName(String[] nameParts, int nameType) {
      if (nameParts != null && nameParts.length != 0) {
         List<String> nameComponents = new ArrayList();

         for(String np : nameParts) {
            nameComponents.add(np);
         }

         this.nameString = nameComponents;
         this.nameType = PrincipalNameType.getTypeByValue(nameType);
      } else {
         throw new IllegalArgumentException("Empty name parts");
      }
   }

   public PrincipalName(String nameString, int nameType) {
      try {
         this.nameString = KerberosUtils.getNames(nameString);
      } catch (ParseException pe) {
         throw new IllegalArgumentException(pe);
      }

      this.nameType = PrincipalNameType.getTypeByValue(nameType);
   }

   public PrincipalNameType getNameType() {
      return this.nameType;
   }

   public void setNameType(PrincipalNameType nameType) {
      this.nameType = nameType;
   }

   public void setNameType(int nameType) {
      this.nameType = PrincipalNameType.getTypeByValue(nameType);
   }

   public void setRealm(String realm) {
      this.realm = realm;
   }

   public String getRealm() {
      return this.realm;
   }

   public List<String> getNames() {
      return this.nameString;
   }

   public String getNameString() {
      if (this.nameString != null && this.nameString.size() != 0) {
         StringBuilder sb = new StringBuilder();
         boolean isFirst = true;

         for(String name : this.nameString) {
            if (isFirst) {
               isFirst = false;
            } else {
               sb.append('/');
            }

            sb.append(name);
         }

         return sb.toString();
      } else {
         return "";
      }
   }

   public void addName(String name) {
      if (this.nameString == null) {
         this.nameString = new ArrayList();
      }

      this.nameString.add(name);
   }

   public int computeLength() {
      this.principalTypeLength = BerValue.getNbBytes(this.nameType.getValue());
      this.principalTypeTagLength = 2 + this.principalTypeLength;
      this.principalNameSeqLength = 1 + TLV.getNbBytes(this.principalTypeTagLength) + this.principalTypeTagLength;
      if (this.nameString != null && this.nameString.size() != 0) {
         this.principalStringsSeqLength = 0;
         this.nameBytes = new ArrayList(this.nameString.size());

         for(String name : this.nameString) {
            if (name != null) {
               byte[] bytes = Strings.getBytesUtf8(name);
               this.nameBytes.add(bytes);
               this.principalStringsSeqLength += 1 + TLV.getNbBytes(bytes.length) + bytes.length;
            } else {
               this.nameBytes.add(Strings.EMPTY_BYTES);
               this.principalStringsSeqLength += 2;
            }
         }
      } else {
         this.principalStringsSeqLength = 0;
      }

      this.principalStringsTagLength = 1 + TLV.getNbBytes(this.principalStringsSeqLength) + this.principalStringsSeqLength;
      this.principalNameSeqLength += 1 + TLV.getNbBytes(this.principalStringsTagLength) + this.principalStringsTagLength;
      return 1 + TLV.getNbBytes(this.principalNameSeqLength) + this.principalNameSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.principalNameSeqLength));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.principalTypeTagLength));
            BerValue.encode(buffer, this.nameType.getValue());
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.principalStringsTagLength));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            if (this.nameString != null && this.nameString.size() != 0) {
               buffer.put(TLV.getBytes(this.principalStringsSeqLength));

               for(byte[] name : this.nameBytes) {
                  buffer.put(UniversalTag.GENERAL_STRING.getValue());
                  if (name != null && name.length != 0) {
                     buffer.put(TLV.getBytes(name.length));
                     buffer.put(name);
                  } else {
                     buffer.put((byte)0);
                  }
               }
            } else {
               buffer.put((byte)0);
            }
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_146, new Object[]{1 + TLV.getNbBytes(this.principalNameSeqLength) + this.principalNameSeqLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("PrinipalName encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("PrinipalName initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("{ ");
      sb.append("name-type: ").append(this.nameType.name());
      if (this.nameString != null && this.nameString.size() != 0) {
         sb.append(", name-string : <");
         boolean isFirst = true;

         for(String name : this.nameString) {
            if (isFirst) {
               isFirst = false;
            } else {
               sb.append(", ");
            }

            sb.append('\'').append(name).append('\'');
         }

         sb.append(">");
      } else {
         sb.append(" no name-string");
      }

      if (this.realm != null) {
         sb.append("realm: ").append(this.realm);
      }

      sb.append(" }");
      return sb.toString();
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.nameString == null ? 0 : this.nameString.hashCode());
      result = 31 * result + (this.nameType == null ? 0 : this.nameType.hashCode());
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else {
         PrincipalName other = (PrincipalName)obj;
         if (this.nameString == null) {
            if (other.nameString != null) {
               return false;
            }
         } else if (!this.nameString.equals(other.nameString)) {
            return false;
         }

         return this.nameType == other.nameType;
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
