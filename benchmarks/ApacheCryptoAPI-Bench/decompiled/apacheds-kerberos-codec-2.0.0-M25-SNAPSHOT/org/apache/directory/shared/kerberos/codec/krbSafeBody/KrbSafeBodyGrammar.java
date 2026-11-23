package org.apache.directory.shared.kerberos.codec.krbSafeBody;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.actions.KrbSafeBodyInit;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.actions.StoreRecipientAddress;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.actions.StoreSenderAddress;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.actions.StoreSeqNumber;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.actions.StoreTimestamp;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.actions.StoreUsec;
import org.apache.directory.shared.kerberos.codec.krbSafeBody.actions.StoreUserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KrbSafeBodyGrammar extends AbstractGrammar<KrbSafeBodyContainer> {
   static final Logger LOG = LoggerFactory.getLogger(KrbSafeBodyGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<KrbSafeBodyContainer> instance;

   private KrbSafeBodyGrammar() {
      this.setName(KrbSafeBodyGrammar.class.getName());
      super.transitions = new GrammarTransition[KrbSafeBodyStatesEnum.LAST_KRB_SAFE_BODY_STATE.ordinal()][256];
      super.transitions[KrbSafeBodyStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KrbSafeBodyStatesEnum.START_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SEQ_TAG_STATE, UniversalTag.SEQUENCE, new KrbSafeBodyInit());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SEQ_TAG_STATE.ordinal()][160] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SEQ_TAG_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_TAG_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_STATE, UniversalTag.OCTET_STRING, new StoreUserData());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_STATE.ordinal()][161] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_TIMESTAMP_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_TIMESTAMP_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_TIMESTAMP_TAG_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_TIMESTAMP_STATE, UniversalTag.GENERALIZED_TIME, new StoreTimestamp());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_TIMESTAMP_STATE.ordinal()][162] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_TIMESTAMP_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USEC_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USEC_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USEC_TAG_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USEC_STATE, UniversalTag.INTEGER, new StoreUsec());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USEC_STATE.ordinal()][163] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USEC_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SEQ_NUMBER_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SEQ_NUMBER_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SEQ_NUMBER_TAG_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SEQ_NUMBER_STATE, UniversalTag.INTEGER, new StoreSeqNumber());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SEQ_NUMBER_STATE.ordinal()][164] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SEQ_NUMBER_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SENDER_ADDRESS_TAG_STATE, 164, new StoreSenderAddress());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SENDER_ADDRESS_TAG_STATE.ordinal()][165] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SENDER_ADDRESS_TAG_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_RECIPIENT_ADDRESS_TAG_STATE, 165, new StoreRecipientAddress());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_STATE.ordinal()][162] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USEC_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_STATE.ordinal()][163] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SEQ_NUMBER_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_STATE.ordinal()][164] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USER_DATA_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SENDER_ADDRESS_TAG_STATE, 164, new StoreSenderAddress());
      super.transitions[KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USEC_STATE.ordinal()][164] = new GrammarTransition(KrbSafeBodyStatesEnum.KRB_SAFE_BODY_USEC_STATE, KrbSafeBodyStatesEnum.KRB_SAFE_BODY_SENDER_ADDRESS_TAG_STATE, 164, new StoreSenderAddress());
   }

   public static Grammar<KrbSafeBodyContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new KrbSafeBodyGrammar();
   }
}
