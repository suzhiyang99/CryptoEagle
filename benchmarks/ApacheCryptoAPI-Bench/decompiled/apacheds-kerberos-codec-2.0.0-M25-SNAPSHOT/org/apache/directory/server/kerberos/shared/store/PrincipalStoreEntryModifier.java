package org.apache.directory.server.kerberos.shared.store;

import java.util.HashMap;
import java.util.Map;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.StringValue;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.KerberosDecoder;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.codec.types.SamType;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;

public class PrincipalStoreEntryModifier {
   private String distinguishedName;
   private String commonName;
   private KerberosPrincipal principal;
   private String realmName;
   private String userId;
   private int keyVersionNumber;
   private KerberosTime validStart;
   private KerberosTime validEnd;
   private KerberosTime passwordEnd;
   private int maxLife;
   private int maxRenew;
   private int kdcFlags;
   private SamType samType;
   private boolean disabled = false;
   private boolean lockedOut = false;
   private KerberosTime expiration;
   private Map<EncryptionType, EncryptionKey> keyMap;

   public PrincipalStoreEntryModifier() {
      this.expiration = KerberosTime.INFINITY;
   }

   public PrincipalStoreEntry getEntry() {
      return new PrincipalStoreEntry(this.distinguishedName, this.commonName, this.userId, this.principal, this.keyVersionNumber, this.validStart, this.validEnd, this.passwordEnd, this.maxLife, this.maxRenew, this.kdcFlags, this.keyMap, this.realmName, this.samType, this.disabled, this.lockedOut, this.expiration);
   }

   public void setDisabled(boolean disabled) {
      this.disabled = disabled;
   }

   public void setLockedOut(boolean lockedOut) {
      this.lockedOut = lockedOut;
   }

   public void setExpiration(KerberosTime expiration) {
      this.expiration = expiration;
   }

   public void setDistinguishedName(String distinguishedName) {
      this.distinguishedName = distinguishedName;
   }

   public void setCommonName(String commonName) {
      this.commonName = commonName;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public void setKDCFlags(int kdcFlags) {
      this.kdcFlags = kdcFlags;
   }

   public void setKeyMap(Map<EncryptionType, EncryptionKey> keyMap) {
      this.keyMap = keyMap;
   }

   public void setKeyVersionNumber(int keyVersionNumber) {
      this.keyVersionNumber = keyVersionNumber;
   }

   public void setMaxLife(int maxLife) {
      this.maxLife = maxLife;
   }

   public void setMaxRenew(int maxRenew) {
      this.maxRenew = maxRenew;
   }

   public void setPasswordEnd(KerberosTime passwordEnd) {
      this.passwordEnd = passwordEnd;
   }

   public void setPrincipal(KerberosPrincipal principal) {
      this.principal = principal;
   }

   public void setRealmName(String realmName) {
      this.realmName = realmName;
   }

   public void setValidEnd(KerberosTime validEnd) {
      this.validEnd = validEnd;
   }

   public void setValidStart(KerberosTime validStart) {
      this.validStart = validStart;
   }

   public void setSamType(SamType samType) {
      this.samType = samType;
   }

   public Map<EncryptionType, EncryptionKey> reconstituteKeyMap(Attribute krb5key) throws KerberosException, LdapException {
      Map<EncryptionType, EncryptionKey> map = new HashMap();

      for(Value<?> val : krb5key) {
         if (val instanceof StringValue) {
            throw new IllegalStateException(I18n.err(I18n.ERR_626, new Object[0]));
         }

         byte[] encryptionKeyBytes = val.getBytes();
         EncryptionKey encryptionKey = KerberosDecoder.decodeEncryptionKey(encryptionKeyBytes);
         map.put(encryptionKey.getKeyType(), encryptionKey);
      }

      return map;
   }
}
