package org.apache.directory.shared.kerberos.codec.ticket;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.ticket.actions.StoreEncPart;
import org.apache.directory.shared.kerberos.codec.ticket.actions.StoreRealm;
import org.apache.directory.shared.kerberos.codec.ticket.actions.StoreSName;
import org.apache.directory.shared.kerberos.codec.ticket.actions.StoreTktVno;
import org.apache.directory.shared.kerberos.codec.ticket.actions.TicketInit;

public final class TicketGrammar extends AbstractGrammar<TicketContainer> {
   private static Grammar<TicketContainer> instance = new TicketGrammar();

   private TicketGrammar() {
      this.setName(TicketGrammar.class.getName());
      super.transitions = new GrammarTransition[TicketStatesEnum.LAST_TICKET_STATE.ordinal()][256];
      super.transitions[TicketStatesEnum.START_STATE.ordinal()][97] = new GrammarTransition(TicketStatesEnum.START_STATE, TicketStatesEnum.TICKET_STATE, 97, new TicketInit());
      super.transitions[TicketStatesEnum.TICKET_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(TicketStatesEnum.TICKET_STATE, TicketStatesEnum.TICKET_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[TicketStatesEnum.TICKET_SEQ_STATE.ordinal()][160] = new GrammarTransition(TicketStatesEnum.TICKET_SEQ_STATE, TicketStatesEnum.TICKET_VNO_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[TicketStatesEnum.TICKET_VNO_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(TicketStatesEnum.TICKET_VNO_TAG_STATE, TicketStatesEnum.TICKET_VNO_STATE, UniversalTag.INTEGER, new StoreTktVno());
      super.transitions[TicketStatesEnum.TICKET_VNO_STATE.ordinal()][161] = new GrammarTransition(TicketStatesEnum.TICKET_VNO_STATE, TicketStatesEnum.TICKET_REALM_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[TicketStatesEnum.TICKET_REALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(TicketStatesEnum.TICKET_REALM_TAG_STATE, TicketStatesEnum.TICKET_REALM_STATE, UniversalTag.GENERAL_STRING, new StoreRealm());
      super.transitions[TicketStatesEnum.TICKET_REALM_STATE.ordinal()][162] = new GrammarTransition(TicketStatesEnum.TICKET_REALM_STATE, TicketStatesEnum.TICKET_SNAME_TAG_STATE, 162, new StoreSName());
      super.transitions[TicketStatesEnum.TICKET_SNAME_TAG_STATE.ordinal()][163] = new GrammarTransition(TicketStatesEnum.TICKET_SNAME_TAG_STATE, TicketStatesEnum.TICKET_ENC_PART_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[TicketStatesEnum.TICKET_SNAME_TAG_STATE.ordinal()][163] = new GrammarTransition(TicketStatesEnum.TICKET_SNAME_TAG_STATE, TicketStatesEnum.TICKET_ENC_PART_TAG_STATE, 163, new StoreEncPart());
   }

   public static Grammar<TicketContainer> getInstance() {
      return instance;
   }
}
