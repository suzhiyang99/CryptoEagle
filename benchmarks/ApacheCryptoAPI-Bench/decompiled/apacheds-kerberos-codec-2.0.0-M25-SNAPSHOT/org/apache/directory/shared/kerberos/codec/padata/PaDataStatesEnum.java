package org.apache.directory.shared.kerberos.codec.padata;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum PaDataStatesEnum implements States {
   START_STATE,
   PADATA_SEQ_STATE,
   PADATA_TYPE_TAG_STATE,
   PADATA_TYPE_STATE,
   PADATA_VALUE_TAG_STATE,
   PADATA_VALUE_STATE,
   LAST_PADATA_STATE;

   public String getGrammarName(int grammar) {
      return "PADATA_GRAMMAR";
   }

   public String getGrammarName(Grammar<PaDataContainer> grammar) {
      return grammar instanceof PaDataGrammar ? "PADATA_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_PADATA_STATE.ordinal() ? "LAST_PADATA_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_PADATA_STATE;
   }

   public PaDataStatesEnum getStartState() {
      return START_STATE;
   }
}
