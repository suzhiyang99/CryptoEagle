package org.apache.directory.shared.kerberos.codec;

import java.nio.ByteBuffer;
import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Container;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.tlv.TLVStateEnum;
import org.apache.directory.server.kerberos.changepwd.exceptions.ChangePasswdErrorType;
import org.apache.directory.server.kerberos.changepwd.exceptions.ChangePasswordException;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.apRep.ApRepContainer;
import org.apache.directory.shared.kerberos.codec.apReq.ApReqContainer;
import org.apache.directory.shared.kerberos.codec.authenticator.AuthenticatorContainer;
import org.apache.directory.shared.kerberos.codec.authorizationData.AuthorizationDataContainer;
import org.apache.directory.shared.kerberos.codec.encApRepPart.EncApRepPartContainer;
import org.apache.directory.shared.kerberos.codec.encAsRepPart.EncAsRepPartContainer;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.EncKrbPrivPartContainer;
import org.apache.directory.shared.kerberos.codec.encTgsRepPart.EncTgsRepPartContainer;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;
import org.apache.directory.shared.kerberos.codec.encryptedData.EncryptedDataContainer;
import org.apache.directory.shared.kerberos.codec.encryptionKey.EncryptionKeyContainer;
import org.apache.directory.shared.kerberos.codec.krbPriv.KrbPrivContainer;
import org.apache.directory.shared.kerberos.codec.paEncTsEnc.PaEncTsEncContainer;
import org.apache.directory.shared.kerberos.codec.principalName.PrincipalNameContainer;
import org.apache.directory.shared.kerberos.codec.ticket.TicketContainer;
import org.apache.directory.shared.kerberos.components.AuthorizationData;
import org.apache.directory.shared.kerberos.components.EncKdcRepPart;
import org.apache.directory.shared.kerberos.components.EncKrbPrivPart;
import org.apache.directory.shared.kerberos.components.EncTicketPart;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.apache.directory.shared.kerberos.components.PaEncTsEnc;
import org.apache.directory.shared.kerberos.components.PrincipalName;
import org.apache.directory.shared.kerberos.exceptions.ErrorType;
import org.apache.directory.shared.kerberos.exceptions.KerberosException;
import org.apache.directory.shared.kerberos.messages.ApRep;
import org.apache.directory.shared.kerberos.messages.ApReq;
import org.apache.directory.shared.kerberos.messages.Authenticator;
import org.apache.directory.shared.kerberos.messages.EncApRepPart;
import org.apache.directory.shared.kerberos.messages.EncAsRepPart;
import org.apache.directory.shared.kerberos.messages.EncTgsRepPart;
import org.apache.directory.shared.kerberos.messages.KrbPriv;
import org.apache.directory.shared.kerberos.messages.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KerberosDecoder {
   private static Logger LOG = LoggerFactory.getLogger(KerberosDecoder.class);
   private static final boolean IS_DEBUG;

   public static Object decode(KerberosMessageContainer kerberosMessageContainer, Asn1Decoder asn1Decoder) throws DecoderException {
      ByteBuffer buf = kerberosMessageContainer.getStream();
      if (kerberosMessageContainer.isTCP()) {
         if (buf.remaining() <= 4) {
            return null;
         }

         kerberosMessageContainer.setTcpLength(buf.getInt());
         buf.mark();
      } else {
         buf.mark();
      }

      while(buf.hasRemaining()) {
         try {
            asn1Decoder.decode(buf, kerberosMessageContainer);
            if (kerberosMessageContainer.getState() == TLVStateEnum.PDU_DECODED) {
               if (IS_DEBUG) {
                  LOG.debug("Decoded KerberosMessage : " + kerberosMessageContainer.getMessage());
                  buf.mark();
               }

               return kerberosMessageContainer.getMessage();
            }
         } catch (DecoderException de) {
            LOG.warn("error while decoding", de);
            buf.clear();
            kerberosMessageContainer.clean();
            throw de;
         }
      }

      return null;
   }

   public static EncryptedData decodeEncryptedData(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container encryptedDataContainer = new EncryptedDataContainer();
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, encryptedDataContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      EncryptedData encryptedData = ((EncryptedDataContainer)encryptedDataContainer).getEncryptedData();
      return encryptedData;
   }

   public static PaEncTsEnc decodePaEncTsEnc(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container paEncTsEncContainer = new PaEncTsEncContainer();
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, paEncTsEncContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      PaEncTsEnc paEncTsEnc = ((PaEncTsEncContainer)paEncTsEncContainer).getPaEncTsEnc();
      return paEncTsEnc;
   }

   public static EncApRepPart decodeEncApRepPart(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container encApRepPartContainer = new EncApRepPartContainer(stream);
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, encApRepPartContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      EncApRepPart encApRepPart = ((EncApRepPartContainer)encApRepPartContainer).getEncApRepPart();
      return encApRepPart;
   }

   public static EncKdcRepPart decodeEncKdcRepPart(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container encKdcRepPartContainer = new EncKdcRepPartContainer(stream);
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, encKdcRepPartContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      EncKdcRepPart encKdcRepPart = ((EncKdcRepPartContainer)encKdcRepPartContainer).getEncKdcRepPart();
      return encKdcRepPart;
   }

   public static EncKrbPrivPart decodeEncKrbPrivPart(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container encKrbPrivPartContainer = new EncKrbPrivPartContainer(stream);
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, encKrbPrivPartContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      EncKrbPrivPart encKrbPrivPart = ((EncKrbPrivPartContainer)encKrbPrivPartContainer).getEncKrbPrivPart();
      return encKrbPrivPart;
   }

   public static EncTicketPart decodeEncTicketPart(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container encTicketPartContainer = new EncTicketPartContainer(stream);
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, encTicketPartContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      EncTicketPart encTicketPart = ((EncTicketPartContainer)encTicketPartContainer).getEncTicketPart();
      return encTicketPart;
   }

   public static EncryptionKey decodeEncryptionKey(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container encryptionKeyContainer = new EncryptionKeyContainer();
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, encryptionKeyContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      EncryptionKey encryptionKey = ((EncryptionKeyContainer)encryptionKeyContainer).getEncryptionKey();
      return encryptionKey;
   }

   public static PrincipalName decodePrincipalName(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container principalNameContainer = new PrincipalNameContainer();
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, principalNameContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      PrincipalName principalName = ((PrincipalNameContainer)principalNameContainer).getPrincipalName();
      return principalName;
   }

   public static Ticket decodeTicket(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container ticketContainer = new TicketContainer(stream);
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, ticketContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      Ticket ticket = ((TicketContainer)ticketContainer).getTicket();
      return ticket;
   }

   public static Authenticator decodeAuthenticator(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container authenticatorContainer = new AuthenticatorContainer(stream);
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, authenticatorContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      Authenticator authenticator = ((AuthenticatorContainer)authenticatorContainer).getAuthenticator();
      return authenticator;
   }

   public static AuthorizationData decodeAuthorizationData(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container authorizationDataContainer = new AuthorizationDataContainer();
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, authorizationDataContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      AuthorizationData authorizationData = ((AuthorizationDataContainer)authorizationDataContainer).getAuthorizationData();
      return authorizationData;
   }

   public static ApRep decodeApRep(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container apRepContainer = new ApRepContainer(stream);
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, apRepContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      ApRep apRep = ((ApRepContainer)apRepContainer).getApRep();
      return apRep;
   }

   public static ApReq decodeApReq(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container apReqContainer = new ApReqContainer(stream);
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, apReqContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      ApReq apReq = ((ApReqContainer)apReqContainer).getApReq();
      return apReq;
   }

   public static KrbPriv decodeKrbPriv(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container krbPrivContainer = new KrbPrivContainer(stream);
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, krbPrivContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      KrbPriv krbPriv = ((KrbPrivContainer)krbPrivContainer).getKrbPriv();
      return krbPriv;
   }

   public static EncAsRepPart decodeEncAsRepPart(byte[] data) throws KerberosException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container encAsRepPartContainer = new EncAsRepPartContainer(stream);
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, encAsRepPartContainer);
      } catch (DecoderException de) {
         throw new KerberosException(ErrorType.KRB_AP_ERR_BAD_INTEGRITY, de);
      }

      EncAsRepPart encAsRepPart = ((EncAsRepPartContainer)encAsRepPartContainer).getEncAsRepPart();
      return encAsRepPart;
   }

   public static EncTgsRepPart decodeEncTgsRepPart(byte[] data) throws ChangePasswordException {
      ByteBuffer stream = ByteBuffer.allocate(data.length);
      stream.put(data);
      stream.flip();
      Asn1Container encTgsRepPartContainer = new EncTgsRepPartContainer(stream);
      Asn1Decoder kerberosDecoder = new Asn1Decoder();

      try {
         kerberosDecoder.decode(stream, encTgsRepPartContainer);
      } catch (DecoderException de) {
         throw new ChangePasswordException(ChangePasswdErrorType.KRB5_KPASSWD_MALFORMED, de);
      }

      EncTgsRepPart encTgsRepPart = ((EncTgsRepPartContainer)encTgsRepPartContainer).getEncTgsRepPart();
      return encTgsRepPart;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
