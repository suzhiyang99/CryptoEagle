package org.apache.directory.shared.kerberos.codec.krbCred;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.krbCred.actions.CheckMsgType;
import org.apache.directory.shared.kerberos.codec.krbCred.actions.KrbCredInit;
import org.apache.directory.shared.kerberos.codec.krbCred.actions.StoreEncPart;
import org.apache.directory.shared.kerberos.codec.krbCred.actions.StorePvno;
import org.apache.directory.shared.kerberos.codec.krbCred.actions.StoreTickets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KrbCredGrammar extends AbstractGrammar<KrbCredContainer> {
   static final Logger LOG = LoggerFactory.getLogger(KrbCredGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<KrbCredContainer> instance;

   private KrbCredGrammar() {
      this.setName(KrbCredGrammar.class.getName());
      super.transitions = new GrammarTransition[KrbCredStatesEnum.LAST_KRB_CRED_STATE.ordinal()][256];
      super.transitions[KrbCredStatesEnum.START_STATE.ordinal()][118] = new GrammarTransition(KrbCredStatesEnum.START_STATE, KrbCredStatesEnum.KRB_CRED_TAG_STATE, 118, new KrbCredInit());
      super.transitions[KrbCredStatesEnum.KRB_CRED_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KrbCredStatesEnum.KRB_CRED_TAG_STATE, KrbCredStatesEnum.KRB_CRED_SEQ_TAG_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[KrbCredStatesEnum.KRB_CRED_SEQ_TAG_STATE.ordinal()][160] = new GrammarTransition(KrbCredStatesEnum.KRB_CRED_SEQ_TAG_STATE, KrbCredStatesEnum.KRB_CRED_PVNO_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[KrbCredStatesEnum.KRB_CRED_PVNO_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbCredStatesEnum.KRB_CRED_PVNO_TAG_STATE, KrbCredStatesEnum.KRB_CRED_PVNO_STATE, UniversalTag.INTEGER, new StorePvno());
      super.transitions[KrbCredStatesEnum.KRB_CRED_PVNO_STATE.ordinal()][161] = new GrammarTransition(KrbCredStatesEnum.KRB_CRED_PVNO_STATE, KrbCredStatesEnum.KRB_CRED_MSGTYPE_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[KrbCredStatesEnum.KRB_CRED_MSGTYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KrbCredStatesEnum.KRB_CRED_MSGTYPE_TAG_STATE, KrbCredStatesEnum.KRB_CRED_MSGTYPE_STATE, UniversalTag.INTEGER, new CheckMsgType());
      super.transitions[KrbCredStatesEnum.KRB_CRED_MSGTYPE_STATE.ordinal()][162] = new GrammarTransition(KrbCredStatesEnum.KRB_CRED_MSGTYPE_STATE, KrbCredStatesEnum.KRB_CRED_TICKETS_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[KrbCredStatesEnum.KRB_CRED_TICKETS_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KrbCredStatesEnum.KRB_CRED_TICKETS_TAG_STATE, KrbCredStatesEnum.KRB_CRED_TICKETS_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[KrbCredStatesEnum.KRB_CRED_TICKETS_STATE.ordinal()][97] = new GrammarTransition(KrbCredStatesEnum.KRB_CRED_TICKETS_STATE, KrbCredStatesEnum.KRB_CRED_TICKETS_STATE, 97, new StoreTickets());
      super.transitions[KrbCredStatesEnum.KRB_CRED_TICKETS_STATE.ordinal()][163] = new GrammarTransition(KrbCredStatesEnum.KRB_CRED_TICKETS_STATE, KrbCredStatesEnum.KRB_CRED_ENCPART_TAG_STATE, 163, new StoreEncPart());
   }

   public static Grammar<KrbCredContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new KrbCredGrammar();
   }
}
