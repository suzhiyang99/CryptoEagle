package org.apache.directory.shared.kerberos.codec.adKdcIssued.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.adKdcIssued.AdKdcIssuedContainer;
import org.apache.directory.shared.kerberos.components.AdKdcIssued;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdKdcIssuedInit extends GrammarAction<AdKdcIssuedContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(AdKdcIssuedInit.class);
   private static final boolean IS_DEBUG;

   public AdKdcIssuedInit() {
      super("Creates a AD-KDCIssued instance");
   }

   public void action(AdKdcIssuedContainer adKdcIssuedContainer) throws DecoderException {
      TLV tlv = adKdcIssuedContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         AdKdcIssued adKdcIssued = new AdKdcIssued();
         adKdcIssuedContainer.setAdKdcIssued(adKdcIssued);
         if (IS_DEBUG) {
            LOG.debug("AdKdcIssued created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
