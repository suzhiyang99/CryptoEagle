package org.apache.directory.shared.kerberos.codec.adAndOr;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum AdAndOrStatesEnum implements States {
   START_STATE,
   AD_AND_OR_STATE,
   AD_AND_OR_CONDITION_COUNT_TAG_STATE,
   AD_AND_OR_CONDITION_COUNT_STATE,
   AD_AND_OR_ELEMENTS_TAG_STATE,
   LAST_AD_AND_OR_STATE;

   public String getGrammarName(int grammar) {
      return "AD_AND_OR_GRAMMAR";
   }

   public String getGrammarName(Grammar<AdAndOrContainer> grammar) {
      return grammar instanceof AdAndOrGrammar ? "AD_AND_OR_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_AD_AND_OR_STATE.ordinal() ? "AD_AND_OR_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_AD_AND_OR_STATE;
   }

   public AdAndOrStatesEnum getStartState() {
      return START_STATE;
   }
}
