package org.apache.directory.shared.kerberos.messages;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.Asn1Object;
import org.apache.directory.api.asn1.EncoderException;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.api.util.Strings;
import org.apache.directory.shared.kerberos.components.PrincipalName;

public class ChangePasswdData implements Asn1Object {
   private byte[] newPasswd;
   private PrincipalName targName;
   private String targRealm;
   private int newPasswdLen;
   private int targNameLen;
   private int targRealmLen;
   private int seqLen;

   public int computeLength() {
      this.newPasswdLen = 1 + TLV.getNbBytes(this.newPasswd.length) + this.newPasswd.length;
      this.seqLen = 1 + TLV.getNbBytes(this.newPasswdLen) + this.newPasswdLen;
      if (this.targName != null) {
         this.targNameLen = this.targName.computeLength();
         this.seqLen += 1 + TLV.getNbBytes(this.targNameLen) + this.targNameLen;
      }

      if (this.targRealm != null) {
         this.targRealmLen = Strings.getBytesUtf8(this.targRealm).length;
         this.targRealmLen += 1 + TLV.getNbBytes(this.targRealmLen);
         this.seqLen += 1 + TLV.getNbBytes(this.targRealmLen) + this.targRealmLen;
      }

      return 1 + TLV.getNbBytes(this.seqLen) + this.seqLen;
   }

   public ByteBuffer encode(ByteBuffer buffer) throws EncoderException {
      if (buffer == null) {
         buffer = ByteBuffer.allocate(this.computeLength());
      }

      buffer.put(UniversalTag.SEQUENCE.getValue());
      buffer.put(BerValue.getBytes(this.seqLen));
      buffer.put((byte)-96);
      buffer.put(BerValue.getBytes(this.newPasswdLen));
      BerValue.encode(buffer, this.newPasswd);
      if (this.targName != null) {
         buffer.put((byte)-95);
         buffer.put(BerValue.getBytes(this.targNameLen));
         this.targName.encode(buffer);
      }

      if (this.targRealm != null) {
         buffer.put((byte)-94);
         buffer.put(BerValue.getBytes(this.targRealmLen));
         buffer.put(UniversalTag.GENERAL_STRING.getValue());
         buffer.put(BerValue.getBytes(this.targRealmLen - 2));
         buffer.put(Strings.getBytesUtf8(this.targRealm));
      }

      return buffer;
   }

   public byte[] getNewPasswd() {
      return this.newPasswd;
   }

   public void setNewPasswd(byte[] newPasswd) {
      this.newPasswd = newPasswd;
   }

   public PrincipalName getTargName() {
      return this.targName;
   }

   public void setTargName(PrincipalName targName) {
      this.targName = targName;
   }

   public String getTargRealm() {
      return this.targRealm;
   }

   public void setTargRealm(String targRealm) {
      this.targRealm = targRealm;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("ChangePasswdData : \n");
      sb.append("    newPasswd : ").append(Strings.utf8ToString(this.newPasswd)).append('\n');
      sb.append("    targName : ").append(this.targName).append('\n');
      sb.append("    targRealm : ").append(this.targRealm).append('\n');
      return sb.toString();
   }
}
