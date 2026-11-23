package org.apache.directory.shared.kerberos.codec.tgsRep.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.Asn1Decoder;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.api.i18n.I18n;
import org.apache.directory.shared.kerberos.KerberosMessageType;
import org.apache.directory.shared.kerberos.codec.kdcRep.KdcRepContainer;
import org.apache.directory.shared.kerberos.codec.tgsRep.TgsRepContainer;
import org.apache.directory.shared.kerberos.messages.TgsRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreKdcRep extends GrammarAction<TgsRepContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(StoreKdcRep.class);
   private static final boolean IS_DEBUG;

   public StoreKdcRep() {
      super("Add an KDC-REP instance");
   }

   public void action(TgsRepContainer tgsRepContainer) throws DecoderException {
      TLV tlv = tgsRepContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_04066, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_04067, new Object[0]));
      } else {
         Asn1Decoder kdcRepDecoder = new Asn1Decoder();
         KdcRepContainer kdcRepContainer = new KdcRepContainer(tgsRepContainer.getStream());
         TgsRep tgsRep = new TgsRep();
         kdcRepContainer.setKdcRep(tgsRep);

         try {
            kdcRepDecoder.decode(tgsRepContainer.getStream(), kdcRepContainer);
         } catch (DecoderException de) {
            throw de;
         }

         tlv.setExpectedLength(tlv.getExpectedLength() - tlv.getLength());
         tgsRepContainer.updateParent();
         if (tgsRep.getMessageType() != KerberosMessageType.TGS_REP) {
            throw new DecoderException("Bad message type");
         } else {
            tgsRepContainer.setTgsRep(tgsRep);
            if (IS_DEBUG) {
               LOG.debug("TGS-REP : {}", tgsRep);
            }

            tgsRepContainer.setGrammarEndAllowed(true);
         }
      }
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
   }
}
