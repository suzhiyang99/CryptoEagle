package org.apache.directory.shared.kerberos.codec.apReq;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.apReq.actions.ApReqInit;
import org.apache.directory.shared.kerberos.codec.apReq.actions.CheckMsgType;
import org.apache.directory.shared.kerberos.codec.apReq.actions.StoreApOptions;
import org.apache.directory.shared.kerberos.codec.apReq.actions.StoreAuthenticator;
import org.apache.directory.shared.kerberos.codec.apReq.actions.StorePvno;
import org.apache.directory.shared.kerberos.codec.apReq.actions.StoreTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApReqGrammar extends AbstractGrammar<ApReqContainer> {
   static final Logger LOG = LoggerFactory.getLogger(ApReqGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<ApReqContainer> instance;

   private ApReqGrammar() {
      this.setName(ApReqGrammar.class.getName());
      super.transitions = new GrammarTransition[ApReqStatesEnum.LAST_AP_REQ_STATE.ordinal()][256];
      super.transitions[ApReqStatesEnum.START_STATE.ordinal()][110] = new GrammarTransition(ApReqStatesEnum.START_STATE, ApReqStatesEnum.AP_REQ_STATE, 110, new ApReqInit());
      super.transitions[ApReqStatesEnum.AP_REQ_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(ApReqStatesEnum.AP_REQ_STATE, ApReqStatesEnum.AP_REQ_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[ApReqStatesEnum.AP_REQ_SEQ_STATE.ordinal()][160] = new GrammarTransition(ApReqStatesEnum.AP_REQ_SEQ_STATE, ApReqStatesEnum.AP_REQ_PVNO_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[ApReqStatesEnum.AP_REQ_PVNO_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(ApReqStatesEnum.AP_REQ_PVNO_TAG_STATE, ApReqStatesEnum.AP_REQ_PVNO_STATE, UniversalTag.INTEGER, new StorePvno());
      super.transitions[ApReqStatesEnum.AP_REQ_PVNO_STATE.ordinal()][161] = new GrammarTransition(ApReqStatesEnum.AP_REQ_PVNO_STATE, ApReqStatesEnum.AP_REQ_MSG_TYPE_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[ApReqStatesEnum.AP_REQ_MSG_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(ApReqStatesEnum.AP_REQ_MSG_TYPE_TAG_STATE, ApReqStatesEnum.AP_REQ_MSG_TYPE_STATE, UniversalTag.INTEGER, new CheckMsgType());
      super.transitions[ApReqStatesEnum.AP_REQ_MSG_TYPE_STATE.ordinal()][162] = new GrammarTransition(ApReqStatesEnum.AP_REQ_MSG_TYPE_STATE, ApReqStatesEnum.AP_REQ_AP_OPTIONS_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[ApReqStatesEnum.AP_REQ_AP_OPTIONS_TAG_STATE.ordinal()][UniversalTag.BIT_STRING.getValue()] = new GrammarTransition(ApReqStatesEnum.AP_REQ_AP_OPTIONS_TAG_STATE, ApReqStatesEnum.AP_REQ_AP_OPTIONS_STATE, UniversalTag.BIT_STRING, new StoreApOptions());
      super.transitions[ApReqStatesEnum.AP_REQ_AP_OPTIONS_STATE.ordinal()][163] = new GrammarTransition(ApReqStatesEnum.AP_REQ_AP_OPTIONS_STATE, ApReqStatesEnum.AP_REQ_TICKET_STATE, 163, new StoreTicket());
      super.transitions[ApReqStatesEnum.AP_REQ_TICKET_STATE.ordinal()][164] = new GrammarTransition(ApReqStatesEnum.AP_REQ_TICKET_STATE, ApReqStatesEnum.LAST_AP_REQ_STATE, 164, new StoreAuthenticator());
   }

   public static Grammar<ApReqContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new ApReqGrammar();
   }
}
