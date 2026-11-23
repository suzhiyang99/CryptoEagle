package org.apache.directory.shared.kerberos.codec.checksum;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum ChecksumStatesEnum implements States {
   START_STATE,
   CHECKSUM_SEQ_STATE,
   CHECKSUM_TYPE_TAG_STATE,
   CHECKSUM_TYPE_STATE,
   CHECKSUM_CHECKSUM_TAG_STATE,
   CHECKSUM_CHECKSUM_STATE,
   LAST_CHECKSUM_STATE;

   public String getGrammarName(int grammar) {
      return "CHECKSUM_GRAMMAR";
   }

   public String getGrammarName(Grammar<ChecksumContainer> grammar) {
      return grammar instanceof ChecksumGrammar ? "CHECKSUM_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_CHECKSUM_STATE.ordinal() ? "LAST_CHECKSUM_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_CHECKSUM_STATE;
   }

   public ChecksumStatesEnum getStartState() {
      return START_STATE;
   }
}
