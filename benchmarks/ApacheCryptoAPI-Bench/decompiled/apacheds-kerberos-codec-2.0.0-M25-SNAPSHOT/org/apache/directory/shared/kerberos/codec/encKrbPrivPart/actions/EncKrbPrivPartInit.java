package org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.EncKrbPrivPartContainer;
import org.apache.directory.shared.kerberos.components.EncKrbPrivPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncKrbPrivPartInit extends GrammarAction<EncKrbPrivPartContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(EncKrbPrivPartInit.class);
   private static final boolean IS_DEBUG;

   public EncKrbPrivPartInit() {
      super("Creates a EncKrbPrivPart instance");
   }

   public void action(EncKrbPrivPartContainer encKrbPrivPartContainer) throws DecoderException {
      TLV tlv = encKrbPrivPartContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         EncKrbPrivPart encKrbPrivPart = new EncKrbPrivPart();
         encKrbPrivPartContainer.setEncKrbPrivPart(encKrbPrivPart);
         if (IS_DEBUG) {
            LOG.debug("EncKrbPrivPart created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
