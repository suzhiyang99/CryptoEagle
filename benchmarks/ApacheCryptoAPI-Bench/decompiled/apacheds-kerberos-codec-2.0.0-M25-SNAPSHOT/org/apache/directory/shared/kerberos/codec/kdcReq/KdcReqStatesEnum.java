package org.apache.directory.shared.kerberos.codec.kdcReq;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum KdcReqStatesEnum implements States {
   START_STATE,
   KDC_REQ_SEQ_STATE,
   KDC_REQ_PVNO_TAG_STATE,
   KDC_REQ_PVNO_STATE,
   KDC_REQ_MSG_TYPE_TAG_STATE,
   KDC_REQ_MSG_TYPE_STATE,
   KDC_REQ_PA_DATA_OR_REQ_BODY_STATE,
   KDC_REQ_PA_DATA_TAG_STATE,
   KDC_REQ_PA_DATA_SEQ_STATE,
   KDC_REQ_PA_DATA_STATE,
   KDC_REQ_KDC_REQ_BODY_STATE,
   LAST_KDC_REQ_STATE;

   public String getGrammarName(int grammar) {
      return "KDC_REQ_GRAMMAR";
   }

   public String getGrammarName(Grammar<KdcReqContainer> grammar) {
      return grammar instanceof KdcReqGrammar ? "KDC_REQ_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_KDC_REQ_STATE.ordinal() ? "KDC_REQ_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_KDC_REQ_STATE;
   }

   public KdcReqStatesEnum getStartState() {
      return START_STATE;
   }
}
