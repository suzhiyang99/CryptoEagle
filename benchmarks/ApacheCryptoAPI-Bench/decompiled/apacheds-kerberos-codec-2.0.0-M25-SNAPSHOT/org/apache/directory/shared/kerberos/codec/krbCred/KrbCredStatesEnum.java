package org.apache.directory.shared.kerberos.codec.krbCred;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum KrbCredStatesEnum implements States {
   START_STATE,
   KRB_CRED_TAG_STATE,
   KRB_CRED_SEQ_TAG_STATE,
   KRB_CRED_PVNO_TAG_STATE,
   KRB_CRED_PVNO_STATE,
   KRB_CRED_MSGTYPE_TAG_STATE,
   KRB_CRED_MSGTYPE_STATE,
   KRB_CRED_TICKETS_TAG_STATE,
   KRB_CRED_TICKETS_STATE,
   KRB_CRED_ENCPART_TAG_STATE,
   LAST_KRB_CRED_STATE;

   public String getGrammarName(int grammar) {
      return "KRB_CRED_GRAMMAR";
   }

   public String getGrammarName(Grammar<KrbCredContainer> grammar) {
      return grammar instanceof KrbCredGrammar ? "KRB_CRED_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_KRB_CRED_STATE.ordinal() ? "LAST_KRB_CRED_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_KRB_CRED_STATE;
   }

   public KrbCredStatesEnum getStartState() {
      return START_STATE;
   }
}
