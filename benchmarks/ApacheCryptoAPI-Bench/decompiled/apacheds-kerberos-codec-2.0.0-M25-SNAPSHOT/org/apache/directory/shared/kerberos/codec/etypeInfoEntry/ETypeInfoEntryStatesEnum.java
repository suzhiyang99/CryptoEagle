package org.apache.directory.shared.kerberos.codec.etypeInfoEntry;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum ETypeInfoEntryStatesEnum implements States {
   START_STATE,
   ETYPE_INFO_ENTRY_SEQ_STATE,
   ETYPE_INFO_ENTRY_ETYPE_TAG_STATE,
   ETYPE_INFO_ENTRY_ETYPE_STATE,
   ETYPE_INFO_ENTRY_SALT_TAG_STATE,
   ETYPE_INFO_ENTRY_SALT_STATE,
   LAST_ETYPE_INFO_ENTRY_STATE;

   public String getGrammarName(int grammar) {
      return "ETYPE_INFO_ENTRY_GRAMMAR";
   }

   public String getGrammarName(Grammar<ETypeInfoEntryContainer> grammar) {
      return grammar instanceof ETypeInfoEntryGrammar ? "ETYPE_INFO_ENTRY_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ETYPE_INFO_ENTRY_STATE.ordinal() ? "LAST_ETYPE_INFO_ENTRY_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ETYPE_INFO_ENTRY_STATE;
   }

   public ETypeInfoEntryStatesEnum getStartState() {
      return START_STATE;
   }
}
