package org.apache.directory.shared.kerberos.codec.methodData.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.methodData.MethodDataContainer;
import org.apache.directory.shared.kerberos.codec.padata.PaDataContainer;
import org.apache.directory.shared.kerberos.components.PaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddPaData extends GrammarAction<MethodDataContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(AddPaData.class);
   private static final boolean IS_DEBUG;

   public AddPaData() {
      super("Add a PA-DATA instance");
   }

   public void action(MethodDataContainer methodDataContainer) throws DecoderException {
      TLV tlv = methodDataContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder paDataDecoder = new Asn1Decoder();
         PaDataContainer paDataContainer = new PaDataContainer();
         paDataContainer.setStream(methodDataContainer.getStream());
         methodDataContainer.rewind();

         try {
            paDataDecoder.decode(methodDataContainer.getStream(), paDataContainer);
         } catch (DecoderException de) {
            throw de;
         }

         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         methodDataContainer.updateParent();
         PaData paData = paDataContainer.getPaData();
         methodDataContainer.addPaData(paData);
         if (IS_DEBUG) {
            LOG.debug("PA-DATA added : {}", paData);
         }

         methodDataContainer.setGrammarEndAllowed(true);
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
