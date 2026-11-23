package org.apache.directory.shared.kerberos.codec.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Container;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.hostAddresses.HostAddressesContainer;
import org.apache.directory.shared.kerberos.components.HostAddresses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractReadHostAddresses<E extends Asn1Container> extends GrammarAction<E> {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractReadHostAddresses.class);
   private static final boolean IS_DEBUG;

   public AbstractReadHostAddresses(String name) {
      super(name);
   }

   protected abstract void setHostAddresses(HostAddresses var1, E var2);

   public final void action(E container) throws DecoderException {
      TLV tlv = container.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder hostAddressesDecoder = new Asn1Decoder();
         HostAddressesContainer hostAddressesContainer = new HostAddressesContainer();
         hostAddressesContainer.setStream(container.getStream());

         try {
            hostAddressesDecoder.decode(container.getStream(), hostAddressesContainer);
         } catch (DecoderException de) {
            throw de;
         }

         HostAddresses hostAddresses = hostAddressesContainer.getHostAddresses();
         this.setHostAddresses(hostAddresses, container);
         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         container.updateParent();
         if (IS_DEBUG) {
            LOG.debug("HostAddresses : {}", hostAddresses);
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
