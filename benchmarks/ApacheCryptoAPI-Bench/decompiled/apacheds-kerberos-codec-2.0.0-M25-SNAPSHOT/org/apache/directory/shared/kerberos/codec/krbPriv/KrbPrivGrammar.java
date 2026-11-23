package org.apache.directory.shared.kerberos.codec.krbPriv;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.krbPriv.actions.CheckMsgType;
import org.apache.directory.shared.kerberos.codec.krbPriv.actions.KrbPrivInit;
import org.apache.directory.shared.kerberos.codec.krbPriv.actions.StoreEncPart;
import org.apache.directory.shared.kerberos.codec.krbPriv.actions.StorePvno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KrbPrivGrammar extends AbstractGrammar<KrbPrivContainer> {
   static final Logger LOG = LoggerFactory.getLogger(KrbPrivGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<KrbPrivContainer> instance;

   private KrbPrivGrammar() {
      this.setName(KrbPrivGrammar.class.getName());
      super.transitions = new GrammarTransition[KrbPrivStatesEnum.LAST_KRB_PRIV_STATE.ordinal()][256];
      super.transitions[KrbPrivStatesEnum.START_STATE.ordinal()][117] = new GrammarTransition(KrbPrivStatesEnum.START_STATE, KrbPrivStatesEnum.KRB_PRIV_TAG_STATE, 117, new KrbPrivInit());
      super.transitions[KrbPrivStatesEnum.KRB_PRIV_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KrbPrivStatesEnum.KRB_PRIV_TAG_STATE, KrbPrivStatesEnum.KRB_PRIV_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[KrbPrivStatesEnum.KRB_PRIV_SEQ_STATE.ordinal()][160] = new GrammarTransition(KrbPrivStatesEnum.KRB_PRIV_SEQ_STATE, KrbPrivStatesEnum.KRB_PRIV_PVNO_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[KrbPrivStatesEnum.KRB_PRIV_PVNO_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbPrivStatesEnum.KRB_PRIV_PVNO_TAG_STATE, KrbPrivStatesEnum.KRB_PRIV_PVNO_STATE, UniversalTag.INTEGER, new StorePvno());
      super.transitions[KrbPrivStatesEnum.KRB_PRIV_PVNO_STATE.ordinal()][161] = new GrammarTransition(KrbPrivStatesEnum.KRB_PRIV_PVNO_STATE, KrbPrivStatesEnum.KRB_PRIV_MSGTYPE_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[KrbPrivStatesEnum.KRB_PRIV_MSGTYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbPrivStatesEnum.KRB_PRIV_MSGTYPE_TAG_STATE, KrbPrivStatesEnum.KRB_PRIV_MSGTYPE_STATE, UniversalTag.INTEGER, new CheckMsgType());
      super.transitions[KrbPrivStatesEnum.KRB_PRIV_MSGTYPE_STATE.ordinal()][163] = new GrammarTransition(KrbPrivStatesEnum.KRB_PRIV_MSGTYPE_STATE, KrbPrivStatesEnum.KRB_PRIV_EN_PART_TAG_STATE, 163, new StoreEncPart());
   }

   public static Grammar<KrbPrivContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new KrbPrivGrammar();
   }
}
