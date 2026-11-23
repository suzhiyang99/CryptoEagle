package org.apache.directory.shared.kerberos;

import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.server.kerberos.shared.crypto.encryption.CipherTextHandler;
import org.apache.directory.server.kerberos.shared.crypto.encryption.KeyUsage;
import org.apache.directory.server.kerberos.shared.replay.ReplayCache;
import org.apache.directory.server.kerberos.shared.store.PrincipalStore;
import org.apache.directory.server.kerberos.shared.store.PrincipalStoreEntry;
import org.apache.directory.shared.kerberos.codec.KerberosDecoder;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.components.EncTicketPart;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.components.HostAddress;
import org.apache.directory.shared.kerberos.components.PrincipalName;
import org.apache.directory.shared.kerberos.exceptions.ErrorType;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;
import org.apache.directory.shared.kerberos.messages.ApReq;
import org.apache.directory.shared.kerberos.messages.Authenticator;
import org.apache.directory.shared.kerberos.messages.Ticket;

public class KerberosUtils {
   public static final int NULL = -1;
   public static final List<String> EMPTY_PRINCIPAL_NAME = new ArrayList();
   private static final Map<String, String> cipherAlgoMap = new LinkedHashMap();
   public static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
   public static final SimpleDateFormat UTC_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
   private static final Set<EncryptionType> oldEncTypes = new HashSet();

   public static List<String> getNames(KerberosPrincipal principal) throws ParseException {
      if (principal == null) {
         return EMPTY_PRINCIPAL_NAME;
      } else {
         String names = principal.getName();
         return Strings.isEmpty(names) ? EMPTY_PRINCIPAL_NAME : getNames(names);
      }
   }

   public static List<String> getNames(String principalNames) throws ParseException {
      if (principalNames == null) {
         return EMPTY_PRINCIPAL_NAME;
      } else {
         List<String> nameComponents = new ArrayList();
         char[] chars = principalNames.toCharArray();
         boolean escaped = false;
         boolean done = false;
         int start = 0;
         int pos = 0;

         for(int i = 0; i < chars.length; ++i) {
            pos = i;
            switch (chars[i]) {
               case '/':
                  if (escaped) {
                     escaped = false;
                  } else {
                     if (i - start <= 0) {
                        throw new ParseException(I18n.err(I18n.ERR_628, new Object[0]), i);
                     }

                     String nameComponent = new String(chars, start, i - start);
                     nameComponents.add(nameComponent);
                     start = i + 1;
                  }
                  break;
               case '@':
                  if (escaped) {
                     escaped = false;
                  } else {
                     done = true;
                  }
                  break;
               case '\\':
                  escaped = !escaped;
            }

            if (done) {
               if (i - start <= 0) {
                  throw new ParseException(I18n.err(I18n.ERR_628, new Object[0]), i);
               }

               String nameComponent = new String(chars, start, i - start);
               nameComponents.add(nameComponent);
               start = i + 1;
               break;
            }

            if (i + 1 == chars.length) {
               String nameComponent = new String(chars, start, i - start + 1);
               nameComponents.add(nameComponent);
               break;
            }
         }

         if (escaped) {
            throw new ParseException(I18n.err(I18n.ERR_629, new Object[0]), pos);
         } else {
            return nameComponents;
         }
      }
   }

   public static KerberosPrincipal getKerberosPrincipal(PrincipalName principal, String realm) {
      String name = principal.getNameString();
      if (!Strings.isEmpty(realm)) {
         name = name + '@' + realm;
      }

      return new KerberosPrincipal(name, principal.getNameType().getValue());
   }

   public static EncryptionType getBestEncryptionType(Set<EncryptionType> requestedTypes, Set<EncryptionType> configuredTypes) {
      for(EncryptionType encryptionType : configuredTypes) {
         if (requestedTypes.contains(encryptionType)) {
            return encryptionType;
         }
      }

      return null;
   }

   public static String getEncryptionTypesString(Set<EncryptionType> encryptionTypes) {
      StringBuilder sb = new StringBuilder();
      boolean isFirst = true;

      for(EncryptionType etype : encryptionTypes) {
         if (isFirst) {
            isFirst = false;
         } else {
            sb.append(", ");
         }

         sb.append(etype);
      }

      return sb.toString();
   }

   public static boolean isKerberosString(byte[] value) {
      if (value == null) {
         return false;
      } else {
         for(byte b : value) {
            if (b < 32 || b > 126) {
               return false;
            }
         }

         return true;
      }
   }

   public static String getAlgoNameFromEncType(EncryptionType encType) {
      String cipherName = Strings.toLowerCaseAscii(encType.getName());

      for(String c : cipherAlgoMap.keySet()) {
         if (cipherName.startsWith(c)) {
            return (String)cipherAlgoMap.get(c);
         }
      }

      throw new IllegalArgumentException("Unknown algorithm name for the encryption type " + encType);
   }

   public static Set<EncryptionType> orderEtypesByStrength(Set<EncryptionType> etypes) {
      Set<EncryptionType> ordered = new LinkedHashSet(etypes.size());

      for(String algo : cipherAlgoMap.values()) {
         for(EncryptionType encType : etypes) {
            String foundAlgo = getAlgoNameFromEncType(encType);
            if (algo.equals(foundAlgo)) {
               ordered.add(encType);
            }
         }
      }

      return ordered;
   }

   public static PrincipalStoreEntry getEntry(KerberosPrincipal principal, PrincipalStore store, ErrorType errorType) throws KerberosException {
      PrincipalStoreEntry entry = null;

      try {
         entry = store.getPrincipal(principal);
      } catch (Exception e) {
         throw new KerberosException(errorType, e);
      }

      if (entry == null) {
         throw new KerberosException(errorType);
      } else if (entry.getKeyMap() != null && !entry.getKeyMap().isEmpty()) {
         return entry;
      } else {
         throw new KerberosException(ErrorType.KDC_ERR_NULL_KEY);
      }
   }

   public static Authenticator verifyAuthHeader(ApReq authHeader, Ticket ticket, EncryptionKey serverKey, long clockSkew, ReplayCache replayCache, boolean emptyAddressesAllowed, InetAddress clientAddress, CipherTextHandler lockBox, KeyUsage authenticatorKeyUsage, boolean isValidate) throws KerberosException {
      if (authHeader.getProtocolVersionNumber() != 5) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BADVERSION);
      } else if (authHeader.getMessageType() != KerberosMessageType.AP_REQ) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_MSG_TYPE);
      } else if (authHeader.getTicket().getTktVno() != 5) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BADVERSION);
      } else {
         EncryptionKey ticketKey = null;
         if (authHeader.getOption(1)) {
            ticketKey = authHeader.getTicket().getEncTicketPart().getKey();
         } else {
            ticketKey = serverKey;
         }

         if (ticketKey == null) {
            throw new KerberosException(ErrorType.KRB_AP_ERR_NOKEY);
         } else {
            byte[] encTicketPartData = lockBox.decrypt(ticketKey, ticket.getEncPart(), KeyUsage.AS_OR_TGS_REP_TICKET_WITH_SRVKEY);
            EncTicketPart encPart = KerberosDecoder.decodeEncTicketPart(encTicketPartData);
            ticket.setEncTicketPart(encPart);
            byte[] authenticatorData = lockBox.decrypt(ticket.getEncTicketPart().getKey(), authHeader.getAuthenticator(), authenticatorKeyUsage);
            Authenticator authenticator = KerberosDecoder.decodeAuthenticator(authenticatorData);
            if (!authenticator.getCName().getNameString().equals(ticket.getEncTicketPart().getCName().getNameString())) {
               throw new KerberosException(ErrorType.KRB_AP_ERR_BADMATCH);
            } else {
               if (ticket.getEncTicketPart().getClientAddresses() != null) {
                  if (!ticket.getEncTicketPart().getClientAddresses().contains(new HostAddress(clientAddress))) {
                     throw new KerberosException(ErrorType.KRB_AP_ERR_BADADDR);
                  }
               } else if (!emptyAddressesAllowed) {
                  throw new KerberosException(ErrorType.KRB_AP_ERR_BADADDR);
               }

               KerberosPrincipal serverPrincipal = getKerberosPrincipal(ticket.getSName(), ticket.getRealm());
               KerberosPrincipal clientPrincipal = getKerberosPrincipal(authenticator.getCName(), authenticator.getCRealm());
               KerberosTime clientTime = authenticator.getCtime();
               int clientMicroSeconds = authenticator.getCusec();
               if (replayCache != null) {
                  if (replayCache.isReplay(serverPrincipal, clientPrincipal, clientTime, clientMicroSeconds)) {
                     throw new KerberosException(ErrorType.KRB_AP_ERR_REPEAT);
                  }

                  replayCache.save(serverPrincipal, clientPrincipal, clientTime, clientMicroSeconds);
               }

               if (!authenticator.getCtime().isInClockSkew(clockSkew)) {
                  throw new KerberosException(ErrorType.KRB_AP_ERR_SKEW);
               } else {
                  KerberosTime startTime = ticket.getEncTicketPart().getStartTime() != null ? ticket.getEncTicketPart().getStartTime() : ticket.getEncTicketPart().getAuthTime();
                  KerberosTime now = new KerberosTime();
                  boolean isValidStartTime = startTime.lessThan(now);
                  if (isValidStartTime && (!ticket.getEncTicketPart().getFlags().isInvalid() || isValidate)) {
                     if (!ticket.getEncTicketPart().getEndTime().greaterThan(now)) {
                        throw new KerberosException(ErrorType.KRB_AP_ERR_TKT_EXPIRED);
                     } else {
                        authHeader.getApOptions().set(2);
                        return authenticator;
                     }
                  } else {
                     throw new KerberosException(ErrorType.KRB_AP_ERR_TKT_NYV);
                  }
               }
            }
         }
      }
   }

   public static boolean isNewEncryptionType(EncryptionType eType) {
      return !oldEncTypes.contains(eType);
   }

   static {
      UTC_DATE_FORMAT.setTimeZone(UTC_TIME_ZONE);
      cipherAlgoMap.put("rc4", "ArcFourHmac");
      cipherAlgoMap.put("aes256", "AES256");
      cipherAlgoMap.put("aes128", "AES128");
      cipherAlgoMap.put("des3", "DESede");
      cipherAlgoMap.put("des", "DES");
      oldEncTypes.add(EncryptionType.DES_CBC_CRC);
      oldEncTypes.add(EncryptionType.DES_CBC_MD4);
      oldEncTypes.add(EncryptionType.DES_CBC_MD5);
      oldEncTypes.add(EncryptionType.DES_EDE3_CBC_ENV_OID);
      oldEncTypes.add(EncryptionType.DES3_CBC_MD5);
      oldEncTypes.add(EncryptionType.DES3_CBC_SHA1);
      oldEncTypes.add(EncryptionType.DES3_CBC_SHA1_KD);
      oldEncTypes.add(EncryptionType.DSAWITHSHA1_CMSOID);
      oldEncTypes.add(EncryptionType.MD5WITHRSAENCRYPTION_CMSOID);
      oldEncTypes.add(EncryptionType.SHA1WITHRSAENCRYPTION_CMSOID);
      oldEncTypes.add(EncryptionType.RC2CBC_ENVOID);
      oldEncTypes.add(EncryptionType.RSAENCRYPTION_ENVOID);
      oldEncTypes.add(EncryptionType.RSAES_OAEP_ENV_OID);
      oldEncTypes.add(EncryptionType.RC4_HMAC);
   }
}
