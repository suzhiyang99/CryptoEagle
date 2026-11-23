package org.apache.directory.shared.kerberos.codec.encApRepPart;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum EncApRepPartStatesEnum implements States {
   START_STATE,
   ENC_AP_REP_PART_STATE,
   ENC_AP_REP_PART_SEQ_STATE,
   ENC_AP_REP_PART_CTIME_TAG_STATE,
   ENC_AP_REP_PART_CTIME_STATE,
   ENC_AP_REP_PART_CUSEC_TAG_STATE,
   ENC_AP_REP_PART_CUSEC_STATE,
   ENC_AP_REP_PART_SUBKEY_STATE,
   ENC_AP_REP_PART_SEQ_NUMBER_TAG_STATE,
   ENC_AP_REP_PART_SEQ_NUMBER_STATE,
   LAST_ENC_AP_REP_PART_STATE;

   public String getGrammarName(int grammar) {
      return "ENC_AP_REP_PART_GRAMMAR";
   }

   public String getGrammarName(Grammar<EncApRepPartContainer> grammar) {
      return grammar instanceof EncApRepPartGrammar ? "ENC_AP_REP_PART_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ENC_AP_REP_PART_STATE.ordinal() ? "ENC_AP_REP_PART_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ENC_AP_REP_PART_STATE;
   }

   public EncApRepPartStatesEnum getStartState() {
      return START_STATE;
   }
}
