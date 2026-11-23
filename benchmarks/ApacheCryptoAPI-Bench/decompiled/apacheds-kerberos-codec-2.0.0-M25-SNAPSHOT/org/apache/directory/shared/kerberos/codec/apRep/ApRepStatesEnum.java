package org.apache.directory.shared.kerberos.codec.apRep;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum ApRepStatesEnum implements States {
   START_STATE,
   AP_REP_STATE,
   AP_REP_SEQ_STATE,
   AP_REP_PVNO_TAG_STATE,
   AP_REP_PVNO_STATE,
   AP_REP_MSG_TYPE_TAG_STATE,
   AP_REP_MSG_TYPE_STATE,
   AP_REP_ENC_PART_STATE,
   LAST_AP_REP_STATE;

   public String getGrammarName(int grammar) {
      return "AP_REP_GRAMMAR";
   }

   public String getGrammarName(Grammar<ApRepContainer> grammar) {
      return grammar instanceof ApRepGrammar ? "AP_REP_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_AP_REP_STATE.ordinal() ? "AP_REP_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_AP_REP_STATE;
   }

   public ApRepStatesEnum getStartState() {
      return START_STATE;
   }
}
