package org.apache.directory.shared.kerberos.codec.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Container;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.encryptedData.EncryptedDataContainer;
import org.apache.directory.shared.kerberos.components.EncryptedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractReadEncryptedPart<E extends Asn1Container> extends GrammarAction<E> {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractReadEncryptedPart.class);
   private static final boolean IS_DEBUG;

   public AbstractReadEncryptedPart(String name) {
      super(name);
   }

   protected abstract void setEncryptedData(EncryptedData var1, E var2);

   public final void action(E container) throws DecoderException {
      TLV tlv = container.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder encryptedDataDecoder = new Asn1Decoder();
         EncryptedDataContainer encryptedDataContainer = new EncryptedDataContainer();
         encryptedDataContainer.setStream(container.getStream());

         try {
            encryptedDataDecoder.decode(container.getStream(), encryptedDataContainer);
         } catch (DecoderException de) {
            throw de;
         }

         EncryptedData encryptedData = encryptedDataContainer.getEncryptedData();
         if (IS_DEBUG) {
            LOG.debug("EncryptedData : " + encryptedData);
         }

         this.setEncryptedData(encryptedData, container);
         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         container.updateParent();
         container.setGrammarEndAllowed(true);
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
