package org.apache.directory.shared.kerberos.codec.typedData;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum TypedDataStatesEnum implements States {
   START_STATE,
   TYPED_DATA_SEQ_STATE,
   TYPED_DATA_SEQ_SEQ_STATE,
   TYPED_DATA_TDTYPE_TAG_STATE,
   TYPED_DATA_TDTYPE_STATE,
   TYPED_DATA_TDDATA_TAG_STATE,
   TYPED_DATA_TDDATA_STATE,
   LAST_TYPED_DATA_STATE;

   public String getGrammarName(int grammar) {
      return "TYPED_DATA_GRAMMAR";
   }

   public String getGrammarName(Grammar<TypedDataContainer> grammar) {
      return grammar instanceof TypedDataGrammar ? "TYPED_DATA_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_TYPED_DATA_STATE.ordinal() ? "LAST_TYPED_DATA_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_TYPED_DATA_STATE;
   }

   public TypedDataStatesEnum getStartState() {
      return START_STATE;
   }
}
