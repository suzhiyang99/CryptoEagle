package org.apache.directory.shared.kerberos.codec.kdcReq.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.kdcReq.KdcReqContainer;
import org.apache.directory.shared.kerberos.codec.padata.PaDataContainer;
import org.apache.directory.shared.kerberos.components.KdcReq;
import org.apache.directory.shared.kerberos.components.PaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddPaData extends GrammarAction<KdcReqContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(AddPaData.class);
   private static final boolean IS_DEBUG;

   public AddPaData() {
      super("KDC-REQ Add PA-DATA");
   }

   public void action(KdcReqContainer kdcReqContainer) throws DecoderException {
      TLV tlv = kdcReqContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder paDataDecoder = new Asn1Decoder();
         PaDataContainer paDataContainer = new PaDataContainer();
         paDataContainer.setStream(kdcReqContainer.getStream());
         kdcReqContainer.rewind();

         try {
            paDataDecoder.decode(kdcReqContainer.getStream(), paDataContainer);
         } catch (DecoderException de) {
            throw de;
         }

         kdcReqContainer.updateParent();
         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         PaData paData = paDataContainer.getPaData();
         KdcReq kdcReq = kdcReqContainer.getKdcReq();
         kdcReq.addPaData(paData);
         if (IS_DEBUG) {
            LOG.debug("Added PA-DATA:  {}", paData);
         }

      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
