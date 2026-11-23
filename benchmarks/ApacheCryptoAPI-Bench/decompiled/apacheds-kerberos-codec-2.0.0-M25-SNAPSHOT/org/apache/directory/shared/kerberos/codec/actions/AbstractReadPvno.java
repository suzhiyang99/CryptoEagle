package org.apache.directory.shared.kerberos.codec.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Container;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.BerValue;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.api.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.api.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractReadPvno<E extends Asn1Container> extends GrammarAction<E> {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractReadPvno.class);
   private static final boolean IS_DEBUG;

   public AbstractReadPvno(String name) {
      super(name);
   }

   protected abstract void setPvno(int var1, E var2);

   public final void action(E container) throws DecoderException {
      TLV tlv = container.getCurrentTLV();
      if (tlv.getLength() != 1) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         BerValue value = tlv.getValue();

         try {
            int pvno = IntegerDecoder.parse(value);
            if (pvno != 5) {
               LOG.error(I18n.err(I18n.ERR_04070, new Object[]{Strings.dumpBytes(value.getData()), "The PVNO should be 5"}));
               throw new DecoderException("The PVNO should be 5");
            } else {
               if (IS_DEBUG) {
                  LOG.debug("pvno : {}", pvno);
               }

               this.setPvno(pvno, container);
            }
         } catch (IntegerDecoderException ide) {
            LOG.error(I18n.err(I18n.ERR_04070, new Object[]{Strings.dumpBytes(value.getData()), ide.getLocalizedMessage()}));
            throw new DecoderException(ide.getMessage());
         }
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
