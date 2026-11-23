package org.apache.directory.shared.kerberos.codec.krbError;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.krbError.actions.CheckMsgType;
import org.apache.directory.shared.kerberos.codec.krbError.actions.KrbErrorInit;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StoreCName;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StoreCRealm;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StoreCTime;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StoreCusec;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StoreEData;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StoreEText;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StoreErrorCode;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StorePvno;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StoreRealm;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StoreSName;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StoreSTime;
import org.apache.directory.shared.kerberos.codec.krbError.actions.StoreSusec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KrbErrorGrammar extends AbstractGrammar<KrbErrorContainer> {
   static final Logger LOG = LoggerFactory.getLogger(KrbErrorGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<KrbErrorContainer> instance;

   private KrbErrorGrammar() {
      this.setName(KrbErrorGrammar.class.getName());
      super.transitions = new GrammarTransition[KrbErrorStatesEnum.LAST_KRB_ERR_STATE.ordinal()][256];
      super.transitions[KrbErrorStatesEnum.START_STATE.ordinal()][126] = new GrammarTransition(KrbErrorStatesEnum.START_STATE, KrbErrorStatesEnum.KRB_ERR_TAG, 126, new KrbErrorInit());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_TAG.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_TAG, KrbErrorStatesEnum.KRB_ERR_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_SEQ_STATE.ordinal()][160] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_SEQ_STATE, KrbErrorStatesEnum.KRB_ERR_PVNO_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_PVNO_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_PVNO_TAG_STATE, KrbErrorStatesEnum.KRB_ERR_PVNO_STATE, UniversalTag.INTEGER.getValue(), new StorePvno());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_PVNO_STATE.ordinal()][161] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_PVNO_STATE, KrbErrorStatesEnum.KRB_ERR_MSG_TYPE_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_MSG_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_MSG_TYPE_TAG_STATE, KrbErrorStatesEnum.KRB_ERR_MSG_TYPE_STATE, UniversalTag.INTEGER, new CheckMsgType());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_MSG_TYPE_STATE.ordinal()][162] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_MSG_TYPE_STATE, KrbErrorStatesEnum.KRB_ERR_CTIME_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_CTIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_CTIME_TAG_STATE, KrbErrorStatesEnum.KRB_ERR_CTIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreCTime());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_CTIME_STATE.ordinal()][163] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_CTIME_STATE, KrbErrorStatesEnum.KRB_ERR_CUSEC_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_CUSEC_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_CUSEC_TAG_STATE, KrbErrorStatesEnum.KRB_ERR_CUSEC_STATE, UniversalTag.INTEGER, new StoreCusec());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_CUSEC_STATE.ordinal()][164] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_CUSEC_STATE, KrbErrorStatesEnum.KRB_ERR_STIME_TAG_STATE, 164, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_STIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_STIME_TAG_STATE, KrbErrorStatesEnum.KRB_ERR_STIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreSTime());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_STIME_STATE.ordinal()][165] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_STIME_STATE, KrbErrorStatesEnum.KRB_ERR_SUSEC_TAG_STATE, 165, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_SUSEC_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_SUSEC_TAG_STATE, KrbErrorStatesEnum.KRB_ERR_SUSEC_STATE, UniversalTag.INTEGER, new StoreSusec());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_SUSEC_STATE.ordinal()][166] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_SUSEC_STATE, KrbErrorStatesEnum.KRB_ERR_ERROR_CODE_TAG_STATE, 166, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_ERROR_CODE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_ERROR_CODE_TAG_STATE, KrbErrorStatesEnum.KRB_ERR_ERROR_CODE_STATE, UniversalTag.INTEGER, new StoreErrorCode());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_ERROR_CODE_STATE.ordinal()][167] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_ERROR_CODE_STATE, KrbErrorStatesEnum.KRB_ERR_CREALM_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_CREALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_CREALM_TAG_STATE, KrbErrorStatesEnum.KRB_ERR_CREALM_STATE, UniversalTag.GENERAL_STRING, new StoreCRealm());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_CREALM_STATE.ordinal()][168] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_CREALM_STATE, KrbErrorStatesEnum.KRB_ERR_CNAME_STATE, 168, new StoreCName());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_CNAME_STATE.ordinal()][169] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_CNAME_STATE, KrbErrorStatesEnum.KRB_ERR_REALM_TAG_STATE, 169, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_REALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_REALM_TAG_STATE, KrbErrorStatesEnum.KRB_ERR_REALM_STATE, UniversalTag.GENERAL_STRING, new StoreRealm());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_REALM_STATE.ordinal()][170] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_REALM_STATE, KrbErrorStatesEnum.KRB_ERR_SNAME_STATE, 170, new StoreSName());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_SNAME_STATE.ordinal()][171] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_SNAME_STATE, KrbErrorStatesEnum.KRB_ERR_ETEXT_TAG_STATE, 171, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_ETEXT_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_ETEXT_TAG_STATE, KrbErrorStatesEnum.KRB_ERR_ETEXT_STATE, UniversalTag.GENERAL_STRING, new StoreEText());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_ETEXT_STATE.ordinal()][172] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_ETEXT_STATE, KrbErrorStatesEnum.KRB_ERR_EDATA_TAG_STATE, 172, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_EDATA_TAG_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_EDATA_TAG_STATE, KrbErrorStatesEnum.KRB_ERR_EDATA_STATE, UniversalTag.OCTET_STRING, new StoreEData());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_MSG_TYPE_STATE.ordinal()][163] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_MSG_TYPE_STATE, KrbErrorStatesEnum.KRB_ERR_CUSEC_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_MSG_TYPE_STATE.ordinal()][164] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_MSG_TYPE_STATE, KrbErrorStatesEnum.KRB_ERR_STIME_TAG_STATE, 164, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_CTIME_STATE.ordinal()][164] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_CTIME_STATE, KrbErrorStatesEnum.KRB_ERR_STIME_TAG_STATE, 164, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_ERROR_CODE_STATE.ordinal()][169] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_ERROR_CODE_STATE, KrbErrorStatesEnum.KRB_ERR_REALM_TAG_STATE, 169, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_ERROR_CODE_STATE.ordinal()][168] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_ERROR_CODE_STATE, KrbErrorStatesEnum.KRB_ERR_CNAME_STATE, 168, new StoreCName());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_CREALM_STATE.ordinal()][169] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_CREALM_STATE, KrbErrorStatesEnum.KRB_ERR_REALM_TAG_STATE, 169, new CheckNotNullLength());
      super.transitions[KrbErrorStatesEnum.KRB_ERR_SNAME_STATE.ordinal()][172] = new GrammarTransition(KrbErrorStatesEnum.KRB_ERR_SNAME_STATE, KrbErrorStatesEnum.KRB_ERR_EDATA_TAG_STATE, 172, new CheckNotNullLength());
   }

   public static Grammar<KrbErrorContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new KrbErrorGrammar();
   }
}
