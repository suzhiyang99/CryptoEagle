package org.apache.directory.shared.kerberos.codec.kdcReqBody;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum KdcReqBodyStatesEnum implements States {
   START_STATE,
   KDC_REQ_BODY_SEQ_STATE,
   KDC_REQ_BODY_KDC_OPTIONS_TAG_STATE,
   KDC_REQ_BODY_KDC_OPTIONS_STATE,
   KDC_REQ_BODY_CNAME_OR_REALM_TAG_STATE,
   KDC_REQ_BODY_CNAME_STATE,
   KDC_REQ_BODY_REALM_TAG_STATE,
   KDC_REQ_BODY_SNAME_OR_FROM_OR_TILL_TAG_STATE,
   KDC_REQ_BODY_SNAME_STATE,
   KDC_REQ_BODY_FROM_STATE,
   KDC_REQ_BODY_TILL_TAG_STATE,
   KDC_REQ_BODY_TILL_STATE,
   KDC_REQ_BODY_RTIME_OR_NONCE_TAG_STATE,
   KDC_REQ_BODY_RTIME_STATE,
   KDC_REQ_BODY_NONCE_TAG_STATE,
   KDC_REQ_BODY_NONCE_STATE,
   KDC_REQ_BODY_ETYPE_TAG_STATE,
   KDC_REQ_BODY_ETYPE_SEQ_STATE,
   KDC_REQ_BODY_ETYPE_STATE,
   KDC_REQ_BODY_ADDRESSES_STATE,
   KDC_REQ_BODY_ENC_AUTH_DATA_STATE,
   KDC_REQ_BODY_ADDITIONAL_TICKETS_TAG_STATE,
   KDC_REQ_BODY_ADDITIONAL_TICKETS_SEQ_STATE,
   KDC_REQ_BODY_ADDITIONAL_TICKETS_STATE,
   LAST_KDC_REQ_BODY_STATE;

   public String getGrammarName(int grammar) {
      return "KDC_REQ_BODY_GRAMMAR";
   }

   public String getGrammarName(Grammar<KdcReqBodyContainer> grammar) {
      return grammar instanceof KdcReqBodyGrammar ? "KDC_REQ_BODY_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_KDC_REQ_BODY_STATE.ordinal() ? "KDC_REQ_BODY_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_KDC_REQ_BODY_STATE;
   }

   public KdcReqBodyStatesEnum getStartState() {
      return START_STATE;
   }
}
