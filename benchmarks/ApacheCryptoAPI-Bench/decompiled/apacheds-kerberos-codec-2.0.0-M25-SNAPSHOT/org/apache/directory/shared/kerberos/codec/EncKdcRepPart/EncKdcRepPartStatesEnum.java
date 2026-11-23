package org.apache.directory.shared.kerberos.codec.EncKdcRepPart;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum EncKdcRepPartStatesEnum implements States {
   START_STATE,
   ENC_KDC_REP_PART_SEQ_TAG_STATE,
   ENC_KDC_REP_PART_KEY_TAG_STATE,
   ENC_KDC_REP_PART_LAST_REQ_TAG_STATE,
   ENC_KDC_REP_PART_NONCE_TAG_STATE,
   ENC_KDC_REP_PART_NONCE_STATE,
   ENC_KDC_REP_PART_KEY_EXPIRATION_TAG_STATE,
   ENC_KDC_REP_PART_KEY_EXPIRATION_STATE,
   ENC_KDC_REP_PART_FLAGS_TAG_STATE,
   ENC_KDC_REP_PART_FLAGS_STATE,
   ENC_KDC_REP_PART_AUTH_TIME_TAG_STATE,
   ENC_KDC_REP_PART_AUTH_TIME_STATE,
   ENC_KDC_REP_PART_START_TIME_TAG_STATE,
   ENC_KDC_REP_PART_START_TIME_STATE,
   ENC_KDC_REP_PART_END_TIME_TAG_STATE,
   ENC_KDC_REP_PART_END_TIME_STATE,
   ENC_KDC_REP_PART_RENEW_TILL_TAG_STATE,
   ENC_KDC_REP_PART_RENEW_TILL_STATE,
   ENC_KDC_REP_PART_SREALM_TAG_STATE,
   ENC_KDC_REP_PART_SREALM_STATE,
   ENC_KDC_REP_PART_SNAME_TAG_STATE,
   ENC_KDC_REP_PART_CADDR_TAG_STATE,
   LAST_ENC_KDC_REP_PART_STATE;

   public String getGrammarName(int grammar) {
      return "ENC_KDC_REP_PART_GRAMMAR";
   }

   public String getGrammarName(Grammar<EncKdcRepPartContainer> grammar) {
      return grammar instanceof EncKdcRepPartGrammar ? "ENC_KDC_REP_PART_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ENC_KDC_REP_PART_STATE.ordinal() ? "LAST_ENC_KDC_REP_PART_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ENC_KDC_REP_PART_STATE;
   }

   public EncKdcRepPartStatesEnum getStartState() {
      return START_STATE;
   }
}
