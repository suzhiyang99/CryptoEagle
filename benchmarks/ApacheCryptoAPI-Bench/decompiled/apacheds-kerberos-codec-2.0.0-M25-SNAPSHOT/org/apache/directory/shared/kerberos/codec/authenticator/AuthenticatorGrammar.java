package org.apache.directory.shared.kerberos.codec.authenticator;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.authenticator.actions.AuthenticatorInit;
import org.apache.directory.shared.kerberos.codec.authenticator.actions.StoreAuthenticatorVno;
import org.apache.directory.shared.kerberos.codec.authenticator.actions.StoreAuthorizationData;
import org.apache.directory.shared.kerberos.codec.authenticator.actions.StoreCName;
import org.apache.directory.shared.kerberos.codec.authenticator.actions.StoreCRealm;
import org.apache.directory.shared.kerberos.codec.authenticator.actions.StoreCTime;
import org.apache.directory.shared.kerberos.codec.authenticator.actions.StoreChecksum;
import org.apache.directory.shared.kerberos.codec.authenticator.actions.StoreCusec;
import org.apache.directory.shared.kerberos.codec.authenticator.actions.StoreSeqNumber;
import org.apache.directory.shared.kerberos.codec.authenticator.actions.StoreSubKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthenticatorGrammar extends AbstractGrammar<AuthenticatorContainer> {
   static final Logger LOG = LoggerFactory.getLogger(AuthenticatorGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<AuthenticatorContainer> instance;

   private AuthenticatorGrammar() {
      this.setName(AuthenticatorGrammar.class.getName());
      super.transitions = new GrammarTransition[AuthenticatorStatesEnum.LAST_AUTHENTICATOR_STATE.ordinal()][256];
      super.transitions[AuthenticatorStatesEnum.START_STATE.ordinal()][98] = new GrammarTransition(AuthenticatorStatesEnum.START_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_STATE, 98, new AuthenticatorInit());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_SEQ_STATE.ordinal()][160] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_SEQ_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_AUTHENTICATOR_VNO_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_AUTHENTICATOR_VNO_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_AUTHENTICATOR_VNO_TAG_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_AUTHENTICATOR_VNO_STATE, UniversalTag.INTEGER, new StoreAuthenticatorVno());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_AUTHENTICATOR_VNO_STATE.ordinal()][161] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_AUTHENTICATOR_VNO_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_CREALM_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_CREALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_CREALM_TAG_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_CREALM_STATE, UniversalTag.GENERAL_STRING, new StoreCRealm());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_CREALM_STATE.ordinal()][162] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_CREALM_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_CNAME_STATE, 162, new StoreCName());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_CNAME_STATE.ordinal()][163] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_CNAME_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_CKSUM_STATE, 163, new StoreChecksum());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_CNAME_STATE.ordinal()][164] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_CNAME_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_CUSEC_TAG_STATE, 164, new CheckNotNullLength());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_CKSUM_STATE.ordinal()][164] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_CKSUM_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_CUSEC_TAG_STATE, 164, new CheckNotNullLength());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_CUSEC_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_CUSEC_TAG_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_CUSEC_STATE, UniversalTag.INTEGER, new StoreCusec());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_CUSEC_STATE.ordinal()][165] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_CUSEC_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_CTIME_TAG_STATE, 165, new CheckNotNullLength());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_CTIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_CTIME_TAG_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_CTIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreCTime());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_CTIME_STATE.ordinal()][166] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_CTIME_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_SUBKEY_STATE, 166, new StoreSubKey());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_CTIME_STATE.ordinal()][167] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_CTIME_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_SEQ_NUMBER_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_CTIME_STATE.ordinal()][168] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_CTIME_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_AUTHORIZATION_DATA_STATE, 168, new StoreAuthorizationData());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_SUBKEY_STATE.ordinal()][167] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_SUBKEY_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_SEQ_NUMBER_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_SUBKEY_STATE.ordinal()][168] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_SUBKEY_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_AUTHORIZATION_DATA_STATE, 168, new StoreAuthorizationData());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_SEQ_NUMBER_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_SEQ_NUMBER_TAG_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_SEQ_NUMBER_STATE, UniversalTag.INTEGER, new StoreSeqNumber());
      super.transitions[AuthenticatorStatesEnum.AUTHENTICATOR_SEQ_NUMBER_STATE.ordinal()][168] = new GrammarTransition(AuthenticatorStatesEnum.AUTHENTICATOR_SEQ_NUMBER_STATE, AuthenticatorStatesEnum.AUTHENTICATOR_AUTHORIZATION_DATA_STATE, 168, new StoreAuthorizationData());
   }

   public static Grammar<AuthenticatorContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new AuthenticatorGrammar();
   }
}
