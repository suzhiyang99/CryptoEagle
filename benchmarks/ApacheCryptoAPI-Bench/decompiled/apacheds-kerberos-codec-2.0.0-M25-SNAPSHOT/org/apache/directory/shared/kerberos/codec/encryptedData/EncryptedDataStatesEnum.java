package org.apache.directory.shared.kerberos.codec.encryptedData;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum EncryptedDataStatesEnum implements States {
   START_STATE,
   ENCRYPTED_DATA_SEQ_STATE,
   ENCRYPTED_DATA_ETYPE_TAG_STATE,
   ENCRYPTED_DATA_ETYPE_STATE,
   ENCRYPTED_DATA_KVNO_TAG_STATE,
   ENCRYPTED_DATA_KVNO_STATE,
   ENCRYPTED_DATA_CIPHER_TAG_STATE,
   ENCRYPTED_DATA_CIPHER_STATE,
   LAST_ENCRYPTED_DATA_STATE;

   public String getGrammarName(int grammar) {
      return "ENCRYPTED_DATA_GRAMMAR";
   }

   public String getGrammarName(Grammar<EncryptedDataContainer> grammar) {
      return grammar instanceof EncryptedDataGrammar ? "ENCRYPTED_DATA_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ENCRYPTED_DATA_STATE.ordinal() ? "ENCRYPTED_DATA_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ENCRYPTED_DATA_STATE;
   }

   public EncryptedDataStatesEnum getStartState() {
      return START_STATE;
   }
}
