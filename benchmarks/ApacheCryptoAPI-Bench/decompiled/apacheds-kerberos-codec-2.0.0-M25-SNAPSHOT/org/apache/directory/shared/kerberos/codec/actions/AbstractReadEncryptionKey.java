package org.apache.directory.shared.kerberos.codec.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Container;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.encryptionKey.EncryptionKeyContainer;
import org.apache.directory.shared.kerberos.components.EncryptionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractReadEncryptionKey<E extends Asn1Container> extends GrammarAction<E> {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractReadEncryptionKey.class);
   private static final boolean IS_DEBUG;

   public AbstractReadEncryptionKey(String name) {
      super(name);
   }

   protected abstract void setEncryptionKey(EncryptionKey var1, E var2);

   public final void action(E container) throws DecoderException {
      TLV tlv = container.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder encryptionKeyDecoder = new Asn1Decoder();
         EncryptionKeyContainer encryptionKeyContainer = new EncryptionKeyContainer();

         try {
            encryptionKeyDecoder.decode(container.getStream(), encryptionKeyContainer);
         } catch (DecoderException de) {
            throw de;
         }

         EncryptionKey encryptionKey = encryptionKeyContainer.getEncryptionKey();
         if (IS_DEBUG) {
            LOG.debug("EncryptionKey : " + encryptionKey);
         }

         this.setEncryptionKey(encryptionKey, container);
         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         container.updateParent();
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
