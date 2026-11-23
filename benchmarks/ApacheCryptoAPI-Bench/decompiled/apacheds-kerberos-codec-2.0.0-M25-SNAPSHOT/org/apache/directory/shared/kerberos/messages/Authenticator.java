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
import org.apache.directory.shared.kerberos.components.AuthorizationData;
import org.apache.directory.shared.kerberos.components.Checksum;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.components.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Authenticator extends KerberosMessage {
   private static final Logger LOG = LoggerFactory.getLogger(Authenticator.class);
   private static final boolean IS_DEBUG;
   private int versionNumber = this.getProtocolVersionNumber();
   private String crealm;
   private PrincipalName cname;
   private Checksum cksum;
   private int cusec;
   private KerberosTime ctime;
   private EncryptionKey subKey;
   private Integer seqNumber;
   private AuthorizationData authorizationData;
   private int authenticatorVnoLength;
   private int crealmLength;
   private byte[] crealmBytes;
   private int cnameLength;
   private int cksumLength;
   private int cusecLength;
   private int ctimeLength;
   private int subkeyLength;
   private int seqNumberLength;
   private int authorizationDataLength;
   private int authenticatorSeqLength;
   private int authenticatorLength;

   public Authenticator() {
      super(KerberosMessageType.AUTHENTICATOR);
   }

   public AuthorizationData getAuthorizationData() {
      return this.authorizationData;
   }

   public void setAuthorizationData(AuthorizationData authorizationData) {
      this.authorizationData = authorizationData;
   }

   public Checksum getCksum() {
      return this.cksum;
   }

   public void setCksum(Checksum cksum) {
      this.cksum = cksum;
   }

   public PrincipalName getCName() {
      return this.cname;
   }

   public void setCName(PrincipalName cname) {
      this.cname = cname;
   }

   public String getCRealm() {
      return this.crealm;
   }

   public void setCRealm(String crealm) {
      this.crealm = crealm;
   }

   public KerberosTime getCtime() {
      return this.ctime;
   }

   public void setCTime(KerberosTime ctime) {
      this.ctime = ctime;
   }

   public int getCusec() {
      return this.cusec;
   }

   public void setCusec(int cusec) {
      this.cusec = cusec;
   }

   public Integer getSeqNumber() {
      return this.seqNumber;
   }

   public void setSeqNumber(int seqNumber) {
      this.seqNumber = seqNumber;
   }

   public EncryptionKey getSubKey() {
      return this.subKey;
   }

   public void setSubKey(EncryptionKey subKey) {
      this.subKey = subKey;
   }

   public int getVersionNumber() {
      return this.versionNumber;
   }

   public void setVersionNumber(int versionNumber) {
      this.versionNumber = versionNumber;
   }

   public int computeLength() {
      this.reset();
      this.authenticatorVnoLength = 2 + BerValue.getNbBytes(this.getVersionNumber());
      this.authenticatorSeqLength = 1 + TLV.getNbBytes(this.authenticatorVnoLength) + this.authenticatorVnoLength;
      this.crealmBytes = Strings.getBytesUtf8(this.crealm);
      this.crealmLength = 1 + TLV.getNbBytes(this.crealmBytes.length) + this.crealmBytes.length;
      this.authenticatorSeqLength += 1 + TLV.getNbBytes(this.crealmLength) + this.crealmLength;
      this.cnameLength = this.cname.computeLength();
      this.authenticatorSeqLength += 1 + TLV.getNbBytes(this.cnameLength) + this.cnameLength;
      if (this.cksum != null) {
         this.cksumLength = this.cksum.computeLength();
         this.authenticatorSeqLength += 1 + TLV.getNbBytes(this.cksumLength) + this.cksumLength;
      }

      this.cusecLength = 2 + BerValue.getNbBytes(this.cusec);
      this.authenticatorSeqLength += 1 + TLV.getNbBytes(this.cusecLength) + this.cusecLength;
      this.ctimeLength = 17;
      this.authenticatorSeqLength += 2 + this.ctimeLength;
      if (this.subKey != null) {
         this.subkeyLength = this.subKey.computeLength();
         this.authenticatorSeqLength += 1 + TLV.getNbBytes(this.subkeyLength) + this.subkeyLength;
      }

      if (this.seqNumber != null) {
         this.seqNumberLength = 2 + BerValue.getNbBytes(this.seqNumber);
         this.authenticatorSeqLength += 1 + TLV.getNbBytes(this.seqNumberLength) + this.seqNumberLength;
      }

      if (this.authorizationData != null) {
         this.authorizationDataLength = this.authorizationData.computeLength();
         this.authenticatorSeqLength += 1 + TLV.getNbBytes(this.authorizationDataLength) + this.authorizationDataLength;
      }

      this.authenticatorLength = 1 + TLV.getNbBytes(this.authenticatorSeqLength) + this.authenticatorSeqLength;
      return 1 + TLV.getNbBytes(this.authenticatorLength) + this.authenticatorLength;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         buffer = ByteBuffer.allocate(this.computeLength());
      }

      try {
         buffer.put((byte)98);
         buffer.put(TLV.getBytes(this.authenticatorLength));
         buffer.put(UniversalTag.SEQUENCE.getValue());
         buffer.put(TLV.getBytes(this.authenticatorSeqLength));
         buffer.put((byte)-96);
         buffer.put(TLV.getBytes(this.authenticatorVnoLength));
         BerValue.encode(buffer, this.getVersionNumber());
         buffer.put((byte)-95);
         buffer.put(TLV.getBytes(this.crealmLength));
         buffer.put(UniversalTag.GENERAL_STRING.getValue());
         buffer.put(TLV.getBytes(this.crealmBytes.length));
         buffer.put(this.crealmBytes);
         buffer.put((byte)-94);
         buffer.put(TLV.getBytes(this.cnameLength));
         this.cname.encode(buffer);
         if (this.cksum != null) {
            buffer.put((byte)-93);
            buffer.put(TLV.getBytes(this.cksumLength));
            this.cksum.encode(buffer);
         }

         buffer.put((byte)-92);
         buffer.put(TLV.getBytes(this.cusecLength));
         BerValue.encode(buffer, this.cusec);
         buffer.put((byte)-91);
         buffer.put(TLV.getBytes(this.ctimeLength));
         buffer.put(UniversalTag.GENERALIZED_TIME.getValue());
         buffer.put((byte)15);
         buffer.put(this.ctime.getBytes());
         if (this.subKey != null) {
            buffer.put((byte)-90);
            buffer.put(TLV.getBytes(this.subkeyLength));
            this.subKey.encode(buffer);
         }

         if (this.seqNumber != null) {
            buffer.put((byte)-89);
            buffer.put(TLV.getBytes(this.seqNumberLength));
            BerValue.encode(buffer, this.seqNumber);
         }

         if (this.authorizationData != null) {
            buffer.put((byte)-88);
            buffer.put(TLV.getBytes(this.authorizationDataLength));
            this.authorizationData.encode(buffer);
         }
      } catch (BufferOverflowException boe) {
         LOG.error(I18n.err(I18n.ERR_139, new Object[]{1 + TLV.getNbBytes(0) + 0, buffer.capacity()}));
         throw new EncoderException(I18n.err(I18n.ERR_138, new Object[0]), boe);
      }

      if (IS_DEBUG) {
         LOG.debug("Authenticator encoding : {}", Strings.dumpBytes(buffer.array()));
         LOG.debug("Authenticator initial value : {}", this.toString());
      }

      return buffer;
   }

   private void reset() {
      this.authenticatorVnoLength = 0;
      this.crealmLength = 0;
      this.crealmBytes = null;
      this.cnameLength = 0;
      this.cksumLength = 0;
      this.cusecLength = 0;
      this.ctimeLength = 0;
      this.subkeyLength = 0;
      this.seqNumberLength = 0;
      this.authorizationDataLength = 0;
      this.authenticatorSeqLength = 0;
      this.authenticatorLength = 0;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Authenticator : \n");
      sb.append("    authenticator-vno : ").append(this.getVersionNumber()).append('\n');
      sb.append("    crealm : ").append(this.crealm).append('\n');
      sb.append("    cname : ").append(this.cname).append('\n');
      if (this.cksum != null) {
         sb.append("    cksum : ").append(this.cksum).append('\n');
      }

      sb.append("    cusec : ").append(this.cusec).append('\n');
      sb.append("    ctime : ").append(this.ctime).append('\n');
      if (this.subKey != null) {
         sb.append("    subkey : ").append(this.subKey).append('\n');
      }

      if (this.seqNumber != null) {
         sb.append("    seq-number : ").append(this.seqNumber).append('\n');
      }

      if (this.authorizationData != null) {
         sb.append("    authorization-data : ").append(this.authorizationData).append('\n');
      }

      return sb.toString();
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
