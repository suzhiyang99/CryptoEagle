package org.apache.directory.shared.kerberos.codec.encTgsRepPart;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum EncTgsRepPartStatesEnum implements States {
   START_STATE,
   ENC_TGS_REP_PART_STATE,
   LAST_ENC_TGS_REP_PART_STATE;

   public String getGrammarName(int grammar) {
      return "ENC_TGS_REP_PART_GRAMMAR";
   }

   public String getGrammarName(Grammar<EncTgsRepPartContainer> grammar) {
      return grammar instanceof EncTgsRepPartGrammar ? "ENC_TGS_REP_PART_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ENC_TGS_REP_PART_STATE.ordinal() ? "ENC_TGS_REP_PART_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ENC_TGS_REP_PART_STATE;
   }

   public EncTgsRepPartStatesEnum getStartState() {
      return START_STATE;
   }
}
