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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdAndOr implements Asn1Object {
   private static final Logger LOG = LoggerFactory.getLogger(AdAndOr.class);
   private static final boolean IS_DEBUG;
   private int conditionCount;
   private AuthorizationData elements;
   private int conditionCountTagLength;
   private int elementsTagLength;
   private int adAndOrSeqLength;

   public int getConditionCount() {
      return this.conditionCount;
   }

   public void setConditionCount(int conditionCount) {
      this.conditionCount = conditionCount;
   }

   public AuthorizationData getElements() {
      return this.elements;
   }

   public void setElements(AuthorizationData elements) {
      this.elements = elements;
   }

   public int computeLength() {
      int conditionCountLength = BerValue.getNbBytes(this.conditionCount);
      this.conditionCountTagLength = 1 + TLV.getNbBytes(conditionCountLength) + conditionCountLength;
      this.adAndOrSeqLength = 1 + TLV.getNbBytes(this.conditionCountTagLength) + this.conditionCountTagLength;
      this.elementsTagLength = this.elements.computeLength();
      this.adAndOrSeqLength += 1 + TLV.getNbBytes(this.elementsTagLength) + this.elementsTagLength;
      return 1 + TLV.getNbBytes(this.adAndOrSeqLength) + this.adAndOrSeqLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.adAndOrSeqLength));
            buffer.put((byte)-96);
            buffer.put((byte)this.conditionCountTagLength);
            BerValue.encode(buffer, this.conditionCount);
            buffer.put((byte)-95);
            buffer.put((byte)this.elementsTagLength);
            this.elements.encode(buffer);
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_139, new Object[]{1 + TLV.getNbBytes(this.adAndOrSeqLength) + this.adAndOrSeqLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("AD-AND-OR encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("AD-AND-OR initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      return this.toString("");
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("AD-AND-OR : {\n");
      sb.append(tabs).append("    condition-count: ").append(this.conditionCount).append('\n');
      sb.append(tabs + "    elements:").append(this.elements).append('\n');
      sb.append(tabs + "}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
