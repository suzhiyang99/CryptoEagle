package org.apache.directory.shared.kerberos.codec.encryptionKey;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum EncryptionKeyStatesEnum implements States {
   START_STATE,
   ENCRYPTION_KEY_SEQ_STATE,
   ENCRYPTION_KEY_TYPE_TAG_STATE,
   ENCRYPTION_KEY_TYPE_STATE,
   ENCRYPTION_KEY_VALUE_TAG_STATE,
   ENCRYPTION_KEY_VALUE_STATE,
   LAST_ENCRYPTION_KEY_STATE;

   public String getGrammarName(int grammar) {
      return "ENCRYPTION_KEY_GRAMMAR";
   }

   public String getGrammarName(Grammar<EncryptionKeyContainer> grammar) {
      return grammar instanceof EncryptionKeyGrammar ? "ENCRYPTION_KEY_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ENCRYPTION_KEY_STATE.ordinal() ? "LAST_ENCRYPTION_KEY_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ENCRYPTION_KEY_STATE;
   }

   public EncryptionKeyStatesEnum getStartState() {
      return START_STATE;
   }
}
