package org.apache.directory.shared.kerberos.codec.kdcRep;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.kdcRep.actions.AddPaData;
import org.apache.directory.shared.kerberos.codec.kdcRep.actions.CheckMsgType;
import org.apache.directory.shared.kerberos.codec.kdcRep.actions.StoreCName;
import org.apache.directory.shared.kerberos.codec.kdcRep.actions.StoreCRealm;
import org.apache.directory.shared.kerberos.codec.kdcRep.actions.StoreEncPart;
import org.apache.directory.shared.kerberos.codec.kdcRep.actions.StorePvno;
import org.apache.directory.shared.kerberos.codec.kdcRep.actions.StoreTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KdcRepGrammar extends AbstractGrammar<KdcRepContainer> {
   static final Logger LOG = LoggerFactory.getLogger(KdcRepGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<KdcRepContainer> instance;

   private KdcRepGrammar() {
      this.setName(KdcRepGrammar.class.getName());
      super.transitions = new GrammarTransition[KdcRepStatesEnum.LAST_KDC_REP_STATE.ordinal()][256];
      super.transitions[KdcRepStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KdcRepStatesEnum.START_STATE, KdcRepStatesEnum.KDC_REP_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[KdcRepStatesEnum.KDC_REP_SEQ_STATE.ordinal()][160] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_SEQ_STATE, KdcRepStatesEnum.KDC_REP_PVNO_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[KdcRepStatesEnum.KDC_REP_PVNO_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_PVNO_TAG_STATE, KdcRepStatesEnum.KDC_REP_PVNO_STATE, UniversalTag.INTEGER, new StorePvno());
      super.transitions[KdcRepStatesEnum.KDC_REP_PVNO_STATE.ordinal()][161] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_PVNO_STATE, KdcRepStatesEnum.KDC_REP_MSG_TYPE_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[KdcRepStatesEnum.KDC_REP_MSG_TYPE_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_MSG_TYPE_TAG_STATE, KdcRepStatesEnum.KDC_REP_MSG_TYPE_STATE, UniversalTag.INTEGER, new CheckMsgType());
      super.transitions[KdcRepStatesEnum.KDC_REP_MSG_TYPE_STATE.ordinal()][162] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_MSG_TYPE_STATE, KdcRepStatesEnum.KDC_REP_PA_DATA_TAG_STATE, 162, new CheckNotNullLength());
      super.transitions[KdcRepStatesEnum.KDC_REP_PA_DATA_TAG_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_PA_DATA_TAG_STATE, KdcRepStatesEnum.KDC_REP_PA_DATA_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[KdcRepStatesEnum.KDC_REP_PA_DATA_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_PA_DATA_STATE, KdcRepStatesEnum.KDC_REP_PA_DATA_STATE, UniversalTag.SEQUENCE, new AddPaData());
      super.transitions[KdcRepStatesEnum.KDC_REP_PA_DATA_STATE.ordinal()][163] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_PA_DATA_STATE, KdcRepStatesEnum.KDC_REP_CREALM_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[KdcRepStatesEnum.KDC_REP_MSG_TYPE_STATE.ordinal()][163] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_MSG_TYPE_STATE, KdcRepStatesEnum.KDC_REP_CREALM_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[KdcRepStatesEnum.KDC_REP_CREALM_TAG_STATE.ordinal()][UniversalTag.GENERAL_STRING.getValue()] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_CREALM_TAG_STATE, KdcRepStatesEnum.KDC_REP_CREALM_STATE, UniversalTag.GENERAL_STRING, new StoreCRealm());
      super.transitions[KdcRepStatesEnum.KDC_REP_CREALM_STATE.ordinal()][164] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_CREALM_STATE, KdcRepStatesEnum.KDC_REP_CNAME_STATE, 164, new StoreCName());
      super.transitions[KdcRepStatesEnum.KDC_REP_CNAME_STATE.ordinal()][165] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_CNAME_STATE, KdcRepStatesEnum.KDC_REP_TICKET_STATE, 165, new StoreTicket());
      super.transitions[KdcRepStatesEnum.KDC_REP_TICKET_STATE.ordinal()][166] = new GrammarTransition(KdcRepStatesEnum.KDC_REP_TICKET_STATE, KdcRepStatesEnum.KDC_REP_ENC_PART_STATE, 166, new StoreEncPart());
   }

   public static Grammar<KdcRepContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new KdcRepGrammar();
   }
}
