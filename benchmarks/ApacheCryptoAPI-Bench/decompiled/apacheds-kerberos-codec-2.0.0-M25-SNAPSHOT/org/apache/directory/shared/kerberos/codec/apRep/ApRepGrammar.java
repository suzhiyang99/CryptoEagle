package org.apache.directory.shared.kerberos.codec.apRep;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.apRep.actions.ApRepInit;
import org.apache.directory.shared.kerberos.codec.apRep.actions.CheckMsgType;
import org.apache.directory.shared.kerberos.codec.apRep.actions.StoreEncPart;
import org.apache.directory.shared.kerberos.codec.apRep.actions.StorePvno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApRepGrammar extends AbstractGrammar<ApRepContainer> {
   static final Logger LOG = LoggerFactory.getLogger(ApRepGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<ApRepContainer> instance;

   private ApRepGrammar() {
      this.setName(ApRepGrammar.class.getName());
      super.transitions = new GrammarTransition[ApRepStatesEnum.LAST_AP_REP_STATE.ordinal()][256];
      super.transitions[ApRepStatesEnum.START_STATE.ordinal()][111] = new GrammarTransition(ApRepStatesEnum.START_STATE, ApRepStatesEnum.AP_REP_STATE, 111, new ApRepInit());
      super.transitions[ApRepStatesEnum.AP_REP_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(ApRepStatesEnum.AP_REP_STATE, ApRepStatesEnum.AP_REP_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[ApRepStatesEnum.AP_REP_SEQ_STATE.ordinal()][160] = new GrammarTransition(ApRepStatesEnum.AP_REP_SEQ_STATE, ApRepStatesEnum.AP_REP_PVNO_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[ApRepStatesEnum.AP_REP_PVNO_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(ApRepStatesEnum.AP_REP_PVNO_TAG_STATE, ApRepStatesEnum.AP_REP_PVNO_STATE, UniversalTag.INTEGER, new StorePvno());
      super.transitions[ApRepStatesEnum.AP_REP_PVNO_STATE.ordinal()][161] = new GrammarTransition(ApRepStatesEnum.AP_REP_PVNO_STATE, ApRepStatesEnum.AP_REP_MSG_TYPE_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[ApRepStatesEnum.AP_REP_MSG_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(ApRepStatesEnum.AP_REP_MSG_TYPE_TAG_STATE, ApRepStatesEnum.AP_REP_MSG_TYPE_STATE, UniversalTag.INTEGER, new CheckMsgType());
      super.transitions[ApRepStatesEnum.AP_REP_MSG_TYPE_STATE.ordinal()][162] = new GrammarTransition(ApRepStatesEnum.AP_REP_MSG_TYPE_STATE, ApRepStatesEnum.AP_REP_ENC_PART_STATE, 162, new StoreEncPart());
   }

   public static Grammar<ApRepContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new ApRepGrammar();
   }
}
