package org.apache.directory.shared.kerberos.codec.methodData;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum MethodDataStatesEnum implements States {
   START_STATE,
   METHOD_DATA_SEQ_STATE,
   LAST_METHOD_DATA_STATE;

   public String getGrammarName(int grammar) {
      return "METHOD_DATA_GRAMMAR";
   }

   public String getGrammarName(Grammar<MethodDataContainer> grammar) {
      return grammar instanceof MethodDataGrammar ? "METHOD_DATA_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_METHOD_DATA_STATE.ordinal() ? "LAST_METHOD_DATA_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_METHOD_DATA_STATE;
   }

   public MethodDataStatesEnum getStartState() {
      return START_STATE;
   }
}
