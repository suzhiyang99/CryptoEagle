package org.apache.directory.shared.kerberos.codec.hostAddresses.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.hostAddress.HostAddressContainer;
import org.apache.directory.shared.kerberos.codec.hostAddresses.HostAddressesContainer;
import org.apache.directory.shared.kerberos.components.HostAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddHostAddress extends GrammarAction<HostAddressesContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(AddHostAddress.class);
   private static final boolean IS_DEBUG;

   public AddHostAddress() {
      super("Add an HostAddress instance");
   }

   public void action(HostAddressesContainer hostAddressesContainer) throws DecoderException {
      TLV tlv = hostAddressesContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder hostAddressDecoder = new Asn1Decoder();
         HostAddressContainer hostAddressContainer = new HostAddressContainer();
         hostAddressContainer.setStream(hostAddressesContainer.getStream());
         hostAddressesContainer.rewind();

         try {
            hostAddressDecoder.decode(hostAddressesContainer.getStream(), hostAddressContainer);
         } catch (DecoderException de) {
            throw de;
         }

         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         hostAddressesContainer.updateParent();
         HostAddress hostAddress = hostAddressContainer.getHostAddress();
         hostAddressesContainer.addHostAddress(hostAddress);
         if (IS_DEBUG) {
            LOG.debug("HostAddress added : {}", hostAddress);
         }

         hostAddressesContainer.setGrammarEndAllowed(true);
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
