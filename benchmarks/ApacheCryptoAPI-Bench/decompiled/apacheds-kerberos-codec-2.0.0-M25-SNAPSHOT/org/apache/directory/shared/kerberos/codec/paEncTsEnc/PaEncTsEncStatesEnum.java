package org.apache.directory.shared.kerberos.codec.paEncTsEnc;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum PaEncTsEncStatesEnum implements States {
   START_STATE,
   PA_ENC_TS_ENC_STATE,
   PA_ENC_TS_ENC_PA_TIMESTAMP_TAG_STATE,
   PA_ENC_TS_PA_TIMESTAMP_STATE,
   PA_ENC_TS_ENC_PA_USEC_TAG_STATE,
   PA_ENC_TS_ENC_PA_USEC_STATE,
   LAST_PA_ENC_TS_ENC_STATE;

   public String getGrammarName(int grammar) {
      return "PA_ENC_TS_ENC_GRAMMAR";
   }

   public String getGrammarName(Grammar<PaEncTsEncContainer> grammar) {
      return grammar instanceof PaEncTsEncGrammar ? "PA_ENC_TS_ENC_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_PA_ENC_TS_ENC_STATE.ordinal() ? "PA_ENC_TS_ENC_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_PA_ENC_TS_ENC_STATE;
   }

   public PaEncTsEncStatesEnum getStartState() {
      return START_STATE;
   }
}
