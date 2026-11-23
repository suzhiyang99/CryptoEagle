package org.apache.directory.server.kerberos.shared.store;

import java.text.ParseException;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.directory.server.kerberos.shared.crypto.encryption.CipherTextHandler;
import org.apache.directory.server.kerberos.shared.crypto.encryption.KeyUsage;
import org.apache.directory.server.kerberos.shared.crypto.encryption.RandomKeyFactory;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.apache.directory.shared.kerberos.codec.types.EncryptionType;
import org.apache.directory.shared.kerberos.codec.types.PrincipalNameType;
import org.apache.directory.shared.kerberos.components.EncTicketPart;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.components.PrincipalName;
import org.apache.directory.shared.kerberos.components.TransitedEncoding;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;
import org.apache.directory.shared.kerberos.flags.TicketFlag;
import org.apache.directory.shared.kerberos.flags.TicketFlags;
import org.apache.directory.shared.kerberos.messages.Ticket;

public class TicketFactory {
   private static final int ONE_DAY = 86400000;
   private static final int ONE_WEEK = 604800000;
   private CipherTextHandler cipherTextHandler = new CipherTextHandler();

   public EncryptionKey getServerKey(KerberosPrincipal serverPrincipal, String serverPassword) {
      KerberosKey serverKerberosKey = new KerberosKey(serverPrincipal, serverPassword.toCharArray(), "DES");
      byte[] serverKeyBytes = serverKerberosKey.getEncoded();
      EncryptionKey serverKey = new EncryptionKey(EncryptionType.DES_CBC_MD5, serverKeyBytes);
      return serverKey;
   }

   public Ticket getTicket(KerberosPrincipal clientPrincipal, KerberosPrincipal serverPrincipal, EncryptionKey serverKey) throws KerberosException, ParseException {
      EncTicketPart encTicketPart = new EncTicketPart();
      TicketFlags ticketFlags = new TicketFlags();
      ticketFlags.setFlag(TicketFlag.RENEWABLE);
      encTicketPart.setFlags(ticketFlags);
      EncryptionKey sessionKey = RandomKeyFactory.getRandomKey(EncryptionType.DES_CBC_MD5);
      encTicketPart.setKey(sessionKey);
      encTicketPart.setCName(new PrincipalName(clientPrincipal.getName(), PrincipalNameType.KRB_NT_PRINCIPAL));
      encTicketPart.setTransited(new TransitedEncoding());
      encTicketPart.setAuthTime(new KerberosTime());
      long now = System.currentTimeMillis();
      KerberosTime endTime = new KerberosTime(now + 86400000L);
      encTicketPart.setEndTime(endTime);
      KerberosTime renewTill = new KerberosTime(now + 604800000L);
      encTicketPart.setRenewTill(renewTill);
      EncryptedData encryptedTicketPart = this.cipherTextHandler.seal(serverKey, encTicketPart, KeyUsage.AS_OR_TGS_REP_TICKET_WITH_SRVKEY);
      Ticket ticket = new Ticket();
      ticket.setTktVno(5);
      ticket.setSName(new PrincipalName(serverPrincipal.getName(), PrincipalNameType.KRB_NT_PRINCIPAL));
      ticket.setRealm(serverPrincipal.getRealm());
      ticket.setEncPart(encryptedTicketPart);
      return ticket;
   }
}
