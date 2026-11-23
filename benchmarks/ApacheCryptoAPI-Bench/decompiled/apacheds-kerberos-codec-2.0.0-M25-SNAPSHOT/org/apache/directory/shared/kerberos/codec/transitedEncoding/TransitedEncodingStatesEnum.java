package org.apache.directory.shared.kerberos.codec.transitedEncoding;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum TransitedEncodingStatesEnum implements States {
   START_STATE,
   TRANSITED_ENCODING_SEQ_STATE,
   TRANSITED_ENCODING_TR_TYPE_TAG_STATE,
   TRANSITED_ENCODING_TR_TYPE_STATE,
   TRANSITED_ENCODING_CONTENTS_TAG_STATE,
   TRANSITED_ENCODING_CONTENTS_STATE,
   LAST_TRANSITED_ENCODING_STATE;

   public String getGrammarName(int grammar) {
      return "TRANSITED_ENCODING_GRAMMAR";
   }

   public String getGrammarName(Grammar<TransitedEncodingContainer> grammar) {
      return grammar instanceof TransitedEncodingGrammar ? "TRANSITED_ENCODING_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_TRANSITED_ENCODING_STATE.ordinal() ? "TRANSITED_ENCODING_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_TRANSITED_ENCODING_STATE;
   }

   public TransitedEncodingStatesEnum getStartState() {
      return START_STATE;
   }
}
