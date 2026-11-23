package org.apache.directory.shared.kerberos.codec.ticket;

import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.States;

public enum TicketStatesEnum implements States {
   START_STATE,
   TICKET_STATE,
   TICKET_SEQ_STATE,
   TICKET_VNO_TAG_STATE,
   TICKET_VNO_STATE,
   TICKET_REALM_TAG_STATE,
   TICKET_REALM_STATE,
   TICKET_SNAME_TAG_STATE,
   TICKET_ENC_PART_TAG_STATE,
   LAST_TICKET_STATE;

   public String getGrammarName(int grammar) {
      return "TICKET_GRAMMAR";
   }

   public String getGrammarName(Grammar<TicketContainer> grammar) {
      return grammar instanceof TicketGrammar ? "TICKET_GRAMMAR" : "UNKNOWN GRAMMAR";
   }

   public String getState(int state) {
      return state == LAST_TICKET_STATE.ordinal() ? "TICKET_END_STATE" : this.name();
   }

   public boolean isEndState() {
      return this == LAST_TICKET_STATE;
   }

   public TicketStatesEnum getStartState() {
      return START_STATE;
   }
}
