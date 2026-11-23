package org.apache.directory.shared.kerberos.codec.krbSafe;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.krbSafe.actions.CheckMsgType;
import org.apache.directory.shared.kerberos.codec.krbSafe.actions.KrbSafeInit;
import org.apache.directory.shared.kerberos.codec.krbSafe.actions.StoreChecksum;
import org.apache.directory.shared.kerberos.codec.krbSafe.actions.StorePvno;
import org.apache.directory.shared.kerberos.codec.krbSafe.actions.StoreSafeBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KrbSafeGrammar extends AbstractGrammar<KrbSafeContainer> {
   static final Logger LOG = LoggerFactory.getLogger(KrbSafeGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<KrbSafeContainer> instance;

   private KrbSafeGrammar() {
      this.setName(KrbSafeGrammar.class.getName());
      super.transitions = new GrammarTransition[KrbSafeStatesEnum.LAST_KRB_SAFE_STATE.ordinal()][256];
      super.transitions[KrbSafeStatesEnum.START_STATE.ordinal()][116] = new GrammarTransition(KrbSafeStatesEnum.START_STATE, KrbSafeStatesEnum.KRB_SAFE_TAG_STATE, 116, new KrbSafeInit());
      super.transitions[KrbSafeStatesEnum.KRB_SAFE_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KrbSafeStatesEnum.KRB_SAFE_TAG_STATE, KrbSafeStatesEnum.KRB_SAFE_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[KrbSafeStatesEnum.KRB_SAFE_SEQ_STATE.ordinal()][160] = new GrammarTransition(KrbSafeStatesEnum.KRB_SAFE_SEQ_STATE, KrbSafeStatesEnum.KRB_SAFE_PVNO_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[KrbSafeStatesEnum.KRB_SAFE_PVNO_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbSafeStatesEnum.KRB_SAFE_PVNO_TAG_STATE, KrbSafeStatesEnum.KRB_SAFE_PVNO_STATE, UniversalTag.INTEGER, new StorePvno());
      super.transitions[KrbSafeStatesEnum.KRB_SAFE_PVNO_STATE.ordinal()][161] = new GrammarTransition(KrbSafeStatesEnum.KRB_SAFE_PVNO_STATE, KrbSafeStatesEnum.KRB_SAFE_MSGTYPE_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[KrbSafeStatesEnum.KRB_SAFE_MSGTYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbSafeStatesEnum.KRB_SAFE_MSGTYPE_TAG_STATE, KrbSafeStatesEnum.KRB_SAFE_MSGTYPE_STATE, UniversalTag.INTEGER, new CheckMsgType());
      super.transitions[KrbSafeStatesEnum.KRB_SAFE_MSGTYPE_STATE.ordinal()][162] = new GrammarTransition(KrbSafeStatesEnum.KRB_SAFE_MSGTYPE_STATE, KrbSafeStatesEnum.KRB_SAFE_SAFE_BODY_TAG_STATE, 162, new StoreSafeBody());
      super.transitions[KrbSafeStatesEnum.KRB_SAFE_SAFE_BODY_TAG_STATE.ordinal()][163] = new GrammarTransition(KrbSafeStatesEnum.KRB_SAFE_SAFE_BODY_TAG_STATE, KrbSafeStatesEnum.KRB_SAFE_CKSUM_TAG_STATE, 163, new StoreChecksum());
   }

   public static Grammar<KrbSafeContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new KrbSafeGrammar();
   }
}
