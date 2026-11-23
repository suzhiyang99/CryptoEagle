package org.apache.directory.shared.kerberos.codec.authorizationData;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.authorizationData.actions.AuthorizationDataInit;
import org.apache.directory.shared.kerberos.codec.authorizationData.actions.StoreAdData;
import org.apache.directory.shared.kerberos.codec.authorizationData.actions.StoreAdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthorizationDataGrammar extends AbstractGrammar<AuthorizationDataContainer> {
   static final Logger LOG = LoggerFactory.getLogger(AuthorizationDataGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<AuthorizationDataContainer> instance;

   private AuthorizationDataGrammar() {
      this.setName(AuthorizationDataGrammar.class.getName());
      super.transitions = new GrammarTransition[AuthorizationDataStatesEnum.LAST_AUTHORIZATION_DATA_STATE.ordinal()][256];
      super.transitions[AuthorizationDataStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(AuthorizationDataStatesEnum.START_STATE, AuthorizationDataStatesEnum.AUTHORIZATION_DATA_SEQ_SEQ_STATE, UniversalTag.SEQUENCE, new AuthorizationDataInit());
      super.transitions[AuthorizationDataStatesEnum.AUTHORIZATION_DATA_SEQ_SEQ_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(AuthorizationDataStatesEnum.AUTHORIZATION_DATA_SEQ_SEQ_STATE, AuthorizationDataStatesEnum.AUTHORIZATION_DATA_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[AuthorizationDataStatesEnum.AUTHORIZATION_DATA_SEQ_STATE.ordinal()][160] = new GrammarTransition(AuthorizationDataStatesEnum.AUTHORIZATION_DATA_SEQ_STATE, AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADTYPE_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADTYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADTYPE_TAG_STATE, AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADTYPE_STATE, UniversalTag.INTEGER, new StoreAdType());
      super.transitions[AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADTYPE_STATE.ordinal()][161] = new GrammarTransition(AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADTYPE_STATE, AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADDATA_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADDATA_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADDATA_TAG_STATE, AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADDATA_STATE, UniversalTag.OCTET_STRING, new StoreAdData());
      super.transitions[AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADDATA_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(AuthorizationDataStatesEnum.AUTHORIZATION_DATA_ADDATA_STATE, AuthorizationDataStatesEnum.AUTHORIZATION_DATA_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
   }

   public static Grammar<AuthorizationDataContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new AuthorizationDataGrammar();
   }
}
