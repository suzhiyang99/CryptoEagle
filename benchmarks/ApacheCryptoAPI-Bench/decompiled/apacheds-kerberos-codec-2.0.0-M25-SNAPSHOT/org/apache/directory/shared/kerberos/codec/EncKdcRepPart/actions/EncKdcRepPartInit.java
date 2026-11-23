package org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.components.EncKdcRepPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncKdcRepPartInit extends GrammarAction<EncKdcRepPartContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(EncKdcRepPart.class);
   private static final boolean IS_DEBUG;

   public EncKdcRepPartInit() {
      super("Creates a EncKdcRepPart instance");
   }

   public void action(EncKdcRepPartContainer encKdcRepPartContainer) throws DecoderException {
      TLV tlv = encKdcRepPartContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         EncKdcRepPart encKdcRepPart = new EncKdcRepPart();
         encKdcRepPartContainer.setEncKdcRepPart(encKdcRepPart);
         if (IS_DEBUG) {
            LOG.debug("EncKdcRepPart created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
