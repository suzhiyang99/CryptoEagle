package org.apache.directory.shared.kerberos.codec.authenticator;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum AuthenticatorStatesEnum implements States {
   START_STATE,
   AUTHENTICATOR_STATE,
   AUTHENTICATOR_SEQ_STATE,
   AUTHENTICATOR_AUTHENTICATOR_VNO_TAG_STATE,
   AUTHENTICATOR_AUTHENTICATOR_VNO_STATE,
   AUTHENTICATOR_CREALM_TAG_STATE,
   AUTHENTICATOR_CREALM_STATE,
   AUTHENTICATOR_CNAME_STATE,
   AUTHENTICATOR_CKSUM_STATE,
   AUTHENTICATOR_CUSEC_TAG_STATE,
   AUTHENTICATOR_CUSEC_STATE,
   AUTHENTICATOR_CTIME_TAG_STATE,
   AUTHENTICATOR_CTIME_STATE,
   AUTHENTICATOR_SUBKEY_STATE,
   AUTHENTICATOR_SEQ_NUMBER_TAG_STATE,
   AUTHENTICATOR_SEQ_NUMBER_STATE,
   AUTHENTICATOR_AUTHORIZATION_DATA_STATE,
   LAST_AUTHENTICATOR_STATE;

   public String getGrammarName(int grammar) {
      return "AUTHENTICATOR_GRAMMAR";
   }

   public String getGrammarName(Grammar<AuthenticatorContainer> grammar) {
      return grammar instanceof AuthenticatorGrammar ? "AUTHENTICATOR_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_AUTHENTICATOR_STATE.ordinal() ? "AUTHENTICATOR_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_AUTHENTICATOR_STATE;
   }

   public AuthenticatorStatesEnum getStartState() {
      return START_STATE;
   }
}
