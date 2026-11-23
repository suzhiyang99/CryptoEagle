package org.apache.directory.shared.kerberos.codec.encAsRepPart;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum EncAsRepPartStatesEnum implements States {
   START_STATE,
   ENC_AS_REP_PART_STATE,
   LAST_ENC_AS_REP_PART_STATE;

   public String getGrammarName(int grammar) {
      return "ENC_AS_REP_PART_GRAMMAR";
   }

   public String getGrammarName(Grammar<EncAsRepPartContainer> grammar) {
      return grammar instanceof EncAsRepPartGrammar ? "ENC_AS_REP_PART_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ENC_AS_REP_PART_STATE.ordinal() ? "ENC_AS_REP_PART_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ENC_AS_REP_PART_STATE;
   }

   public EncAsRepPartStatesEnum getStartState() {
      return START_STATE;
   }
}
