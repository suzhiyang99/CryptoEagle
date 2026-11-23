package org.apache.directory.shared.kerberos.codec.krbError;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum KrbErrorStatesEnum implements States {
   START_STATE,
   KRB_ERR_TAG,
   KRB_ERR_SEQ_STATE,
   KRB_ERR_PVNO_TAG_STATE,
   KRB_ERR_PVNO_STATE,
   KRB_ERR_MSG_TYPE_TAG_STATE,
   KRB_ERR_MSG_TYPE_STATE,
   KRB_ERR_CTIME_TAG_STATE,
   KRB_ERR_CTIME_STATE,
   KRB_ERR_CUSEC_TAG_STATE,
   KRB_ERR_CUSEC_STATE,
   KRB_ERR_STIME_TAG_STATE,
   KRB_ERR_STIME_STATE,
   KRB_ERR_SUSEC_TAG_STATE,
   KRB_ERR_SUSEC_STATE,
   KRB_ERR_ERROR_CODE_TAG_STATE,
   KRB_ERR_ERROR_CODE_STATE,
   KRB_ERR_CREALM_TAG_STATE,
   KRB_ERR_CREALM_STATE,
   KRB_ERR_CNAME_STATE,
   KRB_ERR_REALM_TAG_STATE,
   KRB_ERR_REALM_STATE,
   KRB_ERR_SNAME_STATE,
   KRB_ERR_ETEXT_TAG_STATE,
   KRB_ERR_ETEXT_STATE,
   KRB_ERR_EDATA_TAG_STATE,
   KRB_ERR_EDATA_STATE,
   LAST_KRB_ERR_STATE;

   public String getGrammarName(int grammar) {
      return "KRB_ERR_GRAMMAR";
   }

   public String getGrammarName(Grammar<KrbErrorContainer> grammar) {
      return grammar instanceof KrbErrorGrammar ? "KRB_ERR_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_KRB_ERR_STATE.ordinal() ? "LAST_KRB_ERR_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_KRB_ERR_STATE;
   }

   public KrbErrorStatesEnum getStartState() {
      return START_STATE;
   }
}
