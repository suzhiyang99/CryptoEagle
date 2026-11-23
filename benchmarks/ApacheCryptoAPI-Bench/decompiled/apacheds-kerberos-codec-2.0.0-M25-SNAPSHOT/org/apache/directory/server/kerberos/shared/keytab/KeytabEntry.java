package org.apache.directory.server.kerberos.shared.keytab;

import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

public class KeytabEntry {
   private String principalName;
   private int principalType;
   private KerberosTime timeStamp;
   private byte keyVersion;
   private EncryptionKey key;

   public KeytabEntry(String principalName, int principalType, KerberosTime timeStamp, byte keyVersion, EncryptionKey key) {
      this.principalName = principalName;
      this.principalType = principalType;
      this.timeStamp = timeStamp;
      this.keyVersion = keyVersion;
      this.key = key;
   }

   public EncryptionKey getKey() {
      return this.key;
   }

   public byte getKeyVersion() {
      return this.keyVersion;
   }

   public String getPrincipalName() {
      return this.principalName;
   }

   public int getPrincipalType() {
      return this.principalType;
   }

   public KerberosTime getTimeStamp() {
      return this.timeStamp;
   }
}
