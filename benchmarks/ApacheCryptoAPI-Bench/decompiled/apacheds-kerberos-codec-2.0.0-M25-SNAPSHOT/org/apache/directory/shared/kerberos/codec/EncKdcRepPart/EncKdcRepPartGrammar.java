package org.apache.directory.shared.kerberos.codec.EncKdcRepPart;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.EncKdcRepPartInit;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreAuthTime;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreCAddr;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreEndTime;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreFlags;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreKey;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreKeyExpiration;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreLastReq;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreNonce;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreRenewTill;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreSName;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreSRealm;
import org.apache.directory.shared.kerberos.codec.EncKdcRepPart.actions.StoreStartTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EncKdcRepPartGrammar extends AbstractGrammar<EncKdcRepPartContainer> {
   static final Logger LOG = LoggerFactory.getLogger(EncKdcRepPartGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<EncKdcRepPartContainer> instance;

   private EncKdcRepPartGrammar() {
      this.setName(EncKdcRepPartGrammar.class.getName());
      super.transitions = new GrammarTransition[EncKdcRepPartStatesEnum.LAST_ENC_KDC_REP_PART_STATE.ordinal()][256];
      super.transitions[EncKdcRepPartStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(EncKdcRepPartStatesEnum.START_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SEQ_TAG_STATE, UniversalTag.SEQUENCE, new EncKdcRepPartInit());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SEQ_TAG_STATE.ordinal()][160] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SEQ_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_KEY_TAG_STATE, 160, new StoreKey());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_KEY_TAG_STATE.ordinal()][161] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_KEY_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_LAST_REQ_TAG_STATE, 161, new StoreLastReq());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_LAST_REQ_TAG_STATE.ordinal()][162] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_LAST_REQ_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_NONCE_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_NONCE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_NONCE_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_NONCE_STATE, UniversalTag.INTEGER, new StoreNonce());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_NONCE_STATE.ordinal()][163] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_NONCE_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_KEY_EXPIRATION_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_NONCE_STATE.ordinal()][164] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_NONCE_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_FLAGS_TAG_STATE, 164, new CheckNotNullLength());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_KEY_EXPIRATION_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_KEY_EXPIRATION_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_KEY_EXPIRATION_STATE, UniversalTag.GENERALIZED_TIME, new StoreKeyExpiration());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_KEY_EXPIRATION_STATE.ordinal()][164] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_KEY_EXPIRATION_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_FLAGS_TAG_STATE, 164, new CheckNotNullLength());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_FLAGS_TAG_STATE.ordinal()][UniversalTag.BIT_STRING.getValue()] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_FLAGS_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_FLAGS_STATE, UniversalTag.BIT_STRING, new StoreFlags());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_FLAGS_STATE.ordinal()][165] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_FLAGS_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_AUTH_TIME_TAG_STATE, 165, new CheckNotNullLength());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_AUTH_TIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_AUTH_TIME_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_AUTH_TIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreAuthTime());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_AUTH_TIME_STATE.ordinal()][166] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_AUTH_TIME_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_START_TIME_TAG_STATE, 166, new CheckNotNullLength());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_AUTH_TIME_STATE.ordinal()][167] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_AUTH_TIME_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_END_TIME_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_START_TIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_START_TIME_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_START_TIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreStartTime());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_START_TIME_STATE.ordinal()][167] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_START_TIME_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_END_TIME_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_END_TIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_END_TIME_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_END_TIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreEndTime());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_END_TIME_STATE.ordinal()][168] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_END_TIME_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_RENEW_TILL_TAG_STATE, 168, new CheckNotNullLength());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_END_TIME_STATE.ordinal()][169] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_END_TIME_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SREALM_TAG_STATE, 169, new CheckNotNullLength());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_RENEW_TILL_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_RENEW_TILL_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_RENEW_TILL_STATE, UniversalTag.GENERALIZED_TIME, new StoreRenewTill());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_RENEW_TILL_STATE.ordinal()][169] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_RENEW_TILL_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SREALM_TAG_STATE, 169, new CheckNotNullLength());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SREALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SREALM_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SREALM_STATE, UniversalTag.GENERAL_STRING, new StoreSRealm());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SREALM_STATE.ordinal()][170] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SREALM_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SNAME_TAG_STATE, 170, new StoreSName());
      super.transitions[EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SNAME_TAG_STATE.ordinal()][171] = new GrammarTransition(EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_SNAME_TAG_STATE, EncKdcRepPartStatesEnum.ENC_KDC_REP_PART_CADDR_TAG_STATE, 171, new StoreCAddr());
   }

   public static Grammar<EncKdcRepPartContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new EncKdcRepPartGrammar();
   }
}
