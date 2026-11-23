package org.apache.directory.shared.kerberos.codec.etypeInfo;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum ETypeInfoStatesEnum implements States {
   START_STATE,
   ETYPE_INFO_SEQ_STATE,
   LAST_ETYPE_INFO_STATE;

   public String getGrammarName(int grammar) {
      return "ETYPE_INFO_GRAMMAR";
   }

   public String getGrammarName(Grammar<ETypeInfoContainer> grammar) {
      return grammar instanceof ETypeInfoGrammar ? "ETYPE_INFO_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ETYPE_INFO_STATE.ordinal() ? "LAST_ETYPE_INFO_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ETYPE_INFO_STATE;
   }

   public ETypeInfoStatesEnum getStartState() {
      return START_STATE;
   }
}
