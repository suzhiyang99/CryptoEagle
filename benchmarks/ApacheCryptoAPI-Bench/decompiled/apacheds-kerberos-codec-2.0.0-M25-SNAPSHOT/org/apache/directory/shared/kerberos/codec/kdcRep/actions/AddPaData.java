package org.apache.directory.shared.kerberos.codec.kdcRep.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.kdcRep.KdcRepContainer;
import org.apache.directory.shared.kerberos.codec.padata.PaDataContainer;
import org.apache.directory.shared.kerberos.components.KdcRep;
import org.apache.directory.shared.kerberos.components.PaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddPaData extends GrammarAction<KdcRepContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(AddPaData.class);
   private static final boolean IS_DEBUG;

   public AddPaData() {
      super("KDC-REP Add PA-DATA");
   }

   public void action(KdcRepContainer kdcRepContainer) throws DecoderException {
      TLV tlv = kdcRepContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder paDataDecoder = new Asn1Decoder();
         PaDataContainer paDataContainer = new PaDataContainer();
         paDataContainer.setStream(kdcRepContainer.getStream());
         kdcRepContainer.rewind();

         try {
            paDataDecoder.decode(kdcRepContainer.getStream(), paDataContainer);
         } catch (DecoderException de) {
            throw de;
         }

         kdcRepContainer.updateParent();
         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         PaData paData = paDataContainer.getPaData();
         KdcRep kdcRep = kdcRepContainer.getKdcRep();
         kdcRep.addPaData(paData);
         if (IS_DEBUG) {
            LOG.debug("Added PA-DATA:  {}", paData);
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
