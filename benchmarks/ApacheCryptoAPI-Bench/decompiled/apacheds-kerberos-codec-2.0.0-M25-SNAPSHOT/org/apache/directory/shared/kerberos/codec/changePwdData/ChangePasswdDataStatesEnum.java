package org.apache.directory.shared.kerberos.codec.changePwdData;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum ChangePasswdDataStatesEnum implements States {
   START_STATE,
   CHNGPWD_SEQ_STATE,
   CHNGPWD_NEWPASSWD_TAG_STATE,
   CHNGPWD_NEWPASSWD_STATE,
   CHNGPWD_TARGNAME_TAG_STATE,
   CHNGPWD_TARGREALM_TAG_STATE,
   CHNGPWD_TARGREALM_STATE,
   LAST_CHNGPWD_STATE;

   public String getGrammarName(int grammar) {
      return "CHNGPWD_DATA_GRAMMAR";
   }

   public String getGrammarName(Grammar<ChangePasswdDataContainer> grammar) {
      return grammar instanceof ChangePasswdDataGrammar ? "CHNGPWD_DATA_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_CHNGPWD_STATE.ordinal() ? "LAST_CHNGPWD_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_CHNGPWD_STATE;
   }

   public ChangePasswdDataStatesEnum getStartState() {
      return START_STATE;
   }
}
