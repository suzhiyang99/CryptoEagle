package org.apache.directory.shared.kerberos.codec.paEncTsEnc;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.paEncTsEnc.actions.PaEncTsEncInit;
import org.apache.directory.shared.kerberos.codec.paEncTsEnc.actions.StorePaTimestamp;
import org.apache.directory.shared.kerberos.codec.paEncTsEnc.actions.StorePaUsec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PaEncTsEncGrammar extends AbstractGrammar<PaEncTsEncContainer> {
   static final Logger LOG = LoggerFactory.getLogger(PaEncTsEncGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<PaEncTsEncContainer> instance;

   private PaEncTsEncGrammar() {
      this.setName(PaEncTsEncGrammar.class.getName());
      super.transitions = new GrammarTransition[PaEncTsEncStatesEnum.LAST_PA_ENC_TS_ENC_STATE.ordinal()][256];
      super.transitions[PaEncTsEncStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(PaEncTsEncStatesEnum.START_STATE, PaEncTsEncStatesEnum.PA_ENC_TS_ENC_STATE, UniversalTag.SEQUENCE, new PaEncTsEncInit());
      super.transitions[PaEncTsEncStatesEnum.PA_ENC_TS_ENC_STATE.ordinal()][160] = new GrammarTransition(PaEncTsEncStatesEnum.PA_ENC_TS_ENC_STATE, PaEncTsEncStatesEnum.PA_ENC_TS_ENC_PA_TIMESTAMP_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[PaEncTsEncStatesEnum.PA_ENC_TS_ENC_PA_TIMESTAMP_TAG_STATE.ordinal()][UniversalTag.GENERALIZED_TIME.getValue()] = new GrammarTransition(PaEncTsEncStatesEnum.PA_ENC_TS_ENC_PA_TIMESTAMP_TAG_STATE, PaEncTsEncStatesEnum.PA_ENC_TS_PA_TIMESTAMP_STATE, UniversalTag.GENERALIZED_TIME, new StorePaTimestamp());
      super.transitions[PaEncTsEncStatesEnum.PA_ENC_TS_PA_TIMESTAMP_STATE.ordinal()][161] = new GrammarTransition(PaEncTsEncStatesEnum.PA_ENC_TS_PA_TIMESTAMP_STATE, PaEncTsEncStatesEnum.PA_ENC_TS_ENC_PA_USEC_TAG_STATE, 161, new CheckNotNullLength());
      super.transitions[PaEncTsEncStatesEnum.PA_ENC_TS_ENC_PA_USEC_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(PaEncTsEncStatesEnum.PA_ENC_TS_ENC_PA_USEC_TAG_STATE, PaEncTsEncStatesEnum.PA_ENC_TS_ENC_PA_USEC_STATE, UniversalTag.INTEGER, new StorePaUsec());
   }

   public static Grammar<PaEncTsEncContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new PaEncTsEncGrammar();
   }
}
