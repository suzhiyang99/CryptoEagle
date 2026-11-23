package org.apache.directory.shared.kerberos.codec.etypeInfo2Entry;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum ETypeInfo2EntryStatesEnum implements States {
   START_STATE,
   ETYPE_INFO2_ENTRY_SEQ_STATE,
   ETYPE_INFO2_ENTRY_ETYPE_TAG_STATE,
   ETYPE_INFO2_ENTRY_ETYPE_STATE,
   ETYPE_INFO2_ENTRY_SALT_TAG_STATE,
   ETYPE_INFO2_ENTRY_SALT_STATE,
   ETYPE_INFO2_ENTRY_S2KPARAMS_TAG_STATE,
   ETYPE_INFO2_ENTRY_S2KPARAMS_STATE,
   LAST_ETYPE_INFO2_ENTRY_STATE;

   public String getGrammarName(int grammar) {
      return "ETYPE_INFO2_ENTRY_GRAMMAR";
   }

   public String getGrammarName(Grammar<ETypeInfo2EntryContainer> grammar) {
      return grammar instanceof ETypeInfo2EntryGrammar ? "ETYPE_INFO2_ENTRY_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ETYPE_INFO2_ENTRY_STATE.ordinal() ? "LAST_ETYPE_INFO2_ENTRY_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ETYPE_INFO2_ENTRY_STATE;
   }

   public ETypeInfo2EntryStatesEnum getStartState() {
      return START_STATE;
   }
}
