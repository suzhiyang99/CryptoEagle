package org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.EncKrbCredPartContainer;
import org.apache.directory.shared.kerberos.components.EncKrbCredPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncKrbCredPartInit extends GrammarAction<EncKrbCredPartContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(EncKrbCredPartInit.class);
   private static final boolean IS_DEBUG;

   public EncKrbCredPartInit() {
      super("Creates a EncKrbCredPart instance");
   }

   public void action(EncKrbCredPartContainer encKrbCredPartContainer) throws DecoderException {
      TLV tlv = encKrbCredPartContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         EncKrbCredPart encKrbCredPart = new EncKrbCredPart();
         encKrbCredPartContainer.setEncKrbCredPart(encKrbCredPart);
         if (IS_DEBUG) {
            LOG.debug("EncKrbCredPart created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
