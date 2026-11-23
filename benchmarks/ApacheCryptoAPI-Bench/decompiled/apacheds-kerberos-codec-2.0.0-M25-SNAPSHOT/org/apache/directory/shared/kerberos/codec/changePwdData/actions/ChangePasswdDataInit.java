package org.apache.directory.shared.kerberos.codec.changePwdData.actions;

import org.apache.directory.api.asn1.DecoderException;
import org.apache.directory.api.asn1.ber.grammar.GrammarAction;
import org.apache.directory.api.asn1.ber.tlv.TLV;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.shared.kerberos.codec.changePwdData.ChangePasswdDataContainer;
import org.apache.directory.shared.kerberos.messages.ChangePasswdData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePasswdDataInit extends GrammarAction<ChangePasswdDataContainer> {
   private static final Logger LOG = LoggerFactory.getLogger(ChangePasswdDataInit.class);

   public ChangePasswdDataInit() {
      super("Ticket initialization");
   }

   public void action(ChangePasswdDataContainer chngPwdDataContainer) throws DecoderException {
      TLV tlv = chngPwdDataContainer.getCurrentTLV();
      if (tlv.getLength() == 0) {
         LOG.error(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
         throw new DecoderException(I18n.err(I18n.ERR_744_NULL_PDU_LENGTH, new Object[0]));
      } else {
         ChangePasswdData chngPwdData = new ChangePasswdData();
         chngPwdDataContainer.setChngPwdData(chngPwdData);
      }
   }
}
