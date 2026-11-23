package org.apache.directory.shared.kerberos.codec.encApRepPart;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.encApRepPart.actions.EncApRepPartInit;
import org.apache.directory.shared.kerberos.codec.encApRepPart.actions.StoreCTime;
import org.apache.directory.shared.kerberos.codec.encApRepPart.actions.StoreCusec;
import org.apache.directory.shared.kerberos.codec.encApRepPart.actions.StoreSeqNumber;
import org.apache.directory.shared.kerberos.codec.encApRepPart.actions.StoreSubKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EncApRepPartGrammar extends AbstractGrammar<EncApRepPartContainer> {
   static final Logger LOG = LoggerFactory.getLogger(EncApRepPartGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<EncApRepPartContainer> instance;

   private EncApRepPartGrammar() {
      this.setName(EncApRepPartGrammar.class.getName());
      super.transitions = new GrammarTransition[EncApRepPartStatesEnum.LAST_ENC_AP_REP_PART_STATE.ordinal()][256];
      super.transitions[EncApRepPartStatesEnum.START_STATE.ordinal()][123] = new GrammarTransition(EncApRepPartStatesEnum.START_STATE, EncApRepPartStatesEnum.ENC_AP_REP_PART_STATE, 123, new EncApRepPartInit());
      super.transitions[EncApRepPartStatesEnum.ENC_AP_REP_PART_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(EncApRepPartStatesEnum.ENC_AP_REP_PART_STATE, EncApRepPartStatesEnum.ENC_AP_REP_PART_SEQ_STATE, UniversalTag.SEQUENCE, new CheckNotNullLength());
      super.transitions[EncApRepPartStatesEnum.ENC_AP_REP_PART_SEQ_STATE.ordinal()][160] = new GrammarTransition(EncApRepPartStatesEnum.ENC_AP_REP_PART_SEQ_STATE, EncApRepPartStatesEnum.ENC_AP_REP_PART_CTIME_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[EncApRepPartStatesEnum.ENC_AP_REP_PART_CTIME_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(EncApRepPartStatesEnum.ENC_AP_REP_PART_CTIME_TAG_STATE, EncApRepPartStatesEnum.ENC_AP_REP_PART_CTIME_STATE, UniversalTag.GENERALIZED_TIME, new StoreCTime());
      super.transitions[EncApRepPartStatesEnum.ENC_AP_REP_PART_CTIME_STATE.ordinal()][161] = new GrammarTransition(EncApRepPartStatesEnum.ENC_AP_REP_PART_CTIME_STATE, EncApRepPartStatesEnum.ENC_AP_REP_PART_CUSEC_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[EncApRepPartStatesEnum.ENC_AP_REP_PART_CUSEC_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(EncApRepPartStatesEnum.ENC_AP_REP_PART_CUSEC_TAG_STATE, EncApRepPartStatesEnum.ENC_AP_REP_PART_CUSEC_STATE, UniversalTag.INTEGER, new StoreCusec());
      super.transitions[EncApRepPartStatesEnum.ENC_AP_REP_PART_CUSEC_STATE.ordinal()][162] = new GrammarTransition(EncApRepPartStatesEnum.ENC_AP_REP_PART_CUSEC_STATE, EncApRepPartStatesEnum.ENC_AP_REP_PART_SUBKEY_STATE, 162, new StoreSubKey());
      super.transitions[EncApRepPartStatesEnum.ENC_AP_REP_PART_CUSEC_STATE.ordinal()][163] = new GrammarTransition(EncApRepPartStatesEnum.ENC_AP_REP_PART_CUSEC_STATE, EncApRepPartStatesEnum.ENC_AP_REP_PART_SEQ_NUMBER_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[EncApRepPartStatesEnum.ENC_AP_REP_PART_SUBKEY_STATE.ordinal()][163] = new GrammarTransition(EncApRepPartStatesEnum.ENC_AP_REP_PART_SUBKEY_STATE, EncApRepPartStatesEnum.ENC_AP_REP_PART_SEQ_NUMBER_TAG_STATE, 163, new CheckNotNullLength());
      super.transitions[EncApRepPartStatesEnum.ENC_AP_REP_PART_SEQ_NUMBER_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(EncApRepPartStatesEnum.ENC_AP_REP_PART_SEQ_NUMBER_TAG_STATE, EncApRepPartStatesEnum.ENC_AP_REP_PART_SEQ_NUMBER_STATE, UniversalTag.INTEGER, new StoreSeqNumber());
   }

   public static Grammar<EncApRepPartContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new EncApRepPartGrammar();
   }
}
