package org.apache.directory.shared.kerberos.codec.krbSafeBody;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum KrbSafeBodyStatesEnum implements States {
   START_STATE,
   KRB_SAFE_BODY_SEQ_TAG_STATE,
   KRB_SAFE_BODY_USER_DATA_TAG_STATE,
   KRB_SAFE_BODY_USER_DATA_STATE,
   KRB_SAFE_BODY_TIMESTAMP_TAG_STATE,
   KRB_SAFE_BODY_TIMESTAMP_STATE,
   KRB_SAFE_BODY_USEC_TAG_STATE,
   KRB_SAFE_BODY_USEC_STATE,
   KRB_SAFE_BODY_SEQ_NUMBER_TAG_STATE,
   KRB_SAFE_BODY_SEQ_NUMBER_STATE,
   KRB_SAFE_BODY_SENDER_ADDRESS_TAG_STATE,
   KRB_SAFE_BODY_RECIPIENT_ADDRESS_TAG_STATE,
   LAST_KRB_SAFE_BODY_STATE;

   public String getGrammarName(int grammar) {
      return "KRB_SAFE_BODY_GRAMMAR";
   }

   public String getGrammarName(Grammar<KrbSafeBodyContainer> grammar) {
      return grammar instanceof KrbSafeBodyGrammar ? "KRB_SAFE_BODY_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_KRB_SAFE_BODY_STATE.ordinal() ? "LAST_KRB_SAFE_BODY_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_KRB_SAFE_BODY_STATE;
   }

   public KrbSafeBodyStatesEnum getStartState() {
      return START_STATE;
   }
}
