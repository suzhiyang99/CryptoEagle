package org.apache.directory.shared.kerberos.codec.kdcReq;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.kdcReq.actions.AddPaData;
import org.apache.directory.shared.kerberos.codec.kdcReq.actions.CheckMsgType;
import org.apache.directory.shared.kerberos.codec.kdcReq.actions.StoreKdcReqBody;
import org.apache.directory.shared.kerberos.codec.kdcReq.actions.StorePvno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KdcReqGrammar extends AbstractGrammar<KdcReqContainer> {
   static final Logger LOG = LoggerFactory.getLogger(KdcReqGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<KdcReqContainer> instance;

   private KdcReqGrammar() {
      this.setName(KdcReqGrammar.class.getName());
      super.transitions = new GrammarTransition[KdcReqStatesEnum.LAST_KDC_REQ_STATE.ordinal()][256];
      super.transitions[KdcReqStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KdcReqStatesEnum.START_STATE, KdcReqStatesEnum.KDC_REQ_PVNO_TAG_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[KdcReqStatesEnum.KDC_REQ_PVNO_TAG_STATE.ordinal()][161] = new GrammarTransition(KdcReqStatesEnum.KDC_REQ_PVNO_TAG_STATE, KdcReqStatesEnum.KDC_REQ_PVNO_STATE, 161, new CheckNotNullLength());
      super.transitions[KdcReqStatesEnum.KDC_REQ_PVNO_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KdcReqStatesEnum.KDC_REQ_PVNO_STATE, KdcReqStatesEnum.KDC_REQ_MSG_TYPE_TAG_STATE, UniversalTag.INTEGER, new StorePvno());
      super.transitions[KdcReqStatesEnum.KDC_REQ_MSG_TYPE_TAG_STATE.ordinal()][162] = new GrammarTransition(KdcReqStatesEnum.KDC_REQ_MSG_TYPE_TAG_STATE, KdcReqStatesEnum.KDC_REQ_MSG_TYPE_STATE, 162, new CheckNotNullLength());
      super.transitions[KdcReqStatesEnum.KDC_REQ_MSG_TYPE_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KdcReqStatesEnum.KDC_REQ_MSG_TYPE_STATE, KdcReqStatesEnum.KDC_REQ_PA_DATA_OR_REQ_BODY_STATE, UniversalTag.INTEGER, new CheckMsgType());
      super.transitions[KdcReqStatesEnum.KDC_REQ_PA_DATA_OR_REQ_BODY_STATE.ordinal()][163] = new GrammarTransition(KdcReqStatesEnum.KDC_REQ_PA_DATA_OR_REQ_BODY_STATE, KdcReqStatesEnum.KDC_REQ_PA_DATA_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[KdcReqStatesEnum.KDC_REQ_PA_DATA_OR_REQ_BODY_STATE.ordinal()][164] = new GrammarTransition(KdcReqStatesEnum.KDC_REQ_PA_DATA_OR_REQ_BODY_STATE, KdcReqStatesEnum.KDC_REQ_KDC_REQ_BODY_STATE, 164, new StoreKdcReqBody());
      super.transitions[KdcReqStatesEnum.KDC_REQ_PA_DATA_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KdcReqStatesEnum.KDC_REQ_PA_DATA_TAG_STATE, KdcReqStatesEnum.KDC_REQ_PA_DATA_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[KdcReqStatesEnum.KDC_REQ_PA_DATA_SEQ_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KdcReqStatesEnum.KDC_REQ_PA_DATA_SEQ_STATE, KdcReqStatesEnum.KDC_REQ_PA_DATA_STATE, UniversalTag.SEQUENCE, new AddPaData());
      super.transitions[KdcReqStatesEnum.KDC_REQ_PA_DATA_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KdcReqStatesEnum.KDC_REQ_PA_DATA_STATE, KdcReqStatesEnum.KDC_REQ_PA_DATA_STATE, UniversalTag.SEQUENCE, new AddPaData());
      super.transitions[KdcReqStatesEnum.KDC_REQ_PA_DATA_STATE.ordinal()][164] = new GrammarTransition(KdcReqStatesEnum.KDC_REQ_PA_DATA_STATE, KdcReqStatesEnum.KDC_REQ_KDC_REQ_BODY_STATE, 164, new StoreKdcReqBody());
   }

   public static Grammar<KdcReqContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new KdcReqGrammar();
   }
}
