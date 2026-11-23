package org.apache.directory.shared.kerberos.codec.checksum.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.checksum.ChecksumContainer;
import org.apache.directory.shared.kerberos.components.Checksum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChecksumInit extends GrammarAction<ChecksumContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(ChecksumInit.class);
   private static final boolean IS_DEBUG;

   public ChecksumInit() {
      super("Creates a Checksum instance");
   }

   public void action(ChecksumContainer checksumContainer) throws DecoderException {
      TLV tlv = checksumContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Checksum checksum = new Checksum();
         checksumContainer.setChecksum(checksum);
         if (IS_DEBUG) {
            LOG.debug("Checksum created");
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
