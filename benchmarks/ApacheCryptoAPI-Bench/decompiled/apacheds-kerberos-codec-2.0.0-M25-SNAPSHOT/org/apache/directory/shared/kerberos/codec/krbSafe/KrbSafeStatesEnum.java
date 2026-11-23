package org.apache.directory.shared.kerberos.codec.krbSafe;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum KrbSafeStatesEnum implements States {
   START_STATE,
   KRB_SAFE_TAG_STATE,
   KRB_SAFE_SEQ_STATE,
   KRB_SAFE_PVNO_TAG_STATE,
   KRB_SAFE_PVNO_STATE,
   KRB_SAFE_MSGTYPE_TAG_STATE,
   KRB_SAFE_MSGTYPE_STATE,
   KRB_SAFE_SAFE_BODY_TAG_STATE,
   KRB_SAFE_CKSUM_TAG_STATE,
   LAST_KRB_SAFE_STATE;

   public String getGrammarName(int grammar) {
      return "KRB_SAFE_GRAMMAR";
   }

   public String getGrammarName(Grammar<KrbSafeContainer> grammar) {
      return grammar instanceof KrbSafeGrammar ? "KRB_SAFE_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_KRB_SAFE_STATE.ordinal() ? "LAST_KRB_SAFE_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_KRB_SAFE_STATE;
   }

   public KrbSafeStatesEnum getStartState() {
      return START_STATE;
   }
}
