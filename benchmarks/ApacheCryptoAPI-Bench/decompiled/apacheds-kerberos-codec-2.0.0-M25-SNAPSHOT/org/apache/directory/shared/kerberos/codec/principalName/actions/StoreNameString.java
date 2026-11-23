package org.apache.directory.shared.kerberos.codec.principalName.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.util.Strings;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosUtils;
import org.apache.directory.shared.kerberos.codec.principalName.PrincipalNameContainer;
import org.apache.directory.shared.kerberos.components.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreNameString extends GrammarAction<PrincipalNameContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreNameString.class);
   private static final boolean IS_DEBUG;
   private static boolean ALLOW_UTF8_NAMES;

   public StoreNameString() {
      super("Store the PrincipalName string");
   }

   public void action(PrincipalNameContainer principalNameContainer) throws DecoderException {
      TLV tlv = principalNameContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
      } else {
         PrincipalName principalName = principalNameContainer.getPrincipalName();
         BerValue value = tlv.getValue();
         if (!ALLOW_UTF8_NAMES && !KerberosUtils.isKerberosString(value.getData())) {
            String valBytes = Strings.dumpBytes(value.getData());
            String valStr = Strings.utf8ToString(value.getData());
            String valAll = valBytes + "/" + valStr;
            LOG.error(I18n.err(I18n.ERR_745_NOT_A_KERBEROS_STRING, new Object[]{valAll}));
            throw new DecoderException(I18n.err(I18n.ERR_745_NOT_A_KERBEROS_STRING, new Object[]{valAll}));
         } else {
            String nameString = Strings.utf8ToString(value.getData());
            principalName.addName(nameString);
            principalNameContainer.setGrammarEndAllowed(true);
            if (IS_DEBUG) {
               LOG.debug("PrincipalName String : {}", nameString);
            }

         }
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      ALLOW_UTF8_NAMES = false;
      String allowUTF8Names = System.getProperty("sun.security.krb5.msinterop.kstring");
      if ("true".equalsIgnoreCase(Strings.trim(allowUTF8Names))) {
         ALLOW_UTF8_NAMES = true;
      }

   }
}
