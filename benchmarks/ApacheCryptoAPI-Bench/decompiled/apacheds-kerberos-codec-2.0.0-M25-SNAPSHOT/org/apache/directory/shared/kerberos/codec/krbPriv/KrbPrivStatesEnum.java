package org.apache.directory.shared.kerberos.codec.krbPriv;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum KrbPrivStatesEnum implements States {
   START_STATE,
   KRB_PRIV_TAG_STATE,
   KRB_PRIV_SEQ_STATE,
   KRB_PRIV_PVNO_TAG_STATE,
   KRB_PRIV_PVNO_STATE,
   KRB_PRIV_MSGTYPE_TAG_STATE,
   KRB_PRIV_MSGTYPE_STATE,
   KRB_PRIV_EN_PART_TAG_STATE,
   LAST_KRB_PRIV_STATE;

   public String getGrammarName(int grammar) {
      return "KRB_PRIV_GRAMMAR";
   }

   public String getGrammarName(Grammar<KrbPrivContainer> grammar) {
      return grammar instanceof KrbPrivGrammar ? "KRB_PRIV_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_KRB_PRIV_STATE.ordinal() ? "LAST_KRB_PRIV_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_KRB_PRIV_STATE;
   }

   public KrbPrivStatesEnum getStartState() {
      return START_STATE;
   }
}
