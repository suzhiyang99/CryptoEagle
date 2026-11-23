package org.apache.directory.shared.kerberos.codec.hostAddress.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.hostAddress.HostAddressContainer;
import org.apache.directory.shared.kerberos.components.HostAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostAddressInit extends GrammarAction<HostAddressContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(HostAddressInit.class);
   private static final boolean IS_DEBUG;

   public HostAddressInit() {
      super("Creates a HostAddress instance");
   }

   public void action(HostAddressContainer hostAddressContainer) throws DecoderException {
      TLV tlv = hostAddressContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
      } else {
         HostAddress hostAddress = new HostAddress();
         hostAddressContainer.setHostAddress(hostAddress);
         if (IS_DEBUG) {
            LOG.debug("HostAddress created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
