package org.apache.directory.shared.kerberos.codec.encryptedData.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.encryptedData.EncryptedDataContainer;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptedDataInit extends GrammarAction<EncryptedDataContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(EncryptedDataInit.class);
   private static final boolean IS_DEBUG;

   public EncryptedDataInit() {
      super("Creates a EncryptedData instance");
   }

   public void action(EncryptedDataContainer encryptedDataContainer) throws DecoderException {
      TLV tlv = encryptedDataContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
      } else {
         if (encryptedDataContainer.getEncryptedData() == null) {
            EncryptedData encryptedData = new EncryptedData();
            encryptedDataContainer.setEncryptedData(encryptedData);
            if (IS_DEBUG) {
               LOG.debug("EncryptedData created");
            }
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
