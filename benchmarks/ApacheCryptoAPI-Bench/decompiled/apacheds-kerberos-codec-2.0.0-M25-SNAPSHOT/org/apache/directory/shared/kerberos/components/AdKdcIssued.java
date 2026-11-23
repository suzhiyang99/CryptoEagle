package org.apache.directory.shared.kerberos.components;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdKdcIssued implements Asn1Object {
   private static final Logger LOG = LoggerFactory.getLogger(AdKdcIssued.class);
   private static final boolean IS_DEBUG;
   private Checksum adChecksum;
   private String irealm;
   private PrincipalName isname;
   private AuthorizationData elements;
   private int adCheksumTagLength;
   private int irealmTagLength;
   private byte[] irealmBytes;
   private int isnameTagLength;
   private int elementsTagLength;
   private int adKdcIssuedSeqLength;

   public AuthorizationData getElements() {
      return this.elements;
   }

   public void setElements(AuthorizationData elements) {
      this.elements = elements;
   }

   public Checksum getAdChecksum() {
      return this.adChecksum;
   }

   public void setAdChecksum(Checksum adChecksum) {
      this.adChecksum = adChecksum;
   }

   public String getIRealm() {
      return this.irealm;
   }

   public void setIRealm(String irealm) {
      this.irealm = irealm;
   }

   public PrincipalName getISName() {
      return this.isname;
   }

   public void setISName(PrincipalName isname) {
      this.isname = isname;
   }

   public int computeLength() {
      this.adCheksumTagLength = this.adChecksum.computeLength();
      this.adKdcIssuedSeqLength = 1 + TLV.getNbBytes(this.adCheksumTagLength) + this.adCheksumTagLength;
      if (this.irealm != null) {
         this.irealmBytes = this.irealm.getBytes();
         this.irealmTagLength = 1 + TLV.getNbBytes(this.irealmBytes.length) + this.irealmBytes.length;
         this.adKdcIssuedSeqLength += 1 + TLV.getNbBytes(this.irealmTagLength) + this.irealmTagLength;
      }

      if (this.isname != null) {
         this.isnameTagLength = this.isname.computeLength();
         this.adKdcIssuedSeqLength += 1 + TLV.getNbBytes(this.isnameTagLength) + this.isnameTagLength;
      }

      this.elementsTagLength = this.elements.computeLength();
      this.adKdcIssuedSeqLength += 1 + TLV.getNbBytes(this.elementsTagLength) + this.elementsTagLength;
      return 1 + TLV.getNbBytes(this.adKdcIssuedSeqLength) + this.adKdcIssuedSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.adKdcIssuedSeqLength));
            buffer.put((byte)-96);
            buffer.put((byte)this.adCheksumTagLength);
            this.adChecksum.encode(buffer);
            if (this.irealm != null) {
               buffer.put((byte)-95);
               buffer.put((byte)this.irealmTagLength);
               buffer.put(UniversalTag.GENERAL_STRING.getValue());
               buffer.put((byte)this.irealmBytes.length);
               buffer.put(this.irealmBytes);
            }

            if (this.isname != null) {
               buffer.put((byte)-94);
               buffer.put((byte)this.isnameTagLength);
               this.isname.encode(buffer);
            }

            buffer.put((byte)-93);
            buffer.put((byte)this.elementsTagLength);
            this.elements.encode(buffer);
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_139, new Object[]{1 + TLV.getNbBytes(this.adKdcIssuedSeqLength) + this.adKdcIssuedSeqLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("AD-KDCIssued encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("AD-KDCIssued initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      return this.toString("");
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("AD-KDCIssued : {\n");
      sb.append(tabs).append("    ad-cheksum: ").append(this.adChecksum.toString(tabs + "    ")).append('\n');
      if (this.irealm != null) {
         sb.append(tabs).append("    i-realm: ").append(this.irealm).append('\n');
      }

      if (this.isname != null) {
         sb.append(tabs).append("    i-sname: ").append(this.isname.toString()).append('\n');
      }

      sb.append(tabs + "    elements:").append(this.elements.toString(tabs + "    ")).append('\n');
      sb.append(tabs + "}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
