package org.apache.directory.shared.kerberos.codec.changePwdData;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.changePwdData.actions.ChangePasswdDataInit;
import org.apache.directory.shared.kerberos.codec.changePwdData.actions.StoreNewPassword;
import org.apache.directory.shared.kerberos.codec.changePwdData.actions.StoreTargName;
import org.apache.directory.shared.kerberos.codec.changePwdData.actions.StoreTargRealm;

public final class ChangePasswdDataGrammar extends AbstractGrammar<ChangePasswdDataContainer> {
   private static Grammar<ChangePasswdDataContainer> instance = new ChangePasswdDataGrammar();

   private ChangePasswdDataGrammar() {
      this.setName(ChangePasswdDataGrammar.class.getName());
      super.transitions = new GrammarTransition[ChangePasswdDataStatesEnum.LAST_CHNGPWD_STATE.ordinal()][256];
      super.transitions[ChangePasswdDataStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(ChangePasswdDataStatesEnum.START_STATE, ChangePasswdDataStatesEnum.CHNGPWD_SEQ_STATE, UniversalTag.SEQUENCE, new ChangePasswdDataInit());
      super.transitions[ChangePasswdDataStatesEnum.CHNGPWD_SEQ_STATE.ordinal()][160] = new GrammarTransition(ChangePasswdDataStatesEnum.CHNGPWD_SEQ_STATE, ChangePasswdDataStatesEnum.CHNGPWD_NEWPASSWD_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[ChangePasswdDataStatesEnum.CHNGPWD_NEWPASSWD_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(ChangePasswdDataStatesEnum.CHNGPWD_NEWPASSWD_TAG_STATE, ChangePasswdDataStatesEnum.CHNGPWD_NEWPASSWD_STATE, UniversalTag.OCTET_STRING, new StoreNewPassword());
      super.transitions[ChangePasswdDataStatesEnum.CHNGPWD_NEWPASSWD_STATE.ordinal()][161] = new GrammarTransition(ChangePasswdDataStatesEnum.CHNGPWD_NEWPASSWD_STATE, ChangePasswdDataStatesEnum.CHNGPWD_TARGNAME_TAG_STATE, 161, new StoreTargName());
      super.transitions[ChangePasswdDataStatesEnum.CHNGPWD_TARGNAME_TAG_STATE.ordinal()][162] = new GrammarTransition(ChangePasswdDataStatesEnum.CHNGPWD_TARGNAME_TAG_STATE, ChangePasswdDataStatesEnum.CHNGPWD_TARGREALM_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[ChangePasswdDataStatesEnum.CHNGPWD_TARGREALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(ChangePasswdDataStatesEnum.CHNGPWD_TARGREALM_TAG_STATE, ChangePasswdDataStatesEnum.CHNGPWD_TARGREALM_STATE, UniversalTag.GENERAL_STRING, new StoreTargRealm());
      super.transitions[ChangePasswdDataStatesEnum.CHNGPWD_NEWPASSWD_STATE.ordinal()][162] = new GrammarTransition(ChangePasswdDataStatesEnum.CHNGPWD_NEWPASSWD_STATE, ChangePasswdDataStatesEnum.CHNGPWD_TARGREALM_TAG_STATE, 162, new CheckNotNullLength());
   }

   public static Grammar<ChangePasswdDataContainer> getInstance() {
      return instance;
   }
}
