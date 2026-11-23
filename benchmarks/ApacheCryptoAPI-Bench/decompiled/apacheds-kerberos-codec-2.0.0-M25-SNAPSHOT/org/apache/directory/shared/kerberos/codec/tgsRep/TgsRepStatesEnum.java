package org.apache.directory.shared.kerberos.codec.tgsRep;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum TgsRepStatesEnum implements States {
   START_STATE,
   TGS_REP_STATE,
   LAST_TGS_REP_STATE;

   public String getGrammarName(int grammar) {
      return "TGS_REP_GRAMMAR";
   }

   public String getGrammarName(Grammar<TgsRepContainer> grammar) {
      return grammar instanceof TgsRepGrammar ? "TGS_REP_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_TGS_REP_STATE.ordinal() ? "TGS_REP_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_TGS_REP_STATE;
   }

   public TgsRepStatesEnum getStartState() {
      return START_STATE;
   }
}
