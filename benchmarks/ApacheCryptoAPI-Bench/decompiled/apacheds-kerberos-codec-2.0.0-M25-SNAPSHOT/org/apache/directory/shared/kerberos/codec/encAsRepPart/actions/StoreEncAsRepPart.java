package org.apache.directory.shared.kerberos.codec.encAsRepPart.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.EncKdcRepPartContainer;
import org.apache.directory.shared.kerberos.codec.encAsRepPart.EncAsRepPartContainer;
import org.apache.directory.shared.kerberos.components.EncKdcRepPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreEncAsRepPart extends GrammarAction<EncAsRepPartContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreEncAsRepPart.class);
   private static final boolean IS_DEBUG;

   public StoreEncAsRepPart() {
      super("Add an EncAsRepPart instance");
   }

   public void action(EncAsRepPartContainer encAsRepPartContainer) throws DecoderException {
      TLV tlv = encAsRepPartContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder encKdcRepPartDecoder = new Asn1Decoder();
         EncKdcRepPartContainer encKdcRepPartContainer = new EncKdcRepPartContainer(encAsRepPartContainer.getStream());

         try {
            encKdcRepPartDecoder.decode(encAsRepPartContainer.getStream(), encKdcRepPartContainer);
         } catch (DecoderException de) {
            throw de;
         }

         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         encAsRepPartContainer.updateParent();
         EncKdcRepPart encKdcRepPart = encKdcRepPartContainer.getEncKdcRepPart();
         encAsRepPartContainer.getEncAsRepPart().setEncKdcRepPart(encKdcRepPart);
         if (IS_DEBUG) {
            LOG.debug("EncAsRepPart : {}", encKdcRepPart);
         }

         encAsRepPartContainer.setGrammarEndAllowed(true);
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
