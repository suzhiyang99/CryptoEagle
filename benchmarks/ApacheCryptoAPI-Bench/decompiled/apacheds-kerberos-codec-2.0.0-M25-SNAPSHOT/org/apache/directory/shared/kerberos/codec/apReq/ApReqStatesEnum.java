package org.apache.directory.shared.kerberos.codec.apReq;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum ApReqStatesEnum implements States {
   START_STATE,
   AP_REQ_STATE,
   AP_REQ_SEQ_STATE,
   AP_REQ_PVNO_TAG_STATE,
   AP_REQ_PVNO_STATE,
   AP_REQ_MSG_TYPE_TAG_STATE,
   AP_REQ_MSG_TYPE_STATE,
   AP_REQ_AP_OPTIONS_TAG_STATE,
   AP_REQ_AP_OPTIONS_STATE,
   AP_REQ_TICKET_STATE,
   AP_REQ_AUTHENTICATOR_STATE,
   LAST_AP_REQ_STATE;

   public String getGrammarName(int grammar) {
      return "AP_REQ_GRAMMAR";
   }

   public String getGrammarName(Grammar<ApReqContainer> grammar) {
      return grammar instanceof ApReqGrammar ? "AP_REQ_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_AP_REQ_STATE.ordinal() ? "AP_REQ_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_AP_REQ_STATE;
   }

   public ApReqStatesEnum getStartState() {
      return START_STATE;
   }
}
