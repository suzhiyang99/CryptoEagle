package org.apache.directory.shared.kerberos.codec.encTgsRepPart.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.encTgsRepPart.EncTgsRepPartContainer;
import org.apache.directory.shared.kerberos.components.EncKdcRepPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreEncTgsRepPart extends GrammarAction<EncTgsRepPartContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreEncTgsRepPart.class);
   private static final boolean IS_DEBUG;

   public StoreEncTgsRepPart() {
      super("Add an EncTgsRepPart instance");
   }

   public void action(EncTgsRepPartContainer encTgsRepPartContainer) throws DecoderException {
      TLV tlv = encTgsRepPartContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder encKdcRepPartDecoder = new Asn1Decoder();
         EncKdcRepPartContainer encKdcRepPartContainer = new EncKdcRepPartContainer(encTgsRepPartContainer.getStream());

         try {
            encKdcRepPartDecoder.decode(encTgsRepPartContainer.getStream(), encKdcRepPartContainer);
         } catch (DecoderException de) {
            throw de;
         }

         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         encTgsRepPartContainer.updateParent();
         EncKdcRepPart encKdcRepPart = encKdcRepPartContainer.getEncKdcRepPart();
         encTgsRepPartContainer.getEncTgsRepPart().setEncKdcRepPart(encKdcRepPart);
         if (IS_DEBUG) {
            LOG.debug("EncKdcRepPart : {}", encKdcRepPart);
         }

         encTgsRepPartContainer.setGrammarEndAllowed(true);
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
