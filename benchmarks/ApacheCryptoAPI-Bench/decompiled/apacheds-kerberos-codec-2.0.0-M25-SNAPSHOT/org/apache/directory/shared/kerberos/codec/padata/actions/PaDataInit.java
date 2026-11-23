package org.apache.directory.shared.kerberos.codec.padata.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.kerberos.codec.padata.PaDataContainer;
import org.apache.directory.shared.kerberos.components.PaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaDataInit extends GrammarAction<PaDataContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(PaDataInit.class);
   private static final boolean IS_DEBUG;

   public PaDataInit() {
      super("Creates a PaData instance");
   }

   public void action(PaDataContainer paDataContainer) throws DecoderException {
      PaData paData = new PaData();
      paDataContainer.setPaData(paData);
      if (IS_DEBUG) {
         LOG.debug("PaData created");
      }

   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
