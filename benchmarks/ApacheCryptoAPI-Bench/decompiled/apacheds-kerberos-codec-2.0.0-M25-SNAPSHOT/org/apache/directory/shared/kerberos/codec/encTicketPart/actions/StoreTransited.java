package org.apache.directory.shared.kerberos.codec.encTicketPart.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.encTicketPart.EncTicketPartContainer;
import org.apache.directory.shared.kerberos.codec.transitedEncoding.TransitedEncodingContainer;
import org.apache.directory.shared.kerberos.components.TransitedEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreTransited extends GrammarAction<EncTicketPartContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreTransited.class);
   private static final boolean IS_DEBUG;

   public StoreTransited() {
      super("EncTicketPart transited");
   }

   public void action(EncTicketPartContainer encTicketPartContainer) throws DecoderException {
      TLV tlv = encTicketPartContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         TransitedEncodingContainer transitedContainer = new TransitedEncodingContainer();
         Asn1Decoder transitedEncodingDecoder = new Asn1Decoder();

         try {
            transitedEncodingDecoder.decode(encTicketPartContainer.getStream(), transitedContainer);
         } catch (DecoderException de) {
            throw de;
         }

         TransitedEncoding te = transitedContainer.getTransitedEncoding();
         if (IS_DEBUG) {
            LOG.debug("TransitedEncoding {}", te);
         }

         encTicketPartContainer.getEncTicketPart().setTransited(te);
         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         encTicketPartContainer.updateParent();
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
