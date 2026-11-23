package org.apache.directory.shared.kerberos.codec.etypeInfo2;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum ETypeInfo2StatesEnum implements States {
   START_STATE,
   ETYPE_INFO2_SEQ_STATE,
   LAST_ETYPE_INFO2_STATE;

   public String getGrammarName(int grammar) {
      return "ETYPE_INFO2_GRAMMAR";
   }

   public String getGrammarName(Grammar<ETypeInfo2Container> grammar) {
      return grammar instanceof ETypeInfo2Grammar ? "ETYPE_INFO2_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ETYPE_INFO2_STATE.ordinal() ? "LAST_ETYPE_INFO2_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ETYPE_INFO2_STATE;
   }

   public ETypeInfo2StatesEnum getStartState() {
      return START_STATE;
   }
}
