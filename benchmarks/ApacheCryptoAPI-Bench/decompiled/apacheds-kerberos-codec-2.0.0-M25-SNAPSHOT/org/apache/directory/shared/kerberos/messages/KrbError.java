package org.apache.directory.shared.kerberos.messages;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.components.PrincipalName;
import org.apache.directory.shared.kerberos.exceptions.ErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KrbError extends KerberosMessage {
   private static final Logger log = LoggerFactory.getLogger(KrbError.class);
   private static final boolean IS_DEBUG;
   private KerberosTime cTime;
   private Integer cusec;
   private KerberosTime sTime;
   private int susec;
   private ErrorType errorCode;
   private String cRealm;
   private PrincipalName cName;
   private String realm;
   private PrincipalName sName;
   private String eText;
   private byte[] eData;
   private int pvnoLength;
   private int msgTypeLength;
   private int cTimeLength;
   private int cusecLength;
   private int sTimeLength;
   private int susecLength;
   private int errorCodeLength;
   private int cRealmLength;
   private byte[] crealmBytes;
   private int cNameLength;
   private int realmLength;
   private byte[] realmBytes;
   private int sNameLength;
   private int eTextLength;
   private byte[] eTextBytes;
   private int eDataLength;
   private int krbErrorSeqLength;
   private int krbErrorLength;

   public KrbError() {
      super(KerberosMessageType.KRB_ERROR);
   }

   public KerberosTime getCTime() {
      return this.cTime;
   }

   public void setCTime(KerberosTime cTime) {
      this.cTime = cTime;
   }

   public int getCusec() {
      return this.cusec == null ? 0 : this.cusec;
   }

   public void setCusec(int cusec) {
      this.cusec = cusec;
   }

   public KerberosTime getSTime() {
      return this.sTime;
   }

   public void setSTime(KerberosTime sTime) {
      this.sTime = sTime;
   }

   public int getSusec() {
      return this.susec;
   }

   public void setSusec(int susec) {
      this.susec = susec;
   }

   public ErrorType getErrorCode() {
      return this.errorCode;
   }

   public void setErrorCode(ErrorType errorCode) {
      this.errorCode = errorCode;
   }

   public String getCRealm() {
      return this.cRealm;
   }

   public void setCRealm(String cRealm) {
      this.cRealm = cRealm;
   }

   public PrincipalName getCName() {
      return this.cName;
   }

   public void setCName(PrincipalName cName) {
      this.cName = cName;
   }

   public String getRealm() {
      return this.realm;
   }

   public void setRealm(String realm) {
      this.realm = realm;
   }

   public PrincipalName getSName() {
      return this.sName;
   }

   public void setSName(PrincipalName sName) {
      this.sName = sName;
   }

   public String getEText() {
      return this.eText;
   }

   public void setEText(String eText) {
      this.eText = eText;
   }

   public byte[] getEData() {
      return this.eData;
   }

   public void setEData(byte[] eData) {
      this.eData = eData;
   }

   public int computeLength() {
      this.pvnoLength = 3;
      this.krbErrorSeqLength = 1 + TLV.getNbBytes(this.pvnoLength) + this.pvnoLength;
      this.msgTypeLength = 2 + BerValue.getNbBytes(this.getMessageType().getValue());
      this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.msgTypeLength) + this.msgTypeLength;
      if (this.cTime != null) {
         this.cTimeLength = 17;
         this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.cTimeLength) + this.cTimeLength;
      }

      if (this.cusec != null) {
         int cusecLen = BerValue.getNbBytes(this.cusec);
         this.cusecLength = 1 + TLV.getNbBytes(cusecLen) + cusecLen;
         this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.cusecLength) + this.cusecLength;
      }

      this.sTimeLength = 17;
      this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.sTimeLength) + this.sTimeLength;
      int susecLen = BerValue.getNbBytes(this.susec);
      this.susecLength = 1 + TLV.getNbBytes(susecLen) + susecLen;
      this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.susecLength) + this.susecLength;
      this.errorCodeLength = 2 + BerValue.getNbBytes(this.errorCode.getValue());
      this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.errorCodeLength) + this.errorCodeLength;
      if (this.cRealm != null) {
         this.crealmBytes = Strings.getBytesUtf8(this.cRealm);
         this.cRealmLength = 1 + TLV.getNbBytes(this.crealmBytes.length) + this.crealmBytes.length;
         this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.cRealmLength) + this.cRealmLength;
      }

      if (this.cName != null) {
         this.cNameLength = this.cName.computeLength();
         this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.cNameLength) + this.cNameLength;
      }

      this.realmBytes = Strings.getBytesUtf8(this.realm);
      this.realmLength = 1 + TLV.getNbBytes(this.realmBytes.length) + this.realmBytes.length;
      this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.realmLength) + this.realmLength;
      this.sNameLength = this.sName.computeLength();
      this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.sNameLength) + this.sNameLength;
      if (this.eText != null) {
         this.eTextBytes = Strings.getBytesUtf8(this.eText);
         this.eTextLength = 1 + TLV.getNbBytes(this.eTextBytes.length) + this.eTextBytes.length;
         this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.eTextLength) + this.eTextLength;
      }

      if (this.eData != null) {
         this.eDataLength = 1 + TLV.getNbBytes(this.eData.length) + this.eData.length;
         this.krbErrorSeqLength += 1 + TLV.getNbBytes(this.eDataLength) + this.eDataLength;
      }

      this.krbErrorLength = 1 + TLV.getNbBytes(this.krbErrorSeqLength) + this.krbErrorSeqLength;
      return 1 + TLV.getNbBytes(this.krbErrorLength) + this.krbErrorLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         throw new EncoderException(I18n.err(I18n.ERR_148, new Object[0]));
      } else {
         try {
            buffer.put((byte)126);
            buffer.put(TLV.getBytes(this.krbErrorLength));
            buffer.put(UniversalTag.SEQUENCE.getValue());
            buffer.put(TLV.getBytes(this.krbErrorSeqLength));
            buffer.put((byte)-96);
            buffer.put(TLV.getBytes(this.pvnoLength));
            BerValue.encode(buffer, this.getProtocolVersionNumber());
            buffer.put((byte)-95);
            buffer.put(TLV.getBytes(this.msgTypeLength));
            BerValue.encode(buffer, this.getMessageType().getValue());
            if (this.cTimeLength > 0) {
               buffer.put((byte)-94);
               buffer.put(TLV.getBytes(this.cTimeLength));
               buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
               buffer.put((byte)15);
               buffer.put(this.cTime.getBytes());
            }

            if (this.cusec != null) {
               buffer.put((byte)-93);
               buffer.put(TLV.getBytes(this.cusecLength));
               BerValue.encode(buffer, this.cusec);
            }

            buffer.put((byte)-92);
            buffer.put(TLV.getBytes(this.sTimeLength));
            buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
            buffer.put((byte)15);
            buffer.put(this.sTime.getBytes());
            buffer.put((byte)-91);
            buffer.put(TLV.getBytes(this.susecLength));
            BerValue.encode(buffer, this.susec);
            buffer.put((byte)-90);
            buffer.put(TLV.getBytes(this.errorCodeLength));
            BerValue.encode(buffer, this.errorCode.getValue());
            if (this.cRealm != null) {
               buffer.put((byte)-89);
               buffer.put(TLV.getBytes(this.cRealmLength));
               buffer.put(UniversalTag.GENERAL_STRING.getValue());
               buffer.put(TLV.getBytes(this.crealmBytes.length));
               buffer.put(this.crealmBytes);
            }

            if (this.cName != null) {
               buffer.put((byte)-88);
               buffer.put(TLV.getBytes(this.cNameLength));
               this.cName.encode(buffer);
            }

            buffer.put((byte)-87);
            buffer.put(TLV.getBytes(this.realmLength));
            buffer.put(UniversalTag.GENERAL_STRING.getValue());
            buffer.put(TLV.getBytes(this.realmBytes.length));
            buffer.put(this.realmBytes);
            buffer.put((byte)-86);
            buffer.put(TLV.getBytes(this.sNameLength));
            this.sName.encode(buffer);
            if (this.eText != null) {
               buffer.put((byte)-85);
               buffer.put(TLV.getBytes(this.eTextLength));
               buffer.put(UniversalTag.GENERAL_STRING.getValue());
               buffer.put(TLV.getBytes(this.eTextBytes.length));
               buffer.put(this.eTextBytes);
            }

            if (this.eData != null) {
               buffer.put((byte)-84);
               buffer.put(TLV.getBytes(this.eDataLength));
               BerValue.encode(buffer, this.eData);
            }
         } catch (BufferOverflowException boe) {
            log.error(I18n.err(I18n.ERR_734_CANNOT_ENCODE_KRBERROR, new Object[]{1 + TLV.getNbBytes(this.krbErrorLength) + this.krbErrorLength, buffer.capacity()}));
            throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
         }

         if (IS_DEBUG) {
            log.debug("KrbError encoding : {}", Strings.dumpBytes(buffer.array()));
            log.debug("KrbError initial value : {}", this.toString());
         }

         return buffer;
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("\nKRB-ERROR : {\n");
      sb.append("    pvno: ").append(this.getProtocolVersionNumber()).append('\n');
      sb.append("    msgType: ").append(this.getMessageType()).append('\n');
      if (this.cTime != null) {
         sb.append("    cTime: ").append(this.cTime).append('\n');
      }

      if (this.cusec != null) {
         sb.append("    cusec: ").append(this.cusec).append('\n');
      }

      sb.append("    sTime: ").append(this.sTime).append('\n');
      sb.append("    susec: ").append(this.susec).append('\n');
      sb.append("    errorCode: ").append(this.errorCode).append('\n');
      if (this.cRealm != null) {
         sb.append("    cRealm: ").append(this.cRealm).append('\n');
      }

      if (this.cName != null) {
         sb.append("    cName: ").append(this.cName).append('\n');
      }

      sb.append("    realm: ").append(this.realm).append('\n');
      sb.append("    sName: ").append(this.sName).append('\n');
      if (this.eText != null) {
         sb.append("    eText: ").append(this.eText).append('\n');
      }

      if (this.eData != null) {
         sb.append("    eData: ").append(Strings.dumpBytes(this.eData)).append('\n');
      }

      sb.append("}\n");
      return sb.toString();
   }

   static {
      IS_DEBUG = log.isDebugEnabled();
   }
}
