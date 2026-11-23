package org.apache.directory.shared.kerberos.codec.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Container;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.api.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractReadRealm<E extends Asn1Container> extends GrammarAction<E> {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractReadRealm.class);
   private static final boolean IS_DEBUG;

   public AbstractReadRealm(String name) {
      super(name);
   }

   protected abstract void setRealm(String var1, E var2);

   public final void action(E container) throws DecoderException {
      TLV tlv = container.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         BerValue value = tlv.getValue();
         String realm = Strings.utf8ToString(value.getData());
         if (IS_DEBUG) {
            LOG.debug("read realm value : " + realm);
         }

         this.setRealm(realm, container);
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
