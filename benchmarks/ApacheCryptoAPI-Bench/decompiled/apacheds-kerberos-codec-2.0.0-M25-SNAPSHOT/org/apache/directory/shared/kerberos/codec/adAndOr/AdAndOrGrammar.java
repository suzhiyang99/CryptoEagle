package org.apache.directory.shared.kerberos.codec.adAndOr;

import org.apache.directory.api.asn1.actions.CheckNotNullLength;
import org.apache.directory.api.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.api.asn1.ber.grammar.Grammar;
import org.apache.directory.api.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.api.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.kerberos.codec.adAndOr.actions.AdAndOrInit;
import org.apache.directory.shared.kerberos.codec.adAndOr.actions.StoreConditionCount;
import org.apache.directory.shared.kerberos.codec.adAndOr.actions.StoreElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AdAndOrGrammar extends AbstractGrammar<AdAndOrContainer> {
   static final Logger LOG = LoggerFactory.getLogger(AdAndOrGrammar.class);
   static final boolean IS_DEBUG;
   private static Grammar<AdAndOrContainer> instance;

   private AdAndOrGrammar() {
      this.setName(AdAndOrGrammar.class.getName());
      super.transitions = new GrammarTransition[AdAndOrStatesEnum.LAST_AD_AND_OR_STATE.ordinal()][256];
      super.transitions[AdAndOrStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(AdAndOrStatesEnum.START_STATE, AdAndOrStatesEnum.AD_AND_OR_STATE, UniversalTag.SEQUENCE, new AdAndOrInit());
      super.transitions[AdAndOrStatesEnum.AD_AND_OR_STATE.ordinal()][160] = new GrammarTransition(AdAndOrStatesEnum.AD_AND_OR_STATE, AdAndOrStatesEnum.AD_AND_OR_CONDITION_COUNT_TAG_STATE, 160, new CheckNotNullLength());
      super.transitions[AdAndOrStatesEnum.AD_AND_OR_CONDITION_COUNT_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = new GrammarTransition(AdAndOrStatesEnum.AD_AND_OR_CONDITION_COUNT_TAG_STATE, AdAndOrStatesEnum.AD_AND_OR_CONDITION_COUNT_STATE, UniversalTag.INTEGER, new StoreConditionCount());
      super.transitions[AdAndOrStatesEnum.AD_AND_OR_CONDITION_COUNT_STATE.ordinal()][161] = new GrammarTransition(AdAndOrStatesEnum.AD_AND_OR_CONDITION_COUNT_STATE, AdAndOrStatesEnum.AD_AND_OR_ELEMENTS_TAG_STATE, 161, new StoreElements());
   }

   public static Grammar<AdAndOrContainer> getInstance() {
      return instance;
   }

   static {
      IS_DEBUG = LOG.isDebugEnabled();
      instance = new AdAndOrGrammar();
   }
}
