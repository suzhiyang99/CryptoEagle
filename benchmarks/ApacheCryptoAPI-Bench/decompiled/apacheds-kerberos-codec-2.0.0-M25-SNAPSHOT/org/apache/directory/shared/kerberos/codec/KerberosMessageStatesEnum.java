package org.apache.directory.shared.kerberos.codec;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum KerberosMessageStatesEnum implements States {
   START_STATE,
   AS_REQ_STATE,
   AS_REP_TAG_STATE,
   TGS_REQ_TAG_STATE,
   TGS_REP_TAG_STATE,
   AP_REQ_TAG_STATE,
   AP_REP_TAG_STATE,
   KRB_SAFE_STATE,
   KRB_PRIV_STATE,
   KRB_CRED_STATE,
   KRB_ERROR_STATE,
   LAST_KERBEROS_MESSAGE_STATE;

   public String getGrammarName(int grammar) {
      return "KERBEROS_MESSAGE_GRAMMAR";
   }

   public String getGrammarName(Grammar<KerberosMessageContainer> grammar) {
      return grammar instanceof KerberosMessageGrammar ? "KERBEROS_MESSAGE_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_KERBEROS_MESSAGE_STATE.ordinal() ? "KERBEROS_MESSAGE_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_KERBEROS_MESSAGE_STATE;
   }

   public KerberosMessageStatesEnum getStartState() {
      return START_STATE;
   }
}
