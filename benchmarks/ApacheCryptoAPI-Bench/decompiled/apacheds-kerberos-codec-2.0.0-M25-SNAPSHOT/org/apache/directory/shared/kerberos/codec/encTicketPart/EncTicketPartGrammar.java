package org.apache.directory.shared.kerberos.codec.encTicketPart;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.EncTicketPartInit;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.StoreAuthTime;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.StoreAuthorizationData;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.StoreCName;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.StoreCRealm;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.StoreCaddr;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.StoreEndTime;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.StoreFlags;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.StoreKey;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.StoreRenewtill;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.StoreStartTime;
import org.apache.directory.shared.kerberos.codec.encTicketPart.actions.StoreTransited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EncTicketPartGrammar extends AbstractGrammar<EncTicketPartContainer> {
   static final Logger LOG = LoggerFactory.getLogger(EncTicketPartGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<EncTicketPartContainer> instance;

   private EncTicketPartGrammar() {
      this.setName(EncTicketPartGrammar.class.getName());
      super.transitions = new GrammarTransition[EncTicketPartStatesEnum.LAST_ENC_TICKET_PART_STATE.ordinal()][256];
      super.transitions[EncTicketPartStatesEnum.START_STATE.ordinal()][99] = new GrammarTransition(EncTicketPartStatesEnum.START_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_TAG_STATE, 99, new EncTicketPartInit());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_TAG_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_SEQ_STATE.ordinal()][160] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_SEQ_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_FLAGS_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_FLAGS_TAG_STATE.ordinal()][UniversalTag.BIT_STRING.getValue()] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_FLAGS_TAG_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_FLAGS_STATE, UniversalTag.BIT_STRING, new StoreFlags());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_FLAGS_STATE.ordinal()][161] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_FLAGS_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_KEY_TAG_STATE, 161, new StoreKey());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_KEY_TAG_STATE.ordinal()][162] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_KEY_TAG_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_CREALM_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_CREALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_CREALM_TAG_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_CREALM_STATE, UniversalTag.GENERAL_STRING, new StoreCRealm());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_CREALM_STATE.ordinal()][163] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_CREALM_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_CNAME_TAG_STATE, 163, new StoreCName());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_CNAME_TAG_STATE.ordinal()][164] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_CNAME_TAG_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_TRANSITED_TAG_STATE, 164, new StoreTransited());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_TRANSITED_TAG_STATE.ordinal()][165] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_TRANSITED_TAG_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_AUTHTIME_TAG_STATE, 165, new CheckNotNullLength());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_AUTHTIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_AUTHTIME_TAG_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_AUTHTIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreAuthTime());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_AUTHTIME_STATE.ordinal()][166] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_AUTHTIME_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_STARTTIME_TAG_STATE, 166, new CheckNotNullLength());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_STARTTIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_STARTTIME_TAG_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_STARTTIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreStartTime());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_STARTTIME_STATE.ordinal()][167] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_STARTTIME_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_ENDTIME_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_ENDTIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_ENDTIME_TAG_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_ENDTIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreEndTime());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_ENDTIME_STATE.ordinal()][168] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_ENDTIME_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_RENEWTILL_TAG_STATE, 168, new CheckNotNullLength());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_RENEWTILL_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_RENEWTILL_TAG_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_RENEWTILL_STATE, UniversalTag.GENERALIZED_TIME, new StoreRenewtill());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_RENEWTILL_STATE.ordinal()][169] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_RENEWTILL_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_CADDR_TAG_STATE, 169, new StoreCaddr());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_CADDR_TAG_STATE.ordinal()][170] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_CADDR_TAG_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_AUTHZ_DATA_TAG_STATE, 170, new StoreAuthorizationData());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_AUTHTIME_STATE.ordinal()][167] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_AUTHTIME_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_ENDTIME_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_ENDTIME_STATE.ordinal()][169] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_ENDTIME_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_CADDR_TAG_STATE, 169, new StoreCaddr());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_ENDTIME_STATE.ordinal()][170] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_ENDTIME_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_AUTHZ_DATA_TAG_STATE, 170, new StoreAuthorizationData());
      super.transitions[EncTicketPartStatesEnum.ENC_TICKET_PART_RENEWTILL_STATE.ordinal()][170] = new GrammarTransition(EncTicketPartStatesEnum.ENC_TICKET_PART_RENEWTILL_STATE, EncTicketPartStatesEnum.ENC_TICKET_PART_AUTHZ_DATA_TAG_STATE, 170, new StoreAuthorizationData());
   }

   public static Grammar<EncTicketPartContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new EncTicketPartGrammar();
   }
}
