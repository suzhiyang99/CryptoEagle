package org.apache.directory.shared.kerberos.codec.encKrbPrivPart;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum EncKrbPrivPartStatesEnum implements States {
   START_STATE,
   ENC_KRB_PRIV_PART_TAG_STATE,
   ENC_KRB_PRIV_PART_SEQ_TAG_STATE,
   ENC_KRB_PRIV_PART_USER_DATA_TAG_STATE,
   ENC_KRB_PRIV_PART_USER_DATA_STATE,
   ENC_KRB_PRIV_PART_TIMESTAMP_TAG_STATE,
   ENC_KRB_PRIV_PART_TIMESTAMP_STATE,
   ENC_KRB_PRIV_PART_USEC_TAG_STATE,
   ENC_KRB_PRIV_PART_USEC_STATE,
   ENC_KRB_PRIV_PART_SEQ_NUMBER_TAG_STATE,
   ENC_KRB_PRIV_PART_SEQ_NUMBER_STATE,
   ENC_KRB_PRIV_PART_SENDER_ADDRESS_TAG_STATE,
   ENC_KRB_PRIV_PART_RECIPIENT_ADDRESS_TAG_STATE,
   LAST_ENC_KRB_PRIV_PART_STATE;

   public String getGrammarName(int grammar) {
      return "ENC_KRB_PRIV_PART_GRAMMAR";
   }

   public String getGrammarName(Grammar<EncKrbPrivPartContainer> grammar) {
      return grammar instanceof EncKrbPrivPartGrammar ? "ENC_KRB_PRIV_PART_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ENC_KRB_PRIV_PART_STATE.ordinal() ? "LAST_ENC_KRB_PRIV_PART_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ENC_KRB_PRIV_PART_STATE;
   }

   public EncKrbPrivPartStatesEnum getStartState() {
      return START_STATE;
   }
}
