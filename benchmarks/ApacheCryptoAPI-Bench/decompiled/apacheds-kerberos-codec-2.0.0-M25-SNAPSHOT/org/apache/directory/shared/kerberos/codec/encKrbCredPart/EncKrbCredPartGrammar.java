package org.apache.directory.shared.kerberos.codec.encKrbCredPart;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions.EncKrbCredPartInit;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions.StoreNonce;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions.StoreRecipientAddress;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions.StoreSenderAddress;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions.StoreTicketInfo;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions.StoreTimestamp;
import org.apache.directory.shared.kerberos.codec.encKrbCredPart.actions.StoreUsec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EncKrbCredPartGrammar extends AbstractGrammar<EncKrbCredPartContainer> {
   static final Logger LOG = LoggerFactory.getLogger(EncKrbCredPartGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<EncKrbCredPartContainer> instance;

   private EncKrbCredPartGrammar() {
      this.setName(EncKrbCredPartGrammar.class.getName());
      super.transitions = new GrammarTransition[EncKrbCredPartStatesEnum.LAST_ENC_KRB_CRED_PART_STATE.ordinal()][256];
      super.transitions[EncKrbCredPartStatesEnum.START_STATE.ordinal()][125] = new GrammarTransition(EncKrbCredPartStatesEnum.START_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TAG_STATE, 125, new EncKrbCredPartInit());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TAG_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_SEQ_TAG_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_SEQ_TAG_STATE.ordinal()][160] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_SEQ_TAG_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_TAG_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE, UniversalTag.SEQUENCE, new StoreTicketInfo());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE.ordinal()][161] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_TAG_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_STATE, UniversalTag.INTEGER, new StoreNonce());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_STATE.ordinal()][162] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TIMESTAMP_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TIMESTAMP_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TIMESTAMP_TAG_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TIMESTAMP_STATE, UniversalTag.GENERALIZED_TIME, new StoreTimestamp());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TIMESTAMP_STATE.ordinal()][163] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TIMESTAMP_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_USEC_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_USEC_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_USEC_TAG_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_USEC_STATE, UniversalTag.INTEGER, new StoreUsec());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_USEC_STATE.ordinal()][164] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_USEC_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_SENDER_ADDRESS_TAG_STATE, 164, new StoreSenderAddress());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_SENDER_ADDRESS_TAG_STATE.ordinal()][165] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_SENDER_ADDRESS_TAG_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_RECIPIENT_ADDRESS_TAG_STATE, 164, new StoreRecipientAddress());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE.ordinal()][162] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TIMESTAMP_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE.ordinal()][163] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_USEC_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE.ordinal()][164] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_SENDER_ADDRESS_TAG_STATE, 164, new StoreSenderAddress());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE.ordinal()][165] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TICKET_INFO_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_RECIPIENT_ADDRESS_TAG_STATE, 165, new StoreRecipientAddress());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_STATE.ordinal()][163] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_USEC_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_STATE.ordinal()][164] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_SENDER_ADDRESS_TAG_STATE, 164, new StoreSenderAddress());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_STATE.ordinal()][165] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_NONCE_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_RECIPIENT_ADDRESS_TAG_STATE, 165, new StoreRecipientAddress());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TIMESTAMP_STATE.ordinal()][164] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TIMESTAMP_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_SENDER_ADDRESS_TAG_STATE, 164, new StoreSenderAddress());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TIMESTAMP_STATE.ordinal()][165] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_TIMESTAMP_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_RECIPIENT_ADDRESS_TAG_STATE, 165, new StoreRecipientAddress());
      super.transitions[EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_USEC_STATE.ordinal()][165] = new GrammarTransition(EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_USEC_STATE, EncKrbCredPartStatesEnum.ENC_KRB_CRED_PART_RECIPIENT_ADDRESS_TAG_STATE, 165, new StoreRecipientAddress());
   }

   public static Grammar<EncKrbCredPartContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new EncKrbCredPartGrammar();
   }
}
