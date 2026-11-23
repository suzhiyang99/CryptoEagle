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
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.types.LastReqType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastReq implements Asn1Object {
   private static final Logger LOG = LoggerFactory.getLogger(LastReq.class);
   private static final boolean IS_DEBUG;
   private List<LastReqEntry> lastReqs = new ArrayList();
   private LastReqEntry currentLR;
   private int[] lrTypeTagLen;
   private int[] lrValueTagLen;
   private int[] lastReqSeqLen;
   private int lastReqSeqSeqLen;

   public LastReqType getCurrentLrType() {
      return this.currentLR.getLrType();
   }

   public void setCurrentLrType(LastReqType lrType) {
      this.currentLR.setLrType(lrType);
   }

   public KerberosTime getCurrentLrValue() {
      return this.currentLR.getLrValue();
   }

   public void setCurrentLrValue(KerberosTime lrValue) {
      this.currentLR.setLrValue(lrValue);
   }

   public LastReqEntry getCurrentLR() {
      return this.currentLR;
   }

   public void createNewLR() {
      this.currentLR = new LastReqEntry();
      this.lastReqs.add(this.currentLR);
   }

   public void addEntry(LastReqEntry lastReqEntry) {
      this.lastReqs.add(lastReqEntry);
   }

   public List<LastReqEntry> getLastReqs() {
      return this.lastReqs;
   }

   public int computeLength() {
      int i = 0;
      this.lastReqSeqLen = new int[this.lastReqs.size()];
      this.lrTypeTagLen = new int[this.lastReqs.size()];
      this.lrValueTagLen = new int[this.lastReqs.size()];
      this.lastReqSeqSeqLen = 0;

      for(LastReqEntry lre : this.lastReqs) {
         int lrTypeLen = BerValue.getNbBytes(lre.getLrType().getValue());
         this.lrTypeTagLen[i] = 1 + TLV.getNbBytes(lrTypeLen) + lrTypeLen;
         byte[] lrValyeBytes = lre.getLrValue().getBytes();
         this.lrValueTagLen[i] = 1 + TLV.getNbBytes(lrValyeBytes.length) + lrValyeBytes.length;
         this.lastReqSeqLen[i] = 1 + TLV.getNbBytes(this.lrTypeTagLen[i]) + this.lrTypeTagLen[i] + 1 + TLV.getNbBytes(this.lrValueTagLen[i]) + this.lrValueTagLen[i];
         this.lastReqSeqSeqLen += 1 + TLV.getNbBytes(this.lastReqSeqLen[i]) + this.lastReqSeqLen[i];
         ++i;
      }

      return 1 + TLV.getNbBytes(this.lastReqSeqSeqLen) + this.lastReqSeqSeqLen;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.lastReqSeqSeqLen));
            int i = 0;

            for(LastReqEntry lre : this.lastReqs) {
               buffer.put(UniversalTag.SEQUENCE.getValue());
               buffer.put(TLV.getBytes(this.lastReqSeqLen[i]));
               buffer.put((byte)-96);
               buffer.put(TLV.getBytes(this.lrTypeTagLen[i]));
               BerValue.encode(buffer, lre.getLrType().getValue());
               buffer.put((byte)-95);
               buffer.put(TLV.getBytes(this.lrValueTagLen[i]));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(lre.getLrValue().getBytes());
            }
         } catch (BufferOverflowException boe) {
            LOG.error(I18n.err(I18n.ERR_139, new Object[]{1 + TLV.getNbBytes(this.lastReqSeqSeqLen) + this.lastReqSeqSeqLen, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            LOG.debug("LastReq encoding : {}", Strings.dumpBytes(buffer.array()));
            LOG.debug("LastReq initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString(String tabs) {
      StringBuilder sb = new StringBuilder();
      sb.append(tabs).append("LastReq : \n");

      for(LastReqEntry lre : this.lastReqs) {
         sb.append(lre.toString(tabs + "    "));
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
