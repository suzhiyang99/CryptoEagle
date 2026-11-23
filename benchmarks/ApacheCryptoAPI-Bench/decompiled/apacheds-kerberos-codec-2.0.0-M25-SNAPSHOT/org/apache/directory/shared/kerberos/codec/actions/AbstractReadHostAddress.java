package org.apache.directory.shared.kerberos.codec.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Container;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.hostAddress.HostAddressContainer;
import org.apache.directory.shared.kerberos.components.HostAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractReadHostAddress<E extends Asn1Container> extends GrammarAction<E> {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractReadHostAddress.class);
   private static final boolean IS_DEBUG;

   public AbstractReadHostAddress(String name) {
      super(name);
   }

   protected abstract void setAddress(HostAddress var1, E var2);

   public void action(E container) throws DecoderException {
      TLV tlv = container.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder hostAddressDecoder = new Asn1Decoder();
         HostAddressContainer hostAddressContainer = new HostAddressContainer();
         hostAddressContainer.setStream(container.getStream());

         try {
            hostAddressDecoder.decode(container.getStream(), hostAddressContainer);
         } catch (DecoderException de) {
            throw de;
         }

         HostAddress hostAddress = hostAddressContainer.getHostAddress();
         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         container.updateParent();
         this.setAddress(hostAddress, container);
         if (IS_DEBUG) {
            LOG.debug("HostAddress : {}", hostAddress);
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
