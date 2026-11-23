package org.apache.directory.server.kerberos.shared.store;

import java.util.Map;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.codec.types.SamType;
import org.apache.directory.shared.kerberos.components.EncryptionKey;

public class PrincipalStoreEntry {
   private String distinguishedName;
   private String commonName;
   private KerberosPrincipal principal;
   private String realmName;
   private String userId;
   private KerberosTime validStart;
   private KerberosTime validEnd;
   private KerberosTime passwordEnd;
   private int keyVersionNumber;
   private int maxLife;
   private int maxRenew;
   private int kdcFlags;
   private SamType samType;
   private boolean disabled;
   private boolean lockedOut;
   private KerberosTime expiration;
   private Map<EncryptionType, EncryptionKey> keyMap;

   PrincipalStoreEntry(String distinguishedName, String commonName, String userId, KerberosPrincipal principal, int keyVersionNumber, KerberosTime validStart, KerberosTime validEnd, KerberosTime passwordEnd, int maxLife, int maxRenew, int kdcFlags, Map<EncryptionType, EncryptionKey> keyMap, String realmName, SamType samType, boolean disabled, boolean lockedOut, KerberosTime expiration) {
      this.distinguishedName = distinguishedName;
      this.commonName = commonName;
      this.userId = userId;
      this.principal = principal;
      this.validStart = validStart;
      this.validEnd = validEnd;
      this.passwordEnd = passwordEnd;
      this.keyVersionNumber = keyVersionNumber;
      this.maxLife = maxLife;
      this.maxRenew = maxRenew;
      this.kdcFlags = kdcFlags;
      this.realmName = realmName;
      this.disabled = disabled;
      this.lockedOut = lockedOut;
      this.expiration = expiration;
      this.samType = samType;
      this.keyMap = keyMap;
   }

   public boolean isDisabled() {
      return this.disabled;
   }

   public boolean isLockedOut() {
      return this.lockedOut;
   }

   public KerberosTime getExpiration() {
      return this.expiration;
   }

   public String getDistinguishedName() {
      return this.distinguishedName;
   }

   public String getCommonName() {
      return this.commonName;
   }

   public String getUserId() {
      return this.userId;
   }

   public Map<EncryptionType, EncryptionKey> getKeyMap() {
      return this.keyMap;
   }

   public int getKDCFlags() {
      return this.kdcFlags;
   }

   public int getKeyVersionNumber() {
      return this.keyVersionNumber;
   }

   public int getMaxLife() {
      return this.maxLife;
   }

   public int getMaxRenew() {
      return this.maxRenew;
   }

   public KerberosTime getPasswordEnd() {
      return this.passwordEnd;
   }

   public KerberosPrincipal getPrincipal() {
      return this.principal;
   }

   public String getRealmName() {
      return this.realmName;
   }

   public KerberosTime getValidEnd() {
      return this.validEnd;
   }

   public KerberosTime getValidStart() {
      return this.validStart;
   }

   public SamType getSamType() {
      return this.samType;
   }
}
