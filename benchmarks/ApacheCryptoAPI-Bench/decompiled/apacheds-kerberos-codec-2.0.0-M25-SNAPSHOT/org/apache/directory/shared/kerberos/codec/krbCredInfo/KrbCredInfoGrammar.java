package org.apache.directory.shared.kerberos.codec.krbCredInfo;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.KrbCredInfoInit;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.StoreAuthTime;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.StoreCaddr;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.StoreEndTime;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.StoreFlags;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.StoreKey;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.StorePName;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.StorePRealm;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.StoreRenewtill;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.StoreSName;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.StoreSRealm;
import org.apache.directory.shared.kerberos.codec.krbCredInfo.actions.StoreStartTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KrbCredInfoGrammar extends AbstractGrammar<KrbCredInfoContainer> {
   static final Logger LOG = LoggerFactory.getLogger(KrbCredInfoGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<KrbCredInfoContainer> instance;

   private KrbCredInfoGrammar() {
      this.setName(KrbCredInfoGrammar.class.getName());
      super.transitions = new GrammarTransition[KrbCredInfoStatesEnum.LAST_KRB_CRED_INFO_STATE.ordinal()][256];
      super.transitions[KrbCredInfoStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KrbCredInfoStatesEnum.START_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SEQ_TAG_STATE, UniversalTag.SEQUENCE, new KrbCredInfoInit());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_SEQ_TAG_STATE.ordinal()][160] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_SEQ_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE, 160, new StoreKey());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE.ordinal()][161] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE, UniversalTag.GENERAL_STRING, new StorePRealm());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE.ordinal()][162] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE, 162, new StorePName());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE.ordinal()][163] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_TAG_STATE.ordinal()][UniversalTag.BIT_STRING.getValue()] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE, UniversalTag.BIT_STRING, new StoreFlags());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE.ordinal()][164] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_TAG_STATE, 164, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreAuthTime());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE.ordinal()][165] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_TAG_STATE, 165, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreStartTime());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_STATE.ordinal()][166] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_TAG_STATE, 166, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreEndTime());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_STATE.ordinal()][167] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_STATE, UniversalTag.GENERALIZED_TIME, new StoreRenewtill());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_STATE.ordinal()][168] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_TAG_STATE, 168, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_STATE, UniversalTag.GENERAL_STRING, new StoreSRealm());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_STATE.ordinal()][169] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SNAME_TAG_STATE, 169, new StoreSName());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_SNAME_TAG_STATE.ordinal()][170] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_SNAME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_CADDR_TAG_STATE, 170, new StoreCaddr());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE.ordinal()][162] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE, 162, new StorePName());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE.ordinal()][163] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE.ordinal()][164] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_TAG_STATE, 164, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE.ordinal()][165] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_TAG_STATE, 165, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE.ordinal()][166] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_TAG_STATE, 166, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE.ordinal()][167] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE.ordinal()][168] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_TAG_STATE, 168, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE.ordinal()][169] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SNAME_TAG_STATE, 169, new StoreSName());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE.ordinal()][170] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_KEY_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_CADDR_TAG_STATE, 170, new StoreCaddr());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE.ordinal()][163] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE.ordinal()][164] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_TAG_STATE, 164, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE.ordinal()][165] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_TAG_STATE, 165, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE.ordinal()][166] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_TAG_STATE, 166, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE.ordinal()][167] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE.ordinal()][168] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_TAG_STATE, 168, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE.ordinal()][169] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SNAME_TAG_STATE, 169, new StoreSName());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE.ordinal()][170] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PREALM_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_CADDR_TAG_STATE, 170, new StoreCaddr());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE.ordinal()][164] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_TAG_STATE, 164, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE.ordinal()][165] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_TAG_STATE, 165, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE.ordinal()][166] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_TAG_STATE, 166, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE.ordinal()][167] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE.ordinal()][168] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_TAG_STATE, 168, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE.ordinal()][169] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SNAME_TAG_STATE, 169, new StoreSName());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE.ordinal()][170] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_PNAME_TAG_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_CADDR_TAG_STATE, 170, new StoreCaddr());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE.ordinal()][165] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_TAG_STATE, 165, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE.ordinal()][166] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_TAG_STATE, 166, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE.ordinal()][167] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE.ordinal()][168] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_TAG_STATE, 168, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE.ordinal()][169] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SNAME_TAG_STATE, 169, new StoreSName());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE.ordinal()][170] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_FLAGS_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_CADDR_TAG_STATE, 170, new StoreCaddr());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE.ordinal()][166] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_TAG_STATE, 166, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE.ordinal()][167] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE.ordinal()][168] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_TAG_STATE, 168, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE.ordinal()][169] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SNAME_TAG_STATE, 169, new StoreSName());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE.ordinal()][170] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_AUTHTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_CADDR_TAG_STATE, 170, new StoreCaddr());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_STATE.ordinal()][167] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_TAG_STATE, 167, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_STATE.ordinal()][168] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_TAG_STATE, 168, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_STATE.ordinal()][169] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SNAME_TAG_STATE, 169, new StoreSName());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_STATE.ordinal()][170] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_STARTTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_CADDR_TAG_STATE, 170, new StoreCaddr());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_STATE.ordinal()][168] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_TAG_STATE, 168, new CheckNotNullLength());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_STATE.ordinal()][169] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SNAME_TAG_STATE, 169, new StoreSName());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_STATE.ordinal()][170] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_ENDTIME_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_CADDR_TAG_STATE, 170, new StoreCaddr());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_STATE.ordinal()][169] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_SNAME_TAG_STATE, 169, new StoreSName());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_STATE.ordinal()][170] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_RENEWTILL_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_CADDR_TAG_STATE, 170, new StoreCaddr());
      super.transitions[KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_STATE.ordinal()][170] = new GrammarTransition(KrbCredInfoStatesEnum.KRB_CRED_INFO_SREALM_STATE, KrbCredInfoStatesEnum.KRB_CRED_INFO_CADDR_TAG_STATE, 170, new StoreCaddr());
   }

   public static Grammar<KrbCredInfoContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new KrbCredInfoGrammar();
   }
}
