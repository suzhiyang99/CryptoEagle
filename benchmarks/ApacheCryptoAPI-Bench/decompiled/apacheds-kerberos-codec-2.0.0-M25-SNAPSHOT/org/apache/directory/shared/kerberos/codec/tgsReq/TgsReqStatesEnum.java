package org.apache.directory.shared.kerberos.codec.tgsReq;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum TgsReqStatesEnum implements States {
   START_STATE,
   TGS_REQ_STATE,
   LAST_TGS_REQ_STATE;

   public String getGrammarName(int grammar) {
      return "TGS_REQ_GRAMMAR";
   }

   public String getGrammarName(Grammar<TgsReqContainer> grammar) {
      return grammar instanceof TgsReqGrammar ? "TGS_REQ_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_TGS_REQ_STATE.ordinal() ? "TGS_REQ_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_TGS_REQ_STATE;
   }

   public TgsReqStatesEnum getStartState() {
      return START_STATE;
   }
}
