package org.apache.directory.shared.kerberos.codec.lastReq;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum LastReqStatesEnum implements States {
   START_STATE,
   LAST_REQ_SEQ_STATE,
   LAST_REQ_SEQ_SEQ_STATE,
   LAST_REQ_LR_TYPE_TAG_STATE,
   LAST_REQ_LR_TYPE_STATE,
   LAST_REQ_LR_VALUE_TAG_STATE,
   LAST_REQ_LR_VALUE_STATE,
   LAST_LAST_REQ_STATE;

   public String getGrammarName(int grammar) {
      return "LAST_REQ_GRAMMAR";
   }

   public String getGrammarName(Grammar<LastReqContainer> grammar) {
      return grammar instanceof LastReqGrammar ? "LAST_REQ_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_LAST_REQ_STATE.ordinal() ? "LAST_LAST_REQ_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_LAST_REQ_STATE;
   }

   public LastReqStatesEnum getStartState() {
      return START_STATE;
   }
}
