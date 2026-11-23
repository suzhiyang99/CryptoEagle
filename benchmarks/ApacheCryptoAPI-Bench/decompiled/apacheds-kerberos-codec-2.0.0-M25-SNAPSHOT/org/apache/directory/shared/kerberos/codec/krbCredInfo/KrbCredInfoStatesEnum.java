package org.apache.directory.shared.kerberos.codec.krbCredInfo;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum KrbCredInfoStatesEnum implements States {
   START_STATE,
   KRB_CRED_INFO_SEQ_TAG_STATE,
   KRB_CRED_INFO_KEY_TAG_STATE,
   KRB_CRED_INFO_PREALM_TAG_STATE,
   KRB_CRED_INFO_PREALM_STATE,
   KRB_CRED_INFO_PNAME_TAG_STATE,
   KRB_CRED_INFO_FLAGS_TAG_STATE,
   KRB_CRED_INFO_FLAGS_STATE,
   KRB_CRED_INFO_AUTHTIME_TAG_STATE,
   KRB_CRED_INFO_AUTHTIME_STATE,
   KRB_CRED_INFO_STARTTIME_TAG_STATE,
   KRB_CRED_INFO_STARTTIME_STATE,
   KRB_CRED_INFO_ENDTIME_TAG_STATE,
   KRB_CRED_INFO_ENDTIME_STATE,
   KRB_CRED_INFO_RENEWTILL_TAG_STATE,
   KRB_CRED_INFO_RENEWTILL_STATE,
   KRB_CRED_INFO_SREALM_TAG_STATE,
   KRB_CRED_INFO_SREALM_STATE,
   KRB_CRED_INFO_SNAME_TAG_STATE,
   KRB_CRED_INFO_CADDR_TAG_STATE,
   LAST_KRB_CRED_INFO_STATE;

   public String getGrammarName(int grammar) {
      return "KRB_CRED_INFO_GRAMMAR";
   }

   public String getGrammarName(Grammar<KrbCredInfoContainer> grammar) {
      return grammar instanceof KrbCredInfoGrammar ? "KRB_CRED_INFO_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_KRB_CRED_INFO_STATE.ordinal() ? "LAST_KRB_CRED_INFO_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_KRB_CRED_INFO_STATE;
   }

   public KrbCredInfoStatesEnum getStartState() {
      return START_STATE;
   }
}
