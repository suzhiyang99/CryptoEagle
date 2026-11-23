package org.apache.directory.shared.kerberos.codec.encTicketPart;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum EncTicketPartStatesEnum implements States {
   START_STATE,
   ENC_TICKET_PART_TAG_STATE,
   ENC_TICKET_PART_SEQ_STATE,
   ENC_TICKET_PART_FLAGS_TAG_STATE,
   ENC_TICKET_PART_FLAGS_STATE,
   ENC_TICKET_PART_KEY_TAG_STATE,
   ENC_TICKET_PART_CREALM_TAG_STATE,
   ENC_TICKET_PART_CREALM_STATE,
   ENC_TICKET_PART_CNAME_TAG_STATE,
   ENC_TICKET_PART_TRANSITED_TAG_STATE,
   ENC_TICKET_PART_AUTHTIME_TAG_STATE,
   ENC_TICKET_PART_AUTHTIME_STATE,
   ENC_TICKET_PART_STARTTIME_TAG_STATE,
   ENC_TICKET_PART_STARTTIME_STATE,
   ENC_TICKET_PART_ENDTIME_TAG_STATE,
   ENC_TICKET_PART_ENDTIME_STATE,
   ENC_TICKET_PART_RENEWTILL_TAG_STATE,
   ENC_TICKET_PART_RENEWTILL_STATE,
   ENC_TICKET_PART_CADDR_TAG_STATE,
   ENC_TICKET_PART_AUTHZ_DATA_TAG_STATE,
   LAST_ENC_TICKET_PART_STATE;

   public String getGrammarName(int grammar) {
      return "ENC_TICKET_PART_GRAMMAR";
   }

   public String getGrammarName(Grammar<EncTicketPartContainer> grammar) {
      return grammar instanceof EncTicketPartGrammar ? "ENC_TICKET_PART_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_ENC_TICKET_PART_STATE.ordinal() ? "LAST_ENC_TICKET_PART_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_ENC_TICKET_PART_STATE;
   }

   public EncTicketPartStatesEnum getStartState() {
      return START_STATE;
   }
}
