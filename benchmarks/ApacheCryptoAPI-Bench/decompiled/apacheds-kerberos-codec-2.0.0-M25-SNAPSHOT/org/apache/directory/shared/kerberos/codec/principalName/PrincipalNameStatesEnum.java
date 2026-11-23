package org.apache.directory.shared.kerberos.codec.principalName;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum PrincipalNameStatesEnum implements States {
   START_STATE,
   PRINCIPAL_NAME_SEQ_STATE,
   PRINCIPAL_NAME_NAME_TYPE_TAG_STATE,
   PRINCIPAL_NAME_NAME_TYPE_STATE,
   PRINCIPAL_NAME_NAME_STRING_SEQ_STATE,
   PRINCIPAL_NAME_NAME_STRING_TAG_STATE,
   PRINCIPAL_NAME_NAME_STRING_STATE,
   LAST_PRINCIPAL_NAME_STATE;

   public String getGrammarName(int grammar) {
      return "PRINCIPAL_NAME_GRAMMAR";
   }

   public String getGrammarName(Grammar<PrincipalNameContainer> grammar) {
      return grammar instanceof PrincipalNameGrammar ? "PRINCIPAL_NAME_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_PRINCIPAL_NAME_STATE.ordinal() ? "PRINCIPAL_NAME_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_PRINCIPAL_NAME_STATE;
   }

   public PrincipalNameStatesEnum getStartState() {
      return START_STATE;
   }
}
