package org.apache.directory.shared.kerberos.codec.encKrbCredPart;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum EncKrbCredPartStatesEnum implements States {
   START_STATE,
   ENC_KRB_CRED_PART_TAG_STATE,
   ENC_KRB_CRED_PART_SEQ_TAG_STATE,
   ENC_KRB_CRED_PART_TICKET_INFO_TAG_STATE,
   ENC_KRB_CRED_PART_TICKET_INFO_STATE,
   ENC_KRB_CRED_PART_NONCE_TAG_STATE,
   ENC_KRB_CRED_PART_NONCE_STATE,
   ENC_KRB_CRED_PART_TIMESTAMP_TAG_STATE,
   ENC_KRB_CRED_PART_TIMESTAMP_STATE,
   ENC_KRB_CRED_PART_USEC_TAG_STATE,
   ENC_KRB_CRED_PART_USEC_STATE,
   ENC_KRB_CRED_PART_SENDER_ADDRESS_TAG_STATE,
   ENC_KRB_CRED_PART_RECIPIENT_ADDRESS_TAG_STATE,
   LAST_ENC_KRB_CRED_PART_STATE;

   public String getGrammarName(int grammar) {
      return "ENC_KRB_CRED_PART_GRAMMAR";
   }

   public String getGrammarName(Grammar<EncKrbCredPartContainer> grammar) {
      return grammar instanceof EncKrbCredPartGrammar ? "ENC_KRB_CRED_PART_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ENC_KRB_CRED_PART_STATE.ordinal() ? "LAST_ENC_KRB_CRED_PART_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ENC_KRB_CRED_PART_STATE;
   }

   public EncKrbCredPartStatesEnum getStartState() {
      return START_STATE;
   }
}
