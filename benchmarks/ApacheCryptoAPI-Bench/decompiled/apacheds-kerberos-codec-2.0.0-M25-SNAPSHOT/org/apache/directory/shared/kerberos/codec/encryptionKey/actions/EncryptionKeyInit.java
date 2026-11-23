package org.apache.directory.shared.kerberos.codec.encryptionKey.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.encryptionKey.EncryptionKeyContainer;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionKeyInit extends GrammarAction<EncryptionKeyContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(EncryptionKeyInit.class);
   private static final boolean IS_DEBUG;

   public EncryptionKeyInit() {
      super("Creates a EncryptionKey instance");
   }

   public void action(EncryptionKeyContainer encryptionKeyContainer) throws DecoderException {
      TLV tlv = encryptionKeyContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
      } else {
         EncryptionKey encKey = new EncryptionKey();
         encryptionKeyContainer.setEncryptionKey(encKey);
         if (IS_DEBUG) {
            LOG.debug("EncryptionKey created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
