package org.apache.directory.shared.kerberos.codec.asReq;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum AsReqStatesEnum implements States {
   START_STATE,
   AS_REQ_STATE,
   LAST_AS_REQ_STATE;

   public String getGrammarName(int grammar) {
      return "AS_REQ_GRAMMAR";
   }

   public String getGrammarName(Grammar<AsReqContainer> grammar) {
      return grammar instanceof AsReqGrammar ? "AS_REQ_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_AS_REQ_STATE.ordinal() ? "AS_REQ_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_AS_REQ_STATE;
   }

   public AsReqStatesEnum getStartState() {
      return START_STATE;
   }
}
