package org.apache.directory.shared.kerberos.codec.lastReq;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.lastReq.actions.LastReqInit;
import org.apache.directory.shared.kerberos.codec.lastReq.actions.StoreLrType;
import org.apache.directory.shared.kerberos.codec.lastReq.actions.StoreLrValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LastReqGrammar extends AbstractGrammar<LastReqContainer> {
   static final Logger LOG = LoggerFactory.getLogger(LastReqGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<LastReqContainer> instance;

   private LastReqGrammar() {
      this.setName(LastReqGrammar.class.getName());
      super.transitions = new GrammarTransition[LastReqStatesEnum.LAST_LAST_REQ_STATE.ordinal()][256];
      super.transitions[LastReqStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(LastReqStatesEnum.START_STATE, LastReqStatesEnum.LAST_REQ_SEQ_STATE, UniversalTag.SEQUENCE, new LastReqInit());
      super.transitions[LastReqStatesEnum.LAST_REQ_SEQ_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(LastReqStatesEnum.LAST_REQ_SEQ_STATE, LastReqStatesEnum.LAST_REQ_SEQ_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[LastReqStatesEnum.LAST_REQ_SEQ_SEQ_STATE.ordinal()][160] = new GrammarTransition(LastReqStatesEnum.LAST_REQ_SEQ_SEQ_STATE, LastReqStatesEnum.LAST_REQ_LR_TYPE_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[LastReqStatesEnum.LAST_REQ_LR_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(LastReqStatesEnum.LAST_REQ_LR_TYPE_TAG_STATE, LastReqStatesEnum.LAST_REQ_LR_TYPE_STATE, UniversalTag.INTEGER, new StoreLrType());
      super.transitions[LastReqStatesEnum.LAST_REQ_LR_TYPE_STATE.ordinal()][161] = new GrammarTransition(LastReqStatesEnum.LAST_REQ_LR_TYPE_STATE, LastReqStatesEnum.LAST_REQ_LR_VALUE_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[LastReqStatesEnum.LAST_REQ_LR_VALUE_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(LastReqStatesEnum.LAST_REQ_LR_VALUE_TAG_STATE, LastReqStatesEnum.LAST_REQ_LR_VALUE_STATE, UniversalTag.GENERALIZED_TIME, new StoreLrValue());
      super.transitions[LastReqStatesEnum.LAST_REQ_LR_VALUE_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(LastReqStatesEnum.LAST_REQ_LR_VALUE_STATE, LastReqStatesEnum.LAST_REQ_SEQ_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
   }

   public static Grammar<LastReqContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new LastReqGrammar();
   }
}
