package org.apache.directory.shared.kerberos.codec.kdcReqBody;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.AddEType;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.AddTicket;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.ETypeSequence;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.KdcReqBodyInit;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.StoreAddresses;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.StoreCName;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.StoreEncAuthorizationData;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.StoreFrom;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.StoreKdcOptions;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.StoreNonce;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.StoreRTime;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.StoreRealm;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.StoreSName;
import org.apache.directory.shared.kerberos.codec.kdcReqBody.actions.StoreTill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KdcReqBodyGrammar extends AbstractGrammar<KdcReqBodyContainer> {
   static final Logger LOG = LoggerFactory.getLogger(KdcReqBodyGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<KdcReqBodyContainer> instance;

   private KdcReqBodyGrammar() {
      this.setName(KdcReqBodyGrammar.class.getName());
      super.transitions = new GrammarTransition[KdcReqBodyStatesEnum.LAST_KDC_REQ_BODY_STATE.ordinal()][256];
      super.transitions[KdcReqBodyStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KdcReqBodyStatesEnum.START_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_KDC_OPTIONS_TAG_STATE, UniversalTag.SEQUENCE, new KdcReqBodyInit());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_KDC_OPTIONS_TAG_STATE.ordinal()][160] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_KDC_OPTIONS_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_KDC_OPTIONS_STATE, 160, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_KDC_OPTIONS_STATE.ordinal()][UniversalTag.BIT_STRING.getValue()] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_KDC_OPTIONS_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_CNAME_OR_REALM_TAG_STATE, UniversalTag.BIT_STRING, new StoreKdcOptions());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_CNAME_OR_REALM_TAG_STATE.ordinal()][161] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_CNAME_OR_REALM_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_CNAME_STATE, 161, new StoreCName());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_CNAME_OR_REALM_TAG_STATE.ordinal()][162] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_CNAME_OR_REALM_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_REALM_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_CNAME_STATE.ordinal()][162] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_CNAME_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_REALM_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_REALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_REALM_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_OR_FROM_OR_TILL_TAG_STATE, UniversalTag.GENERAL_STRING, new StoreRealm());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_OR_FROM_OR_TILL_TAG_STATE.ordinal()][163] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_OR_FROM_OR_TILL_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_STATE, 163, new StoreSName());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_OR_FROM_OR_TILL_TAG_STATE.ordinal()][164] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_OR_FROM_OR_TILL_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_FROM_STATE, 164, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_OR_FROM_OR_TILL_TAG_STATE.ordinal()][165] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_OR_FROM_OR_TILL_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_TILL_STATE, 165, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_STATE.ordinal()][164] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_FROM_STATE, 164, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_STATE.ordinal()][165] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_SNAME_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_TILL_STATE, 165, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_FROM_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_FROM_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_TILL_TAG_STATE, UniversalTag.GENERALIZED_TIME, new StoreFrom());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_TILL_TAG_STATE.ordinal()][165] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_TILL_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_TILL_STATE, 165, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_TILL_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_TILL_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_RTIME_OR_NONCE_TAG_STATE, UniversalTag.GENERALIZED_TIME, new StoreTill());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_RTIME_OR_NONCE_TAG_STATE.ordinal()][166] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_RTIME_OR_NONCE_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_RTIME_STATE, 166, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_RTIME_OR_NONCE_TAG_STATE.ordinal()][167] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_RTIME_OR_NONCE_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_NONCE_STATE, 167, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_RTIME_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_RTIME_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_NONCE_TAG_STATE, UniversalTag.GENERALIZED_TIME, new StoreRTime());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_NONCE_TAG_STATE.ordinal()][167] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_NONCE_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_NONCE_STATE, 167, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_NONCE_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_NONCE_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_TAG_STATE, UniversalTag.INTEGER, new StoreNonce());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_TAG_STATE.ordinal()][168] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_SEQ_STATE, 168, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_SEQ_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_SEQ_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_STATE, UniversalTag.SEQUENCE, new ETypeSequence());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_STATE, UniversalTag.INTEGER, new AddEType());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_STATE.ordinal()][169] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDRESSES_STATE, 169, new StoreAddresses());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_STATE.ordinal()][170] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ENC_AUTH_DATA_STATE, 170, new StoreEncAuthorizationData());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_STATE.ordinal()][171] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ETYPE_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_TAG_STATE, 171, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDRESSES_STATE.ordinal()][170] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDRESSES_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ENC_AUTH_DATA_STATE, 170, new StoreEncAuthorizationData());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDRESSES_STATE.ordinal()][171] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDRESSES_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_TAG_STATE, 171, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ENC_AUTH_DATA_STATE.ordinal()][171] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ENC_AUTH_DATA_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_TAG_STATE, 171, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_TAG_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_SEQ_STATE.ordinal()][97] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_SEQ_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_STATE, 97, new AddTicket());
      super.transitions[KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_STATE.ordinal()][97] = new GrammarTransition(KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_STATE, KdcReqBodyStatesEnum.KDC_REQ_BODY_ADDITIONAL_TICKETS_STATE, 97, new AddTicket());
   }

   public static Grammar<KdcReqBodyContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new KdcReqBodyGrammar();
   }
}
