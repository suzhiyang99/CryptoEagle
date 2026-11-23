package org.apache.directory.shared.kerberos.codec.authorizationData;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum AuthorizationDataStatesEnum implements States {
   START_STATE,
   AUTHORIZATION_DATA_SEQ_STATE,
   AUTHORIZATION_DATA_SEQ_SEQ_STATE,
   AUTHORIZATION_DATA_ADTYPE_TAG_STATE,
   AUTHORIZATION_DATA_ADTYPE_STATE,
   AUTHORIZATION_DATA_ADDATA_TAG_STATE,
   AUTHORIZATION_DATA_ADDATA_STATE,
   LAST_AUTHORIZATION_DATA_STATE;

   public String getGrammarName(int grammar) {
      return "AUTHORIZATION_DATA_GRAMMAR";
   }

   public String getGrammarName(Grammar<AuthorizationDataContainer> grammar) {
      return grammar instanceof AuthorizationDataGrammar ? "AUTHORIZATION_DATA_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_AUTHORIZATION_DATA_STATE.ordinal() ? "LAST_AUTHORIZATION_DATA_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_AUTHORIZATION_DATA_STATE;
   }

   public AuthorizationDataStatesEnum getStartState() {
      return START_STATE;
   }
}
