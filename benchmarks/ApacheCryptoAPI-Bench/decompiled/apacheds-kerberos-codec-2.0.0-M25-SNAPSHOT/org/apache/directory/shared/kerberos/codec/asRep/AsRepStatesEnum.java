package org.apache.directory.shared.kerberos.codec.asRep;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum AsRepStatesEnum implements States {
   START_STATE,
   AS_REP_STATE,
   LAST_AS_REP_STATE;

   public String getGrammarName(int grammar) {
      return "AS_REP_GRAMMAR";
   }

   public String getGrammarName(Grammar<AsRepContainer> grammar) {
      return grammar instanceof AsRepGrammar ? "AS_REP_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_AS_REP_STATE.ordinal() ? "AS_REP_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_AS_REP_STATE;
   }

   public AsRepStatesEnum getStartState() {
      return START_STATE;
   }
}
