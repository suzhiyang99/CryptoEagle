package org.apache.directory.shared.kerberos.codec.encKrbPrivPart;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions.EncKrbPrivPartInit;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions.StoreRecipientAddress;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions.StoreSenderAddress;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions.StoreSeqNumber;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions.StoreTimestamp;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions.StoreUsec;
import org.apache.directory.shared.kerberos.codec.encKrbPrivPart.actions.StoreUserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EncKrbPrivPartGrammar extends AbstractGrammar<EncKrbPrivPartContainer> {
   static final Logger LOG = LoggerFactory.getLogger(EncKrbPrivPartGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<EncKrbPrivPartContainer> instance;

   private EncKrbPrivPartGrammar() {
      this.setName(EncKrbPrivPartGrammar.class.getName());
      super.transitions = new GrammarTransition[EncKrbPrivPartStatesEnum.LAST_ENC_KRB_PRIV_PART_STATE.ordinal()][256];
      super.transitions[EncKrbPrivPartStatesEnum.START_STATE.ordinal()][124] = new GrammarTransition(EncKrbPrivPartStatesEnum.START_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TAG_STATE, 124, new EncKrbPrivPartInit());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TAG_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SEQ_TAG_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SEQ_TAG_STATE.ordinal()][160] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SEQ_TAG_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_TAG_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_STATE, UniversalTag.OCTET_STRING, new StoreUserData());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_STATE.ordinal()][161] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TIMESTAMP_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TIMESTAMP_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TIMESTAMP_TAG_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TIMESTAMP_STATE, UniversalTag.GENERALIZED_TIME, new StoreTimestamp());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TIMESTAMP_STATE.ordinal()][162] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TIMESTAMP_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USEC_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USEC_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USEC_TAG_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USEC_STATE, UniversalTag.INTEGER, new StoreUsec());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USEC_STATE.ordinal()][163] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USEC_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SEQ_NUMBER_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SEQ_NUMBER_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SEQ_NUMBER_TAG_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SEQ_NUMBER_STATE, UniversalTag.INTEGER, new StoreSeqNumber());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SEQ_NUMBER_STATE.ordinal()][164] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SEQ_NUMBER_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SENDER_ADDRESS_TAG_STATE, 164, new StoreSenderAddress());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SENDER_ADDRESS_TAG_STATE.ordinal()][165] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SENDER_ADDRESS_TAG_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_RECIPIENT_ADDRESS_TAG_STATE, 165, new StoreRecipientAddress());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_STATE.ordinal()][162] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USEC_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_STATE.ordinal()][163] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SEQ_NUMBER_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_STATE.ordinal()][164] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USER_DATA_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SENDER_ADDRESS_TAG_STATE, 164, new StoreSenderAddress());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TIMESTAMP_STATE.ordinal()][163] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TIMESTAMP_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SEQ_NUMBER_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TIMESTAMP_STATE.ordinal()][164] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_TIMESTAMP_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SENDER_ADDRESS_TAG_STATE, 164, new StoreSenderAddress());
      super.transitions[EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USEC_STATE.ordinal()][164] = new GrammarTransition(EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_USEC_STATE, EncKrbPrivPartStatesEnum.ENC_KRB_PRIV_PART_SENDER_ADDRESS_TAG_STATE, 164, new StoreSenderAddress());
   }

   public static Grammar<EncKrbPrivPartContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new EncKrbPrivPartGrammar();
   }
}
